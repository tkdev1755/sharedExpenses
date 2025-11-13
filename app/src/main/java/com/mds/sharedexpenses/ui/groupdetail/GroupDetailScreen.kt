package com.mds.sharedexpenses.ui.groupdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.mds.sharedexpenses.ui.components.NavigationTopBar
import com.mds.sharedexpenses.ui.home.HomeScreen
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@Composable
fun GroupDetailScreen(
    navController: NavController,
    viewModel: GroupDetailViewModel = GroupDetailViewModel()
) {
    Scaffold(
        topBar = {
            NavigationTopBar(
                title = "Group Name",
                canNavigateBack = true,
                onNavigateBack = { navController.navigateUp() } // TODO: move to ViewController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Text("<group detail screen>")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupDetailScreenPreview() {
    SharedExpensesTheme {
        GroupDetailScreen(navController = NavController(context = androidx.compose.ui.platform.LocalContext.current))
    }
}