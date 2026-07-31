package com.asiradnan.asirtasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.asiradnan.asirtasks.ui.theme.AsirTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsirTasksTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AsirTasksApp()
                }
            }
        }
    }
}
