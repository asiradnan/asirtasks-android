package com.asiradnan.asirtasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Query("SELECT * from tasks ORDER BY date IS NULL ASC, date ASC, time ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * from tasks WHERE id = :id")
    fun getTask(id: Int): Flow<Task?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}