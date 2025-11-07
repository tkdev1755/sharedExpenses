package com.mds.sharedexpenses.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mds.sharedexpenses.ui.home.HomeScreen


@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(/*navController = navController*/)
        }
        composable(route = Screen.GroupDetail.route) {
            HomeScreen(/*navController = navController*/)
        }
}}

