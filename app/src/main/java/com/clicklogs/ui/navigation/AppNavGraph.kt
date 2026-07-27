package com.clicklogs.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.clicklogs.data.repository.ClickLogsRepository
import com.clicklogs.ui.addtask.AddTaskScreen
import com.clicklogs.ui.dashboard.DashboardScreen
import com.clicklogs.ui.reports.ReportsScreen

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object AddTask : Screen("add_task")
    data object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: String) = "edit_task/$taskId"
    }
    data object Reports : Screen("reports")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    repository: ClickLogsRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                repository = repository,
                onNavigateToAddTask = { navController.navigate(Screen.AddTask.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }
        composable(Screen.AddTask.route) {
            AddTaskScreen(
                repository = repository,
                onTaskSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(Screen.EditTask.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            AddTaskScreen(
                repository = repository,
                editTaskId = taskId,
                onTaskSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(Screen.Reports.route) {
            ReportsScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
