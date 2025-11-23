package com.mds.sharedexpenses.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
@Deprecated("dont use as screen")
fun ExpenseInputScreen(
    navController: NavController,
    groupId: String,
    viewModel: ExpenseInputViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var payerSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.init(groupId)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                ExpenseInputNavigationEvent.Done -> navController.popBackStack()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ExpenseInputBottomSheet(
            onDismiss = { navController.popBackStack() },
            onSave = { viewModel.onSaveClicked() },
            onOpenPayerSelection = { payerSheetOpen = true },
            description = uiState.description,
            onDescriptionChange = viewModel::onDescriptionChange,
            amount = uiState.amount,
            onAmountChange = viewModel::onAmountChange,
            date = uiState.date,
            onDateChange = viewModel::onDateChange
        )

        PayerSelectionBottomSheet(
            open = payerSheetOpen,
            onDismiss = { payerSheetOpen = false },
            onSave = { selected ->
                viewModel.onPayersSelected(selected)
                payerSheetOpen = false
            },
            allPayers = uiState.allPayers,
            selectedPayers = uiState.selectedPayers,
            onTogglePayer = viewModel::onTogglePayer
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseInputBottomSheet(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onOpenPayerSelection: () -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit
) {

    var datePickerOpen by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Add expense", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
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
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onOpenPayerSelection) {
                    Text("Split equally")
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save expense")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayerSelectionBottomSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit,
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
                .verticalScroll(rememberScrollState())
        ) {
            Text("Select participants", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            allPayers.forEach { name ->
                val checked = name in selectedPayers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .toggleable(
                            value = checked,
                            role = Role.Checkbox,
                            onValueChange = { onTogglePayer(name) }
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = checked, onCheckedChange = null)
                    Spacer(Modifier.width(12.dp))
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onSave(selectedPayers.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save selection")
            }
        }
    }
}

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
