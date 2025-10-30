package com.mds.sharedexpenses.ui.groupdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mds.sharedexpenses.ui.components.NavigationTopBar
import com.mds.sharedexpenses.ui.home.HomeScreen
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@Composable
fun GroupDetailScreen(
    viewModel: GroupDetailViewModel = GroupDetailViewModel()
) {
    Scaffold(
        topBar = {
            NavigationTopBar(
                title = "Group Name",
                canNavigateBack = true,
                onNavigateBack = { /* viewModel.navigateBack() */ }
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
        GroupDetailScreen()
    }
}