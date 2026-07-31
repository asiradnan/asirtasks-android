package com.asiradnan.asirtasks.ui

import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asiradnan.asirtasks.AsirTasksTopAppBar
import com.asiradnan.asirtasks.R
import com.asiradnan.asirtasks.data.Task
import com.asiradnan.asirtasks.util.toFormattedDate
import com.asiradnan.asirtasks.util.toFormattedTime
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToTaskAdd: () -> Unit,
    navigateToTaskEdit: (taskId: Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AsirTasksTopAppBar(title = "Tasks") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToTaskAdd,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    Filled.Add,
                    "Floating action button.",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            taskList = homeUiState.taskList,
            onTaskClick = navigateToTaskEdit,
            onToggleTaskCompletion = { task, isCompleted ->
                coroutineScope.launch {
                    viewModel.toggleTaskCompletion(task, isCompleted)

                }
            }
        )
    }

}

@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    taskList: List<Task>,
    onTaskClick: (taskId: Int) -> Unit,
    onToggleTaskCompletion: (task: Task, isCompleted: Boolean) -> Unit
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (taskList.isEmpty())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHighest),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_tasks_yet),
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        else {
            val completedTaskList = taskList.filter { it.isCompleted }
            val incompletedTaskList = taskList.filter { !it.isCompleted }
            var isCompletedExpanded by rememberSaveable { mutableStateOf(false) }

            if (incompletedTaskList.isNotEmpty())
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(horizontal = 12.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(items = incompletedTaskList, key = { it.id }) { task ->
                        TaskCard(
                            task = task, modifier = Modifier.clickable { onTaskClick(task.id) },
                            onCheckedChange = { isCompleted ->
                                onToggleTaskCompletion(task, isCompleted)
                            }
                        )
                    }
                }
            if (completedTaskList.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(horizontal = 12.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .clickable { isCompletedExpanded = !isCompletedExpanded }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Completed (${completedTaskList.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (isCompletedExpanded) Icon(
                                Icons.Default.ExpandLess,
                                contentDescription = "Collapse"
                            )
                            else Icon(Icons.Default.ExpandMore, contentDescription = "Expand")
                        }
                    }
                    if (isCompletedExpanded)
                        items(items = completedTaskList, key = { it.id }) { task ->
                            TaskCard(
                                task = task, modifier = Modifier.clickable { onTaskClick(task.id) },
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

//@Composable
//fun CompletedList(
//    modifier: Modifier = Modifier,
//    completedTaskList: List<Task>,
//    onTaskClick: (taskId: Int) -> Unit,
//    onToggleTaskCompletion: (task: Task, isCompleted: Boolean) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(12.dp)
//            .clip(shape = RoundedCornerShape(12.dp))
//            .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
//    ) {
//
//        if (isCompletedExpanded)
//            LazyColumn(
//                modifier = Modifier
//                    .padding(bottom = 12.dp),
//                contentPadding = PaddingValues(bottom = 8.dp)
//            ) {
//                items(items = completedTaskList, key = { it.id }) { task ->
//                    if (task.isCompleted) TaskCard(
//                        task = task, modifier = Modifier.clickable { onTaskClick(task.id) },
//                        onCheckedChange = { isCompleted ->
//                            onToggleTaskCompletion(task, isCompleted)
//                        }
//
//                    )
//                }
//            }
//    }
//
//}

@Composable
fun TaskCard(
    modifier: Modifier = Modifier, task: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                style = MaterialTheme.typography.bodyMedium,
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
    IconButton(
        onClick = { onCheckedChange(!checked) }) {
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