package com.mds.sharedexpenses.ui.groupdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonPin
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mds.sharedexpenses.data.repositories.Expense
import com.mds.sharedexpenses.ui.components.NavigationTopBar
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

//Sample Data
val sampleExpense : Expense = Expense(12.0, "Sample Expense")


@Composable
fun GroupDetailScreen(
    viewModel: GroupDetailViewModel = GroupDetailViewModel()
) {
    Scaffold(
        topBar = {
            NavigationTopBar(
                title = "Group Name", // TODO: add real title
                canNavigateBack = true,
                onNavigateBack = { /* viewModel.navigateBack() */ }
            )
        }
    ) { innerPadding ->
        // only for quick prototyping
        // replace with LazyList later and dynamically fetch data
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            StatsBox(12, { /* show Modal*/ }, modifier = Modifier)
            MonthHeader("January", 2025)
            ExpenseRecord(sampleExpense, { /* impl */ }, { /* impl */ })
            ExpenseRecord(sampleExpense, { /* impl */ }, { /* impl */ })
        }
        // Lazy List to replace with
//        LazyColumn(modifier = Modifier
//            .fillMaxSize()
//            .padding(innerPadding)
//        ) {
//
//        }
    }
}

@Composable
fun StatsBox(
    amount: Number,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Column {
        Text("Your current balance: $amount")
        Button(onClick = { onButtonClick }) {
            Text(text = "Pay Off")
        }
    }
}

@Composable
fun MonthHeader(
    name: String,
    year: Number,
    modifier: Modifier = Modifier
) {
    Text("$name $year")
}

@Composable
fun ExpenseRecord(
    expense: Expense,
    onClickDelete: () -> Unit,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier) {

    Row {
        Text("19th")
        Row {
            Icon()
            Column {
                Text("Føtex")
                Text("Max paid €11,21")
            }
        }
        Column {
            Text("You owe")
            Text("€ ${expense.amount}")
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