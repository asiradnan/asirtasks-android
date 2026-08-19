package com.asiradnan.asirtasks.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.asiradnan.asirtasks.AsirTasksApplication
import com.asiradnan.asirtasks.data.Task
import com.asiradnan.asirtasks.network.toEntity
import com.asiradnan.asirtasks.network.toNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.i("SyncWorker", "doWork started")
        val container = (applicationContext as AsirTasksApplication).container
        val taskDao = container.taskDao
        val taskApiService = container.taskApiService

        return withContext(Dispatchers.IO) {
            try {
                // PHASE 1: PULL & COMPARE (Last Write Wins)
                Log.d("SyncWorker", "Fetching remote tasks...")
                val remoteTasks = taskApiService.getTasks()
                Log.d("SyncWorker", "Fetched ${remoteTasks.size} remote tasks")

                val remoteTaskIds = remoteTasks.map { it.uuid }.toSet()

                val localTasks = taskDao.getAllTasksSync()
                val localTasksMap = localTasks.associateBy { it.uuid }
                val tasksToUpdateLocally = mutableListOf<Task>()

                for (remoteTask in remoteTasks) {
                    val local = localTasksMap[remoteTask.uuid]
                    if (local == null || (remoteTask.modificationTime > local.modificationTime && local.isSynced)) {
                        tasksToUpdateLocally.add(remoteTask.toEntity().copy(isSynced = true))
                    }
                }

                if (tasksToUpdateLocally.isNotEmpty()) {
                    taskDao.upsertAll(tasksToUpdateLocally)
                }

                // PHASE 2: PUSH LOCAL CHANGES
                val pendingTasks = taskDao.getUnsyncedTasks()
                Log.d("SyncWorker", "Found ${pendingTasks.size} unsynced tasks to push.")

                for (task in pendingTasks) {
                    if (task.isDeleted) continue
                    try {
                        val networkTask = task.toNetwork()
                        val serverResponse = if (remoteTaskIds.contains(task.uuid)) {
                            taskApiService.updateTask(task.uuid, networkTask)
                        } else {
                            taskApiService.createTask(networkTask)
                        }
                        Log.d("asiradnan", networkTask.toString())
                        Log.d("asiradnan", serverResponse.toString())

                        if (task.uuid != serverResponse.uuid) {
                            Log.d(
                                "asiradnan",
                                "UUID changed from ${task.uuid} to ${serverResponse.uuid}"
                            )
                            taskDao.delete(task)
                            taskDao.insert(
                                serverResponse.toEntity().copy(isSynced = true)
                            )
                        } else {
                            taskDao.update(task.copy(isSynced = true))
                        }
                    } catch (e: Exception) {
                        Log.e("SyncWorker", "Failed to sync task ${task.uuid}: ${e.message}")
                    }
                }

                // PHASE 3: PUSH DELETIONS
                val tasksToDelete = taskDao.getTasksToDelete()
                for (task in tasksToDelete) {
                    try {
                        taskApiService.deleteTask(task.uuid)
                        taskDao.delete(task)
                    } catch (e: Exception) {
                        if (e is HttpException && e.code() == 404) {
                            taskDao.delete(task)
                        }
                        Log.e("SyncWorker", "Failed to delete task ${task.uuid} on server")
                    }
                }

                // PHASE 4: PULL DELETIONS
                for (localTask in localTasks) {
                    if (localTask.isSynced && !remoteTaskIds.contains(localTask.uuid)) taskDao.delete(
                        localTask
                    )
                }
                container.userPreferencesManager.saveLastSyncTime(System.currentTimeMillis())

                Result.success()
            } catch (e: HttpException) {
                if (e.code() == 401 || e.code() == 403) return@withContext Result.failure()
                Result.retry()
            } catch (_: IOException) {
                Result.retry() // Retry on network errors
            } catch (_: Exception) {
                Result.failure() // Permanent failure for unknown errors
            }
        }
    }
}