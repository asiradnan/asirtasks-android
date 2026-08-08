package com.asiradnan.asirtasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asiradnan.asirtasks.AsirTasksTopAppBar
import com.asiradnan.asirtasks.R
import com.asiradnan.asirtasks.data.Task
import com.asiradnan.asirtasks.util.toFormattedDate
import com.asiradnan.asirtasks.util.toFormattedTime
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToTaskAdd: () -> Unit,
    navigateToTaskEdit: (taskId: String) -> Unit,
    navigateToAuth: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isDarkModePref by viewModel.isDarkMode.collectAsState()
    val effectiveDarkMode = isDarkModePref ?: androidx.compose.foundation.isSystemInDarkTheme()



    Scaffold(
        topBar = {
            AsirTasksTopAppBar(
                title = "Tasks",
                isLoggedIn = isLoggedIn,
                isDarkMode = effectiveDarkMode,
                onToggleTheme = { viewModel.toggleTheme(effectiveDarkMode) },
                onAuthClick = {
                    if (isLoggedIn) viewModel.logout() else navigateToAuth()
                },
                syncStatus = syncStatus,
                onSyncClick = { showSyncDialog = true },
                showMenuIcon = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToTaskAdd,
                modifier = Modifier
                    .padding(8.dp)
                    .size(60.dp)
            ) {
                Icon(
                    Filled.Add,
                    "Floating action button.",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshTasks() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeBody(
                modifier = Modifier.fillMaxSize(),
                taskList = homeUiState.taskList,
                onTaskClick = navigateToTaskEdit,
                onToggleTaskCompletion = { task, isCompleted ->
                    coroutineScope.launch {
                        viewModel.toggleTaskCompletion(task, isCompleted)
                    }
                }
            )
        }
        if (showSyncDialog) {
            SyncStatusDialog(
                status = syncStatus,
                onDismiss = { showSyncDialog = false },
                onLoginClick = { navigateToAuth() }
            )
        }
    }
}


@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    taskList: List<Task>,
    onTaskClick: (taskId: String) -> Unit,
    onToggleTaskCompletion: (task: Task, isCompleted: Boolean) -> Unit
) {
    val completedTaskList = taskList.filter { it.isCompleted }
    val incompletedTaskList = taskList.filter { !it.isCompleted }
    var isCompletedExpanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        if (taskList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(horizontal = 12.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerLow),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_tasks_yet),
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            if (incompletedTaskList.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(vertical = 8.dp)
                    ) {
                        incompletedTaskList.forEach { task ->
                            TaskCard(
                                task = task,
                                modifier = Modifier.clickable { onTaskClick(task.uuid) },
                                onCheckedChange = { isCompleted ->
                                    onToggleTaskCompletion(task, isCompleted)
                                }
                            )
                        }
                    }
                }
            }
            if (completedTaskList.isNotEmpty()) {
                if (incompletedTaskList.isNotEmpty())
                    item { Spacer(Modifier.height(12.dp)) }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { isCompletedExpanded = !isCompletedExpanded }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Completed (${completedTaskList.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                imageVector = if (isCompletedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isCompletedExpanded) "Collapse" else "Expand"
                            )
                        }

                        if (isCompletedExpanded) {
                            completedTaskList.forEach { task ->
                                TaskCard(
                                    task = task,
                                    modifier = Modifier
                                        .clickable { onTaskClick(task.uuid) }
                                        .animateItem(),
                                    onCheckedChange = { isCompleted ->
                                        onToggleTaskCompletion(task, isCompleted)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    modifier: Modifier = Modifier, task: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularCheckbox(
            checked = task.isCompleted,
            onCheckedChange = {
                onCheckedChange(it)
            },
            modifier = Modifier.padding(end = 4.dp)
        )
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = task.name,
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
            )
            if (task.time != null || task.date != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = "time",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = buildString {
                        task.date?.let { append(it.toFormattedDate()) }
                        if (task.date != null && task.time != null) append(", ")
                        task.time?.let { append(it.toFormattedTime()) }
                    }, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    IconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCheckedChange(!checked)
        }) {
        Icon(
            imageVector = if (checked) Filled.Check else Icons.Outlined.Circle,
            contentDescription = if (checked) "Uncheck task" else "Check task",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

//@Preview
//@Composable
//private fun HomeScreenPreview() {
//    HomeScreen(
//        navigateToTaskAdd = { },
//        navigateToTaskEdit = { TODO() },
//    )
//}