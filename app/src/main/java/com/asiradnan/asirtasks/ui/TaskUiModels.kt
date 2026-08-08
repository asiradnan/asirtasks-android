package com.asiradnan.asirtasks.ui

import com.asiradnan.asirtasks.data.Task
import java.util.UUID

data class TaskUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)

data class TaskDetails(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val isCompleted: Boolean = false,
    val date: Long? = null,
    val time: Long? = null
)

fun TaskDetails.toTask(): Task = Task(
    uuid = uuid,
    name = name,
    isCompleted = isCompleted,
    date = date,
    time = time
)

fun Task.toTaskDetails(): TaskDetails = TaskDetails(
    uuid = uuid,
    name = name,
    isCompleted = isCompleted,
    date = date,
    time = time
)