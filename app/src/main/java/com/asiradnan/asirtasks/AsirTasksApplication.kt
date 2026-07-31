package com.asiradnan.asirtasks

import android.app.Application
import com.asiradnan.asirtasks.data.AppContainer
import com.asiradnan.asirtasks.data.AppDataContainer


class AsirTasksApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}