package com.mds.sharedexpenses.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.ui.addgroup.AddGroupBottomSheet
import com.mds.sharedexpenses.ui.components.AnimatedBorderCard
import com.mds.sharedexpenses.ui.components.CustomActionButton
import com.mds.sharedexpenses.ui.components.HeaderTopBar
import com.mds.sharedexpenses.ui.components.InstructionCard
import com.mds.sharedexpenses.ui.navigation.Screen
import com.mds.sharedexpenses.ui.authContent.LogInContent
import com.mds.sharedexpenses.ui.authContent.OnboardingContent
import com.mds.sharedexpenses.ui.authContent.SignUpContent
import com.mds.sharedexpenses.ui.authContent.WelcomeContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    notificationAsk: () -> Unit,
    viewModel: HomeViewModel,
) {
    //collects state from viewmodel
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            println("EVENT !!!!")
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updateGroup()

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
            }
        }

    }

    if (uiState.authenticationStep != null) {
        OnboardingSheet(viewModel, notificationAsk,uiState)
    }
    if (uiState.activeSheet == SheetTypeHome.ADD_GROUP){
        AddGroupBottomSheet(onCreateGroup = { name, description ->
            viewModel.createNewGroup(name, description)
        }, onDismiss = { viewModel.onDismissRequest() })
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
                onClick = { viewModel.onAddNewGroupClicked() },
            )
        },
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            groupWithRecentActivity = uiState.groupWithRecentActivity,
            groups = uiState.groups,
            onAddGroupClick = { viewModel.onAddNewGroupClicked() },
            onGroupClick = { group -> viewModel.onGroupClicked(group) },
        )

    }


}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    groupWithRecentActivity: Group?,
    groups: List<Group>,
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Groups",
            style = MaterialTheme.typography.titleLarge,
        )

        if (groups.isEmpty()) {
            InstructionCard(
                title = "No groups found",
                description = "You are currently not in any group. Ask someone to add you to an existing group or start by creating a new group.",
                buttonLabel = "Create Group",
                onButtonClick = { onAddGroupClick() },
                imageId = com.mds.sharedexpenses.R.drawable.group,
            )
        } else {
            groupWithRecentActivity?.let { recentGroup ->
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                )
                AnimatedBorderCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    RecentActivityCard(
                        group = recentGroup,
                        onClick = { onGroupClick(recentGroup) },
                    )
                }
            }

            GroupsSection(
                groups = groups,
                onGroupClick = onGroupClick,
            )
        }
    }
}


@Composable
private fun RecentActivityCard(
    group: Group,
    onClick: () -> Unit,
) {
    val memberNames = group.users.joinToString(", ") { user ->
        user.name.ifEmpty { "Unnamed" }
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = memberNames,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun GroupsSection(
    groups: List<Group>,
    onGroupClick: (Group) -> Unit,
) {
    println("we have ${groups.size} groups")
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(groups) { group ->
            OutlinedCard(
                onClick = { onGroupClick(group) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun OnboardingSheet(
    viewModel: HomeViewModel,
    notificationAsk: () -> Unit,
    uiState: HomeUiState,
){
    Dialog(
        onDismissRequest = { /**/ },
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
        when (uiState.authenticationStep) {
            AuthStep.LOGIN -> LogInContent(
                onLogin = { email,password ->
                    viewModel.onLogin(email,password)
                },
                onCancel = {
                    viewModel.goToAuthStep(AuthStep.WELCOME)
                },
            )

            AuthStep.SIGNUP -> SignUpContent(
                onFinished = { email, password, name, phone ->
                    viewModel.onSignUp(email, password, name, phone)
                },
                onCancel = {
                    viewModel.goToAuthStep(AuthStep.WELCOME)
                }
            )

            AuthStep.ONBOARDING -> OnboardingContent(
                onNotificationActivation = { value ->
                    viewModel.onNotificationActivation(value)
                    notificationAsk
                },
                notifictionState = uiState.notificationStatus,
                onFinish = {
                    viewModel.finishOnboarding()
                },
            )

            AuthStep.WELCOME -> WelcomeContent(
                onLogin = {
                    viewModel.goToAuthStep(AuthStep.LOGIN)
                },
                onSignUp = {
                    viewModel.goToAuthStep(AuthStep.SIGNUP)
                },
            )

            null -> viewModel.finishOnboarding() // but this can basically never be the case
        }
        }
    }
}
