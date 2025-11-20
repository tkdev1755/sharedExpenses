package com.mds.sharedexpenses.ui.groupdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mds.sharedexpenses.data.repositories.Expense
import com.mds.sharedexpenses.ui.components.NavigationTopBar
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@Composable
fun EditBottomSheet(viewModel: GroupDetailViewModel) {
    var nameValue by remember { mutableStateOf(TextFieldValue("")) }
    var descriptionValue by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = nameValue,
            onValueChange = { nameValue = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descriptionValue,
            onValueChange = { descriptionValue = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
        Text("Group Members", style = TextStyle(fontWeight = FontWeight.Bold))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            viewModel.members().forEach { member ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(member)
                    IconButton(onClick = { viewModel.removeMember(member) }) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Removes",
                        )
                    }
                }
            }
        }
        Button(onClick = {}) {
            Text("Add Member")
        }
    }
}

@Composable
fun StatsBox(
    amount: Number,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Row (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("You owe €$amount")
        Button(onClick = { onButtonClick }) {
            Text(text = "Pay Off")
        }
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
    onClickDelete: () -> Unit,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 2.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = expense.date.dayOfMonth.toOrdinal(), modifier = modifier.weight(1.5F), textAlign = TextAlign.Center)
        Row (
            modifier = modifier.weight(4.0F)
        ) {
            Column (
                modifier = modifier.fillMaxWidth()
            ) {
                Text(expense.description)
                Text("Max paid €11,21", color = Color.Gray)
            }
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.weight(2.0F)
        ) {
            Text("You owe", color = Color.Gray)
            Text("€${expense.amount}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    navController: NavController,
    viewModel: GroupDetailViewModel = GroupDetailViewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val expenses = viewModel.expenses()
    val totalOwe = expenses.values.sumOf { list -> list.sumOf { it.amount } }

    Scaffold(
        topBar = {
            NavigationTopBar(
                title = viewModel.groupName(),
                onNavigateBack = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(PaddingValues(16.dp))
        ) {
            StatsBox(totalOwe, { /* show Modal*/ }, modifier = Modifier)

            expenses.forEach { (month, entries) ->
                Text(month, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
                entries.forEach { entry ->
                    HorizontalDivider()
                    ExpenseRecord(
                        expense = entry,
                        onClickDelete = { },
                        onClickEdit = { },
                        modifier = Modifier
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) { EditBottomSheet(viewModel) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupDetailPreview() {
    SharedExpensesTheme {
        GroupDetailScreen(
            navController = NavController(context = LocalContext.current)
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)

    SharedExpensesTheme {
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight()
        ) {
            EditBottomSheet(viewModel = GroupDetailViewModel())
        }
    }
}