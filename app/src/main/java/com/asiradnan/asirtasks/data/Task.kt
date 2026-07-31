package com.asiradnan.asirtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val date: Long?,
    val time: Long?,
    val modificationTime: Long = System.currentTimeMillis()
)
