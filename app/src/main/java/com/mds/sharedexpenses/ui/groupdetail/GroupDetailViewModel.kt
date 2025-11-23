package com.mds.sharedexpenses.ui.groupdetail

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class GroupDetailUiState(
    // Data
    val group: Group? = null,
    val currentUser: User? = null,
    val expensesByMonth: Map<String, List<Expense>> = emptyMap(),
    val totalOwed: Double = 0.0,
    // UI
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    //Sheet
    val activeSheet: SheetType? = null,
    //Form state
    val expenseForm: ExpenseFormState = ExpenseFormState(),
    val isAddMemberFieldVisible: Boolean = false,
)

data class ExpenseFormState(
    val description: String = "",
    val amount: String = "",
    val date: String = "",
    val selectedPayerIds: Set<String> = emptySet(),
    val editingExpenseId: String? = null,
)

enum class SheetType { EDIT_GROUP, ADD_EXPENSE, EDIT_EXPENSE }


class GroupDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val groupId: String = checkNotNull(savedStateHandle["groupId"]) {
        "Group Id is required"
    }
    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState = _uiState.asStateFlow()
    init {
            loadGroupDetails()
    }
    private fun loadGroupDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userDeferred = async { appRepository.users.getCurrentUserData() }
            val groupDeferred = async { appRepository.groups.getGroupById(groupId) }

            val userResult = userDeferred.await()
            val groupResult = groupDeferred.await()

            if (groupResult is DataResult.Success && userResult is DataResult.Success) {
                val group = groupResult.data
                val currentUser = userResult.data

                val (expensesByMonth, totalOwed) = calculateGroupStats(group, currentUser.id)

                _uiState.update {
                    it.copy(
                        group = group,
                        currentUser = currentUser,
                        expensesByMonth = expensesByMonth,
                        totalOwed = totalOwed,
                        isLoading = false
                    )
                }

            } else {
                val errorMsg = (groupResult as? DataResult.Error)?.errorMessage
                    ?: (userResult as? DataResult.Error)?.errorMessage
                    ?: "Failed to load data"
                showErrorMessage(errorMsg)
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }

            }
        }
    private fun calculateGroupStats(
        group: Group,
        currentUserId: String,
    ): Pair<Map<String, List<Expense>>, Double> {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

        val expensesByMonth = group.expenses
            .sortedByDescending { it.date }
            .groupBy { expense -> expense.date.format(formatter) }

        // calculating Owed Amount
        val totalOwed = group.expenses
            .filter { expense ->
                expense.debtors.any { it.id == currentUserId } && expense.payer.id != currentUserId
            }
            .sumOf { expense ->
                val splitCount = expense.debtors.size
                if (splitCount > 0) expense.amount / splitCount else 0.0
            }

        return Pair(expensesByMonth, totalOwed)
    }

    fun onAddMemberClicked(){
        _uiState.value = _uiState.value.copy(isAddMemberFieldVisible = true)
    }
    fun onAddMember(currentGroup : Group ,email:String){
    }

    fun members(): List<User> {
        return _uiState.value.group?.users.orEmpty()
    }

    fun addMember(email:String) {
        if (_uiState.value.group == null){
            showErrorMessage("Group was not loaded correctly")
            return
        }
        viewModelScope.launch {
            val res : DataResult<Boolean>  = appRepository.groups.inviteUser(_uiState.value.group!!,email)
            when (res) {
                is DataResult.Success -> {
                    _uiState.value = _uiState.value.copy(isAddMemberFieldVisible = false)
                }
                is DataResult.Error -> {
                    val message =
                        res.errorMessage.orEmpty().ifEmpty { "Error inviting user" }
                    println("Error while calling cloud function -> inviteUser ${res.errorMessage}")
                    showErrorMessage(message)

                }
                is DataResult.NotFound -> {

                }
            }
        }
    }

    fun removeMember(member: User) {
        if (_uiState.value.group == null){
            showErrorMessage("Group was not loaded correctly")
            return
        }
        viewModelScope.launch {
            val res:DataResult<Boolean> = appRepository.groups.removeGroupUser(_uiState.value.group!!, member)
            if (res is DataResult.Error){
                println("Unable to remove user from group -> ${res.errorMessage}")
                showErrorMessage("Unable to remove user from group")
            }
        }
    }
    fun onEditGroupClicked() {
        _uiState.update { it.copy(activeSheet = SheetType.EDIT_GROUP) }
    }
    fun onAddExpenseClicked() {
        resetExpenseForm()
        _uiState.update { it.copy(activeSheet = SheetType.ADD_EXPENSE) }
    }
    fun onEditExpenseClicked(expenseId: String) {
        val expenseToEdit = _uiState.value.group?.expenses?.find { it.id == expenseId }

        if (expenseToEdit != null) {
            val prefilledForm = ExpenseFormState(
                description = expenseToEdit.description,
                amount = expenseToEdit.amount.toString(), // for input field
                date = expenseToEdit.date.toString(),
                selectedPayerIds = expenseToEdit.debtors.map { it.id }.toSet(),
                editingExpenseId = expenseToEdit.id,
            )


            _uiState.update {
                it.copy(
                    activeSheet = SheetType.EDIT_EXPENSE,
                    expenseForm = prefilledForm,
                )
            }
        } else {
            showErrorMessage("Expense not found")
        }
    }
    private fun resetExpenseForm() {
        _uiState.update {
            it.copy(
                expenseForm = ExpenseFormState(),
            )
        }
    }
    fun onDismissSheet() {
        _uiState.update { it.copy(activeSheet = null) }
        resetExpenseForm()
    }
    fun onExpenseDescriptionChange(newDescription: String) {
        _uiState.update { currentState ->
            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    description = newDescription
                )
            )
        }
    }
    fun onExpenseAmountChange(newAmount: String) {
        _uiState.update { currentState ->
            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    amount = newAmount
                )
            )
        }
    }
    fun onExpenseDateChange(newDate: String) {
        _uiState.update { currentState ->
            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    date = newDate
                )
            )
        }
    }
    fun onGroupTitleChange(newTitle: String) {
        TODO()
    }
    fun onGroupDescriptionChange(newDescription: String) {
        TODO()
    }
    fun onExpensePayerToggle(userId: String) {
        _uiState.update { currentState ->
            val currentSelection = currentState.expenseForm.selectedPayerIds

            val newSelection = if (userId in currentSelection) {
                currentSelection - userId
            } else {
                currentSelection + userId
            }

            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    selectedPayerIds = newSelection
                )
            )
        }
    }
    fun saveExpense() {
        val currentState = _uiState.value
        val currentUser = currentState.currentUser
        val currentGroup = currentState.group

        val amount = currentState.expenseForm.amount.toDoubleOrNull()
        if (currentState.expenseForm.description.isBlank() || amount == null || amount <= 0) {
            showErrorMessage("invalid input")
            return
        }

        if (currentUser == null || currentGroup == null) {
            showErrorMessage("data not loaded")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val newExpense = Expense(
                    id = "", // TODO: check: is this automatically generated?
                    description = currentState.expenseForm.description,
                    amount = amount,
                    payer = currentUser,
                    debtors = currentGroup.users.filter { it.id in currentState.expenseForm.selectedPayerIds }.toMutableList(),
                    date = LocalDate.parse(currentState.expenseForm.date),
                )

                val result = appRepository.expenses.addGroupExpense(currentGroup, newExpense)

                if (result is DataResult.Success) {
                    showErrorMessage("ExpenseAdded (fixme: not an error)")
                    onDismissSheet()
                    loadGroupDetails()
                } else {
                    showErrorMessage("an error occurred")
                }
            } catch (e: Exception) {
                showErrorMessage("an error occurred")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
