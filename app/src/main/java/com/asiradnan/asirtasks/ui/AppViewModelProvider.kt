package com.asiradnan.asirtasks.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asiradnan.asirtasks.AsirTasksApplication
import com.asiradnan.asirtasks.auth.ui.LoginViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                asirTasksApplication().container.tasksRepository,
                authRepository = asirTasksApplication().container.authRepository,
                userPreferencesManager = asirTasksApplication().container.userPreferencesManager,
                workManager = asirTasksApplication().container.workManager,
                connectivityObserver = asirTasksApplication().container.connectivityObserver
            )
        }

        initializer {
            TaskAddViewModel(asirTasksApplication().container.tasksRepository)
        }

        initializer {
            TaskEditViewModel(
                this.createSavedStateHandle(),
                asirTasksApplication().container.tasksRepository
            )
        }

        initializer {
            LoginViewModel(asirTasksApplication().container.authRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.asirTasksApplication(): AsirTasksApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AsirTasksApplication)