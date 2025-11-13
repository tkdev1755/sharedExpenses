package com.mds.sharedexpenses.ui.groupdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mds.sharedexpenses.data.repositories.Expense
import com.mds.sharedexpenses.ui.components.NavigationTopBar
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@Composable
fun GroupDetailScreen(
    viewModel: GroupDetailViewModel = GroupDetailViewModel()
) {
    val expenses: Map<String, List<Expense>> = mapOf(
        "January 2025" to listOf(
            Expense(45.90, "Groceries – Lidl"),
            Expense(120.00, "Electricity Bill"),
            Expense(15.49, "Coffee – Studenterhuset")
        ),
        "February" to listOf(
            Expense(32.10, "Groceries – Rema1000"),
            Expense(60.00, "Phone Bill"),
            Expense(9.99, "Spotify")
        ),
        "March" to listOf(
            Expense(55.70, "Groceries – Føtex"),
            Expense(18.00, "Laundry"),
            Expense(45.00, "Night out – Lambda")
        )
    )

    val totalOwe = expenses.values.sumOf { list -> list.sumOf { it.amount } }

    Scaffold(
        topBar = {
            NavigationTopBar(
                title = "Group Name", // TODO: add real title
                canNavigateBack = true,
                onNavigateBack = { /* viewModel.navigateBack() */ }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(PaddingValues(16.dp))
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
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

@Composable
fun ExpenseRecord(
    expense: Expense,
    onClickDelete: () -> Unit,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier) {

    Row (
        modifier = modifier.fillMaxWidth().padding(all = 2.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("19th", modifier = modifier.weight(1.5F), textAlign = TextAlign.Center)
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

@Preview(showBackground = true)
@Composable
fun GroupDetailScreenPreview() {
    SharedExpensesTheme {
        GroupDetailScreen()
    }
}
