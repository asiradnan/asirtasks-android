package com.asiradnan.asirtasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asiradnan.asirtasks.data.Task
import com.asiradnan.asirtasks.data.TasksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    /**
     * Holds home ui state. The list of tasks are retrieved from [TasksRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        tasksRepository.getAllTasksStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    suspend fun toggleTaskCompletion(task: Task, isCompleted: Boolean) {
        tasksRepository.updateTask(task.copy(isCompleted = isCompleted))
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val taskList: List<Task> = listOf())