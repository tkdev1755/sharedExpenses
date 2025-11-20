package com.mds.sharedexpenses.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mds.sharedexpenses.ui.groupdetail.GroupDetailScreen
import com.mds.sharedexpenses.ui.groupdetail.GroupDetailViewModel
import com.mds.sharedexpenses.ui.home.HomeScreen
import com.mds.sharedexpenses.ui.home.HomeViewModel
import com.mds.sharedexpenses.ui.profile.ProfileScreen
import com.mds.sharedexpenses.ui.profile.ProfileViewModel
import com.mds.sharedexpenses.utils.SnackbarManager


@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = snackbarHostState) {
        SnackbarManager.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            val homeViewModel : HomeViewModel = viewModel()
            HomeScreen(
                navController = navController,
            viewModel = homeViewModel
            )
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
            val profileViewModel : ProfileViewModel = viewModel()
            ProfileScreen(
                navController = navController,
                viewModel = profileViewModel
            )
        }
}}}

