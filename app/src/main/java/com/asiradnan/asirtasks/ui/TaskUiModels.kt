package com.asiradnan.asirtasks.ui

import com.asiradnan.asirtasks.data.Task

data class TaskUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)

data class TaskDetails(
    val id: Int = 0,
    val name: String = "",
    val isCompleted: Boolean = false,
    val date: Long? = null,
    val time: Long? = null
)

fun TaskDetails.toTask(): Task = Task(
    id = id,
    name = name,
    isCompleted = isCompleted,
    date = date,
    time = time
)

fun Task.toTaskDetails(): TaskDetails = TaskDetails(
    id = id,
    name = name,
    isCompleted = isCompleted,
    date = date,
    time = time
)