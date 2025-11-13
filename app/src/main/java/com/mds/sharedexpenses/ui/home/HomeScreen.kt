package com.mds.sharedexpenses.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel(),
    onProfileClick: () -> Unit = {},
    onAddGroupClick: () -> Unit = {},
    onGroupClick: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
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

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onAddGroupClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Group")
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    SharedExpensesTheme {
        HomeScreen()
    }
}
