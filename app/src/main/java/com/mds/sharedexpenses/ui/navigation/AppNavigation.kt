package com.mds.sharedexpenses.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mds.sharedexpenses.ui.groupdetail.GroupDetailScreen
import com.mds.sharedexpenses.ui.groupdetail.GroupDetailViewModel
import com.mds.sharedexpenses.ui.home.HomeScreen
import com.mds.sharedexpenses.ui.profile.ProfileScreen


@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            val groupDetailViewModel: GroupDetailViewModel =
                viewModel()
            GroupDetailScreen(
                navController = navController,
                viewModel = groupDetailViewModel
            )
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
}}

