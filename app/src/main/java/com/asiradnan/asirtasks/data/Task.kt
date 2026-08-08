package com.asiradnan.asirtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val isCompleted: Boolean = false,
    val date: Long?,
    val time: Long?,
    val modificationTime: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
