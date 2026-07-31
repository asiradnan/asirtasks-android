package com.asiradnan.asirtasks.data

import android.content.Context


interface AppContainer {
    val tasksRepository: TasksRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val tasksRepository: TasksRepository by lazy {
        OfflineTasksRepository(AsirTasksDatabase.getDatabase(context).taskDao())
    }
}