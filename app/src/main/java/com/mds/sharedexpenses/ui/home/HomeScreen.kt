package com.mds.sharedexpenses.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.mds.sharedexpenses.ui.components.CustomActionButton
import com.mds.sharedexpenses.ui.components.HeaderTopBar
import com.mds.sharedexpenses.ui.navigation.Screen
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = HomeViewModel()
) {
    Scaffold(
        topBar = {
            HeaderTopBar("<add title!>")
        },
        floatingActionButton = {
            CustomActionButton(
                imageVector = Icons.Filled.GroupAdd,
                iconContentDescription = "Click to create a new Group",
                text = "Create Group",
                onClick = {viewModel.onAddNewGroupClicked()})
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "home screen :D")
            Button(onClick = {
                navController.navigate(Screen.GroupDetail.createRoute("add-group-id"))
            }) {
                Text(text = "Navigate to group detail")
            }
            Button(onClick = {
                navController.navigate(Screen.Profile.route)
            }) {
                Text(text = "Navigate to profile page")
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SharedExpensesTheme {
        HomeScreen(navController = NavController(context = androidx.compose.ui.platform.LocalContext.current))
    }
}
