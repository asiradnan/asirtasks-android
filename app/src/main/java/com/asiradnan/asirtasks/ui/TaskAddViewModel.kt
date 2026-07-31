package com.asiradnan.asirtasks.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.asiradnan.asirtasks.data.Task
import com.asiradnan.asirtasks.data.TasksRepository

class TaskAddViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    var taskUiState by mutableStateOf(value = TaskUiState())
        private set

    suspend fun saveTask(): Boolean {
        return if (validateInput()) {
            tasksRepository.addTask(taskUiState.taskDetails.toTask())
            true
        } else {
            false
        }
    }

    fun updateUiState(taskDetails: TaskDetails) {
        taskUiState =
            TaskUiState(taskDetails = taskDetails, isEntryValid = validateInput(taskDetails))
    }

    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }
}

