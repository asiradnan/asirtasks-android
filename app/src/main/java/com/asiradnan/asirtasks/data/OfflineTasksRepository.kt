package com.asiradnan.asirtasks.data

import kotlinx.coroutines.flow.Flow


class OfflineTasksRepository(private val taskDao: TaskDAO) : TasksRepository {
    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()

    override fun getTaskStream(id:Int): Flow<Task?> = taskDao.getTask(id)

    override suspend fun addTask(task: Task) {
        taskDao.create(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }
}