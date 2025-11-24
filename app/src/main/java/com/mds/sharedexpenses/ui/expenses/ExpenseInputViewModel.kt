package com.mds.sharedexpenses.ui.expenses

import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpenseInputUiState(
    val name: String = "",
    val description: String = "",
    val amount: String = "",
    val date: String = "",
    val allPayers: List<String> = emptyList(),
    val selectedPayers: Set<String> = emptySet()
)

sealed class ExpenseInputNavigationEvent {
    object Done : ExpenseInputNavigationEvent()
}

class ExpenseInputViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow(ExpenseInputUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<ExpenseInputNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private var currentGroup: Group? = null
    private var currentGroupId: String? = null


    fun init(groupId: String) {
        if (currentGroup != null) return
        currentGroupId = groupId

        viewModelScope.launch {
            val groupResult = appRepository.groups.getGroupById(groupId)
            if (groupResult is DataResult.Success) {
                val group = groupResult.data
                currentGroup = group

                val payerNames = group.users.map { it.name }
                _uiState.value = _uiState.value.copy(
                    allPayers = payerNames,
                    selectedPayers = payerNames.toSet()
                )
            } else if (groupResult is DataResult.Error) {
                val message = groupResult.errorMessage
                    .orEmpty()
                    .ifEmpty { "Error getting group data" }
                showErrorMessage(message)
            }
        }
    }

    fun onDescriptionChange(newValue: String) {
        _uiState.value = _uiState.value.copy(description = newValue)
    }

    fun onNameChange(newValue:String){
        _uiState.value = _uiState.value.copy(name = newValue)
    }
    fun onAmountChange(newValue: String) {
        _uiState.value = _uiState.value.copy(amount = newValue)
    }

    fun onDateChange(newValue: String) {
        _uiState.value = _uiState.value.copy(date = newValue)
    }

    fun onTogglePayer(name: String) {
        val current = _uiState.value.selectedPayers
        val updated = if (name in current) current - name else current + name
        _uiState.value = _uiState.value.copy(selectedPayers = updated)
    }

    fun onPayersSelected(selected: List<String>) {
        _uiState.value = _uiState.value.copy(selectedPayers = selected.toSet())
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            val state = _uiState.value
            val group = currentGroup
            val groupId = currentGroupId

            if (group == null || groupId == null) {
                showErrorMessage("Group not loaded.")
                return@launch
            }

            if (state.name.isBlank()){
                showErrorMessage("Please enter a name.")
                return@launch
            }

            if (state.description.isBlank()) {
                showErrorMessage("Please enter a description.")
                return@launch
            }

            val amountDouble = state.amount.toDoubleOrNull()
            if (amountDouble == null || amountDouble <= 0) {
                showErrorMessage("Amount must be a valid number.")
                return@launch
            }

            val parsedDate = try {
                if (state.date.isBlank()) LocalDate.now() else LocalDate.parse(state.date)
            } catch (e: Exception) {
                showErrorMessage("Invalid date format.")
                return@launch
            }

            if (state.selectedPayers.isEmpty()) {
                showErrorMessage("Please select at least one participant.")
                return@launch
            }

            val userResult = appRepository.users.getCurrentUserData()
            if (userResult !is DataResult.Success) {
                val msg = (userResult as? DataResult.Error)?.errorMessage
                    .orEmpty()
                    .ifEmpty { "Error getting user data" }
                showErrorMessage(msg)
                return@launch
            }
            val payer: User = userResult.data

            val debtorUsers = mutableListOf<User>()
            for (name in state.selectedPayers) {
                val found = group.users.find { it.name == name }
                if (found != null) debtorUsers.add(found)
            }

            if (debtorUsers.isEmpty()) {
                showErrorMessage("Please select at least one valid participant.")
                return@launch
            }

            val expense = Expense(
                id = "",
                name = state.name,
                payer = payer,
                debtors = debtorUsers,
                amount = amountDouble,
                description = state.description,
                date = parsedDate
            )

            val saveResult = appRepository.expenses.addGroupExpense(group, expense)
            if (saveResult is DataResult.Error) {
                val msg = saveResult.errorMessage
                    .orEmpty()
                    .ifEmpty { "Error saving expense." }
                showErrorMessage(msg)
                return@launch
            }

            _navigationEvents.emit(ExpenseInputNavigationEvent.Done)
        }
    }
}
