package com.asiradnan.asirtasks.ui

import android.R.attr.label
import android.R.attr.text
import android.R.attr.top
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.asiradnan.asirtasks.AsirTasksTopAppBar
import com.asiradnan.asirtasks.R
import com.asiradnan.asirtasks.util.getHourFromMillis
import com.asiradnan.asirtasks.util.getMinuteFromMillis
import com.asiradnan.asirtasks.util.toFormattedTime
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun TaskAddScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TaskAddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            AsirTasksTopAppBar(
                title = "Add new task",
                showSaveButton = true,
                onActionClick = {
                    coroutineScope.launch {
                        val saved = viewModel.saveTask()
                        if (saved) {
                            navigateBack()
                        }
                    }
                },
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) { innerPadding ->
        TaskBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            taskDetails = viewModel.taskUiState.taskDetails,
            onValueChange = viewModel::updateUiState
        )
    }
}


@Composable
fun TaskEditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            AsirTasksTopAppBar(
                title = "Edit task",
                onActionClick = {
                    showDeleteConfirmation = true

                },
                showDeleteButton = true,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) { innerPadding ->
        TaskBody(
            taskDetails = viewModel.taskUiState.taskDetails,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onValueChange = {
                viewModel.updateUiState(it)
            },
            isEditing = true
        )
        if (showDeleteConfirmation)
            ConfirmDeletionAlert(
                onDismiss = { showDeleteConfirmation = false},
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.deleteTask()
                        navigateBack()
                    }
                }
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDeletionAlert(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete task") },
        text = { Text("Are you sure you want to delete this task?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBody(
    modifier: Modifier = Modifier,
    taskDetails: TaskDetails,
    onValueChange: (TaskDetails) -> Unit,
    isEditing: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }

        // Initialize states for pickers (fallback to current time if null)
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = taskDetails.date ?: System.currentTimeMillis()
        )
        val timePickerState = rememberTimePickerState(
            initialHour = taskDetails.time?.getHourFromMillis() ?: 12,
            initialMinute = taskDetails.time?.getMinuteFromMillis() ?: 0
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = taskDetails.name,
                onValueChange = { onValueChange(taskDetails.copy(name = it)) },
                placeholder = {
                    Text(text = "Task", style = MaterialTheme.typography.headlineSmall)
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    textDecoration = if (taskDetails.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Separate Rows for Date and Time with Clear Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Date Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (taskDetails.date != null) {
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                                Date(taskDetails.date)
                            )
                        } else {
                            "Select date"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (taskDetails.date != null) {
                        IconButton(onClick = { onValueChange(taskDetails.copy(date = null)) }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear date")
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                // Time Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true }
                        .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = taskDetails.time?.toFormattedTime() ?: "Select time",
                        modifier = Modifier.weight(1f)
                    )
                    if (taskDetails.time != null) {
                        IconButton(onClick = { onValueChange(taskDetails.copy(time = null)) }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear time")
                        }
                    }
                }
            }

            // DATE PICKER DIALOG
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val selectedDate =
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            onValueChange(taskDetails.copy(date = selectedDate))
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // TIME PICKER DIALOG
            if (showTimePicker) {
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            // Calculate milliseconds since midnight: (hours * 3600 + minutes * 60) * 1000
                            val offsetMillis =
                                (timePickerState.hour * 3600L + timePickerState.minute * 60L) * 1000L
                            onValueChange(taskDetails.copy(time = offsetMillis))
                            showTimePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TimePicker(state = timePickerState)
                        }
                    }
                )
            }
        }

        // Mark Completed Button (for editing mode)
        if (isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { onValueChange(taskDetails.copy(isCompleted = !taskDetails.isCompleted)) }) {
                    Text(
                        text = if (taskDetails.isCompleted) stringResource(R.string.mark_uncompleted) else stringResource(
                            R.string.mark_completed
                        )
                    )
                }
            }
        }
    }
}

//@Preview
//@Composable
//private fun TaskAddScreenPreview() {
//    TaskAddScreen(
//        navigateBack = {}
//    )
//}