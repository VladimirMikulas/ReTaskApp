package com.vlamik.retask.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vlamik.retask.features.tasklist.TaskListScreen


@Composable
fun ReTaskNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = NavRoutes.TaskList.path) {
        composable(NavRoutes.TaskList.path) {
            TaskListScreen(
                hiltViewModel(),
                navigateToTaskDetail = { id ->
                    navController.navigate(NavRoutes.TaskDetails.build(id.toString()))
                }
            )
        }
    }
}
