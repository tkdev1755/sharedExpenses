package com.mds.sharedexpenses.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseInputBottomSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    onSave: (description: String, amount: String, date: String, selectedPayers: List<String>) -> Unit,
    onOpenPayerSelection: () -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit
) {
    if (!open) return
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {

        var datePickerOpen by remember { mutableStateOf(false) }

            Text("Add expense", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                trailingIcon = {
                    IconButton(onClick = { /* action later */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add attachment")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                label = { Text("Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerOpen = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (datePickerOpen) {
                DatePickerModal(
                    onDateSelected = { millis ->
                        millis?.let {
                            val localDate = java.time.Instant.ofEpochMilli(it)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChange(localDate.toString())
                        }
                    },
                    onDismiss = { datePickerOpen = false }
                )
            }


            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onOpenPayerSelection) {
                    Text("Split equally")
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("dismiss")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onSave(description, amount, date, emptyList()) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("save")
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayerSelectionBottomSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    onSave: (selected: List<String>) -> Unit,
    allPayers: List<String>,
    selectedPayers: Set<String>,
    onTogglePayer: (String) -> Unit
) {
    if (!open) return
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Select payers", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            allPayers.forEach { name ->
                val checked = name in selectedPayers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = checked,
                            role = Role.Checkbox,
                            onValueChange = { onTogglePayer(name) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = checked, onCheckedChange = null)
                    Spacer(Modifier.width(12.dp))
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp)
                ) {
                    Text("dismiss")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onSave(selectedPayers.toList()) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("save")
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

//DATE PICKER -----------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
private fun DatePickerModalPreview() {
    SharedExpensesTheme {
        DatePickerModal(
            onDateSelected = {},
            onDismiss = {}
        )
    }
}

// -------------------------------------------------------------------
// PREVIEW BOTTOM SHEETS
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ExpenseFlowPreview() {
    SharedExpensesTheme {
        var openExpense by remember { mutableStateOf(true) }
        var openPayers by remember { mutableStateOf(false) }

        var description by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("") }

        val all = listOf("Max", "Emma", "Lars", "Joe")
        var selected by remember { mutableStateOf(setOf("Max", "Emma", "Joe")) }

        Box(Modifier.fillMaxSize()) {
            ExpenseInputBottomSheet(
                open = openExpense,
                onDismiss = { openExpense = false },
                onSave = { _, _, _, _ -> openExpense = false },
                onOpenPayerSelection = { openPayers = true },
                description = description,
                onDescriptionChange = { description = it },
                amount = amount,
                onAmountChange = { amount = it },
                date = date,
                onDateChange = { date = it }
            )

            PayerSelectionBottomSheet(
                open = openPayers,
                onDismiss = { openPayers = false },
                onSave = { sel -> selected = sel.toSet().also { openPayers = false } },
                allPayers = all,
                selectedPayers = selected,
                onTogglePayer = { name ->
                    selected = if (name in selected) selected - name else selected + name
                }
            )
        }
    }
}

