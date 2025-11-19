package com.mds.sharedexpenses.ui.navigation

//listing all screens of the app
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object GroupDetail : Screen("group_detail/{groupId}"){
        // helping function for dynamic rout creation
        fun createRoute(groupId: String) = "group_detail/$groupId"
    }
    object Profile : Screen("profile")
}