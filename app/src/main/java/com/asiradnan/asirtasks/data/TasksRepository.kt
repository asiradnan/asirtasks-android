package com.asiradnan.asirtasks.data

import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    fun getAllTasksStream(): Flow<List<Task>>

    fun getTaskStream(id:Int): Flow<Task?>

    suspend fun addTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)
}
