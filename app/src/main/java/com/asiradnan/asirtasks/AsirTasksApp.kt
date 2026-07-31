package com.asiradnan.asirtasks

import androidx.annotation.StringRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.asiradnan.asirtasks.ui.HomeScreen
import com.asiradnan.asirtasks.ui.TaskAddScreen
import com.asiradnan.asirtasks.ui.TaskEditScreen

enum class AsirTasksScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    TaskAdd(title = R.string.add_new_task),
    TaskEdit(title = R.string.edit_task),
}

@Composable
fun AsirTasksApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AsirTasksScreen.Start.name,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(
                tween(
                    300
                )
            )
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut(
                tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(tween(300))
        }
    ) {
        composable(route = AsirTasksScreen.Start.name) {
            HomeScreen(
                navigateToTaskAdd = { navController.navigate(AsirTasksScreen.TaskAdd.name) },
                navigateToTaskEdit = {
                    navController.navigate("${AsirTasksScreen.TaskEdit.name}/${it}")
                }
            )
        }
        composable(route = AsirTasksScreen.TaskAdd.name) {
            TaskAddScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${AsirTasksScreen.TaskEdit.name}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) {
            TaskEditScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsirTasksTopAppBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateUp: () -> Unit = {},
    title: String,
    showSaveButton: Boolean = false,
    showDeleteButton: Boolean = false,
    onActionClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            if (showSaveButton)
                IconButton(onClick = onActionClick) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = stringResource(R.string.save_task)
                    )
                }
            else if (showDeleteButton)
                IconButton(onClick = onActionClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_task)
                    )
                }

        }
    )
}