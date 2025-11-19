package com.mds.sharedexpenses.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mds.sharedexpenses.ui.components.CustomActionButton
import com.mds.sharedexpenses.ui.components.HeaderTopBar
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    Scaffold(
        topBar = {
            HeaderTopBar (title = "Home", onProfileClick)},

        floatingActionButton = {
            CustomActionButton(
                imageVector = Icons.Filled.GroupAdd,
                iconContentDescription = "Click to create a new Group",
                text = "Create Group",
                onClick = {viewModel.onAddNewGroupClicked()})
        }
        ) { innerPadding ->
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            onAddGroupClick = onAddGroupClick,
            onGroupClick = onGroupClick
        )

    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    onAddGroupClick: () -> Unit,
    onGroupClick: (String) -> Unit
) {
    val groups = listOf("Grocery Shopping", "Rome", "Spain 2026")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge
        )

        RecentActivityCard(
            title = "Trip to Barcelona",
            subtitle = "Max, Søren, Mette, Emma"
        )

        Text(
            text = "Groups",
            style = MaterialTheme.typography.titleLarge
        )

        GroupsSection(
            groups = groups,
            onGroupClick = onGroupClick,
            onAddGroupClick = onAddGroupClick
        )
    }
}

@Composable
private fun RecentActivityCard(
    title: String,
    subtitle: String
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun GroupsSection(
    groups: List<String>,
    onGroupClick: (String) -> Unit,
    onAddGroupClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groups) { groupName ->
            OutlinedCard(
                onClick = { onGroupClick(groupName) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = groupName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    SharedExpensesTheme {
        HomeScreen(navController = NavController(context = androidx.compose.ui.platform.LocalContext.current))
    }
}
