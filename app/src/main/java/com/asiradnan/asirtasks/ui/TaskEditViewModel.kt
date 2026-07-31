package com.asiradnan.asirtasks.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asiradnan.asirtasks.data.TasksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class TaskEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val taskId: Int = checkNotNull(savedStateHandle["taskId"])

    var taskUiState by mutableStateOf(TaskUiState())
        private set

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            tasksRepository.getTaskStream(taskId)
                .filterNotNull()
                .first()
                .let { task ->
                    taskUiState =
                        TaskUiState(taskDetails = task.toTaskDetails(), isEntryValid = true)
                }
        }
    }

    suspend fun updateTask() {
        if (validateInput(taskUiState.taskDetails))
            tasksRepository.updateTask(taskUiState.taskDetails.toTask())
    }

    suspend fun deleteTask() {
        tasksRepository.deleteTask(taskUiState.taskDetails.toTask())
    }

    /**
     * Updates the [taskUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(taskDetails: TaskDetails) {
        taskUiState = TaskUiState(
            taskDetails = taskDetails,
            isEntryValid = validateInput(taskDetails)
        )

        saveJob?.cancel()

        saveJob = viewModelScope.launch {
            delay(400.milliseconds)
            updateTask()
        }
    }


    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }
}
