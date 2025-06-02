package com.vlamik.retask.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun ReTaskNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = NavRoutes.TaskList.path) {
        composable(NavRoutes.TaskList.path) {


        }
    }
}