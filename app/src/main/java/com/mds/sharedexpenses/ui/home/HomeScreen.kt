package com.mds.sharedexpenses.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.auth.LoginScreen
import com.example.auth.OnboardingScreen
import com.example.auth.SignUpScreen
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.ui.components.CustomActionButton
import com.mds.sharedexpenses.ui.components.HeaderTopBar
import com.mds.sharedexpenses.ui.navigation.Screen
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme
import com.mds.sharedexpenses.ui.welcome.WelcomeScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel
) {
    //collects state from viewmodel
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getUserData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.checkLoginStatus()){
            println("[AUTH] - USER IS NOT LOGGED IN SHOWING MODAL")
            viewModel.onDisconnect()
        }
        viewModel.navigationEvents.collect { event ->
            when (event){
                is HomeNavigationEvent.ToGroupDetails ->
                    navController.navigate(Screen.GroupDetail.createRoute((event.groupId)))

                HomeNavigationEvent.ToCreateGroup -> {
                    navController.navigate(Screen.AddGroup.route)
                }
            }
        }

    }
    if (uiState.showLoginSheet) {
        onboardingSheet(viewModel, uiState)
    }
    if (uiState.showGroupAddSheet){

    }
    Scaffold(
        modifier = modifier,
        topBar = {
            HeaderTopBar(title = "Home", { navController.navigate(Screen.Profile.route) })
        },

        floatingActionButton = {
            CustomActionButton(
                imageVector = Icons.Filled.GroupAdd,
                iconContentDescription = "Click to create a new Group",
                text = "Create Group",
                onClick = { viewModel.onAddNewGroupClicked() })
        }
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            groupWithRecentActivity = uiState.groupWithRecentActivity,
            groups = uiState.groups,
            onAddGroupClick = { viewModel.onAddNewGroupClicked() },
            onGroupClick = { group -> viewModel.onGroupClicked(group) }
        )

    }


}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    groupWithRecentActivity: Group?,
    groups: List<Group>,
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Only show recent activity block if ViewModel provided a group
        groupWithRecentActivity?.let { recentGroup ->
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge
            )

            RecentActivityCard(group = recentGroup)
        }

        Text(
            text = "Groups",
            style = MaterialTheme.typography.titleLarge
        )

        GroupsSection(
            groups = groups,
            onAddGroupClick = onAddGroupClick,
            onGroupClick = onGroupClick
        )
    }
}

@Composable
private fun RecentActivityCard(
    group: Group
) {
    val memberNames = group.users.joinToString(", ") { user ->
        user.name.ifEmpty { "Unnamed" }
    }

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
                text = group.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = memberNames,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun GroupsSection(
    groups: List<Group>,
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groups) { group ->
            OutlinedCard(
                onClick = { onGroupClick(group) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun onboardingSheet(
    viewModel : HomeViewModel,
    uiState : HomeUiState
){
    ModalBottomSheet(
        onDismissRequest = { viewModel.onSheetDismiss() },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
    ) {
        when (uiState.currentStep) {
            AuthStep.LOGIN -> LoginScreen(
                onLogin = { email,password ->
                    viewModel.onLogin(email,password)
                },
            )

            AuthStep.SIGNUP -> SignUpScreen(
                onFinished = { email, password,name,phone ->
                    viewModel.onSignUp(email,password,name,phone)
                }
            )

            AuthStep.ONBOARDING -> OnboardingScreen(
                onFinish = {
                    viewModel.finishOnboarding()
                }
            )

            AuthStep.WELCOME -> WelcomeScreen(
                onLogin = {
                    viewModel.goToLogin()
                },
                onSignUp = {
                    viewModel.goToSignUp()
                }
            )
        }
    }
}
