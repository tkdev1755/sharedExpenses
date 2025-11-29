package com.mds.sharedexpenses.ui.groupdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.ui.components.CustomActionButton
import com.mds.sharedexpenses.ui.components.NavigationTopBar
import com.mds.sharedexpenses.ui.expenses.ExpenseInputBottomSheet
import java.time.format.DateTimeFormatter

@Composable
fun EditBottomSheet(
    viewModel: GroupDetailViewModel,
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    var nameValue by remember { mutableStateOf(TextFieldValue("")) } //TODO: bind to viewModel
    var descriptionValue by remember { mutableStateOf(TextFieldValue("")) } //TODO: bind to viewModel
    var newMemberEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = nameValue,
            onValueChange = { nameValue = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = descriptionValue,
            onValueChange = { descriptionValue = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
        Text("Group Members", style = TextStyle(fontWeight = FontWeight.Bold))
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            viewModel.members().forEach { member ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(member.name)
                    IconButton(onClick = { viewModel.removeMember(member) }) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Removes",
                        )
                    }
                }
            }
        }
        //TODO: refactor this (this should become a parameter) - but works for now
        if (viewModel.uiState.collectAsState().value.isAddMemberFieldVisible) {
            var newMember by remember { mutableStateOf("") }

            OutlinedTextField(
                value = newMember,
                onValueChange = { newMember = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { viewModel.addMember(newMember) },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Add")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }
        // TODO: I think we dont need that here do we?
        // cause the payers are selected via chips
        Button(onClick = { viewModel.onAddMemberClicked() }) {
            Text("Add Member")
        }
    }
}

@Composable
fun StatsBox(
    amount: Number,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text("You owe", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("$amount€", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
    }
}

fun Int.toOrdinal(): String {
    val suffix = when {
        this % 100 in 11..13 -> "th" // Handles 11th, 12th, 13th
        this % 10 == 1 -> "st"
        this % 10 == 2 -> "nd"
        this % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$this$suffix"
}

@Composable
fun ExpenseRecord(
    expense: Expense,
    currentUser: String,
    debt: Debt?,
    onClickDelete: () -> Unit,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier,
    getAmountOwed: (Expense) -> Double,
    onClickDetail: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 2.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = expense.date.dayOfMonth.toOrdinal(),
            modifier = modifier.weight(1.5F),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = modifier.weight(4.0F),
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
            ) {
                val isExpensePayer = expense.payer.id == currentUser
                val name = if (isExpensePayer) "you" else expense.payer.name
                Text(expense.name)
                Text("$name paid ${expense.amount}€", color = Color.Gray)
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.weight(2.0F),
        ) {
            if (debt != null && debt.user.id != currentUser) {
                Text("You owe", color = Color.Gray)
                Text("${getAmountOwed(expense)}€")
            }
        }
        IconButton(
            onClick = onClickDetail,
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "See details",
            )
        }
    }
}

@Composable
fun ExpenseInfoDialog(
    expense: Expense,
    currentUser: String,
    onDismiss: () -> Unit,
    onNotifyClick: (user: User) -> Unit,
    onPayClicked: (expense: Expense) -> Unit,
    getAmountOwed: (expense: Expense, user: User) -> Double,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text("Expense Details : ${expense.name}")
        },
        text = {
            Column {
                Text("Amount : ${expense.amount}€")
                Spacer(Modifier.height(16.dp))
                Text("Paid by : ${expense.payer.name}")
                Spacer(Modifier.height(16.dp))
                Text("Date : ${expense.date}")
                Spacer(Modifier.height(16.dp))
                if (expense.payer.id == currentUser) {
                    ExpenseInfo(
                        expense = expense,
                        onNotifyClick = onNotifyClick,
                        getAmountOwed = getAmountOwed,
                        modifier = modifier,
                    )
                } else if (expense.debtors.any { it.id == currentUser }) {
                    ExpenseActions(
                        expense = expense,
                        modifier = modifier,
                        onPayClicked = onPayClicked,
                    )
                }
            }

        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}

@Composable
fun ExpenseActions(
    expense: Expense,
    onPayClicked: (expense: Expense) -> Unit,
    modifier: Modifier,
) {
    Button(
        onClick = { onPayClicked(expense) },
    ) {
        Text("Pay back")
    }
}

@Composable
fun ExpenseInfo(
    expense: Expense,
    modifier: Modifier,
    getAmountOwed: (expense: Expense, user: User) -> Double,
    onNotifyClick: (user: User) -> Unit,
) {
    val users = expense.debtors.filter { it.id != expense.payer.id }
    println("ERROR WAS ${users.size}")
    LazyColumn {
        items(users.size) { element ->
            userActions(
                users[element],
                owedAmount = getAmountOwed(expense, users[element]),
                onNotifyClick = onNotifyClick,
            )
        }
    }
}

@Composable
fun userActions(
    user: User,
    owedAmount: Double,
    onNotifyClick: (user: User) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("${user.name}")
        Column {
            Text("Owes you")
            Text("${owedAmount}€")
        }
        IconButton(
            onClick = { onNotifyClick(user) },
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notify user",
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: GroupDetailViewModel,
) {

    val uiState by viewModel.uiState.collectAsState()
    if (uiState.group == null) {
        return
    }
    val groupName = uiState.group?.name.orEmpty()
    val expenses = uiState.expensesByMonth
    val totalOwe = uiState.totalOwed
    val nothingOwed = totalOwe == 0.0
    val isLoading = uiState.isLoading

    Scaffold(
        modifier = modifier,
        topBar = {
            NavigationTopBar(
                title = groupName.ifEmpty { "Group" },
                onNavigateBack = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { viewModel.onEditGroupClicked() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            CustomActionButton(
                imageVector = Icons.Filled.AttachMoney,
                iconContentDescription = "Click to add an expense",
                text = "Add Expense",
                onClick = { viewModel.onAddExpenseClicked() },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(PaddingValues(16.dp)),
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Text("Loading group details...")
                }
            } else {
                StatsBox(totalOwe, { /* show Modal*/ })

                expenses.forEach { (month, entries) ->
                    Text(month, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
                    entries.forEach { entry ->
                        HorizontalDivider()
                        ExpenseRecord(
                            expense = entry.component1(),
                            onClickDelete = { },
                            onClickEdit = { },
                            debt = entry.component2(),
                            modifier = Modifier,
                            currentUser = uiState.currentUser?.id ?: "",
                            getAmountOwed = { expense ->
                                viewModel.getOwedAmountFromUser(
                                    expense,
                                    uiState.currentUser!!,
                                )
                            },
                            onClickDetail = { viewModel.onShowExpenseInfo(entry.component1()) },
                        )
                    }
                }
            }
        }

        if (uiState.activeSheet == SheetType.EDIT_GROUP) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onDismissSheet() },
                sheetState = rememberModalBottomSheetState(),
            ) {
                EditBottomSheet(
                    viewModel,
                    name = uiState.group!!.name,
                    onNameChange = { viewModel.onGroupNameChange(it) },
                    description = uiState.group!!.description,
                    onDescriptionChange = { viewModel.onGroupDescriptionChange(it) },
                    //TODO: investigate: do we need UI state here?
                )
            }
        }
        if (uiState.activeSheet == SheetType.ADD_EXPENSE) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onDismissSheet() },
                sheetState = rememberModalBottomSheetState(),
            ) {
                ExpenseInputBottomSheet(
                    onDismiss = { viewModel.onDismissSheet() },
                    onSave = { viewModel.saveExpense() },
                    description = viewModel.uiState.collectAsState().value.expenseForm.description,
                    onDescriptionChange = viewModel::onExpenseDescriptionChange,
                    amount = viewModel.uiState.collectAsState().value.expenseForm.amount,
                    onAmountChange = viewModel::onExpenseAmountChange,
                    date = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm")
                        .format(viewModel.uiState.collectAsState().value.expenseForm.date),
                    onDateChange = viewModel::onExpenseDateChange,
                    name = viewModel.uiState.collectAsState().value.expenseForm.name,
                    onNameChange = viewModel::onExpenseNameChange,
                    onPayerSelect = viewModel::toggleChip,
                    payersChips = viewModel.uiState.collectAsState().value.expenseForm.chips,
                )
            }
            if (uiState.isAddMemberFieldVisible) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onDismissPayerSelection() },
                    sheetState = rememberModalBottomSheetState(),
                ) {
                    /*PayerSelectionBottomSheet(
                        open = uiState.isPayerSelectionVisible,
                        onDismiss = viewModel::onDismissPayerSelection,
                        onSave = viewModel::onDismissPayerSelection,
                        /*TODO: this is somewhat redundant, the users are updated and saved when they are selected.
                           The save button does basically nothing except for closing the dialog.
                           This is not a bug however, as we want to save the state also if the Bottom Sheet is closed manually.
                           Users assume, that their selection is the latest thing thats saved, also if they dismiss the Modal Sheet by dragging it.*/
                        allPayers = uiState.group!!.users, // TODO: remove "!!"
                        selectedPayers = uiState.expenseForm.selectedPayers,
                        onTogglePayer = viewModel::onExpensePayerToggle
                    )*/
                }

            }
        }
        if (uiState.detailVisible && uiState.selectedExpense != null) {
            ExpenseInfoDialog(
                expense = uiState.selectedExpense!!,
                onDismiss = { viewModel.onDismissDialog() },
                currentUser = uiState.currentUser?.id ?: "",
                onNotifyClick = { user ->
                    viewModel.onNotifyButtonClicked(uiState.selectedExpense!!, user)
                },
                getAmountOwed = viewModel::getOwedAmountFromUser,
                onPayClicked = { expense ->
                    if (uiState.currentUser != null) {
                        viewModel.onPayButtonClicked(expense, uiState.currentUser!!)

                    }
                },
                modifier = modifier,
            )
        }
    }
}
