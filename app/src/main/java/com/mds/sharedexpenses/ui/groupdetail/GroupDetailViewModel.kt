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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class GroupDetailUiState(
    val group: Group? = null,
    val expensesByMonth: Map<String, List<Expense>> = emptyMap(),
    val totalOwed: Double = 0.0,
    val debtStatus: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isEditSheetVisible: Boolean = false,
    val isExpenseSheetVisible: Boolean = false,
    val expenseDescription: String = "",
    val expenseAmount: String = "",
    val expenseDate: String = LocalDate.now().toString(),
    val isAddMemberFieldVisible: Boolean = false,
)

class GroupDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val groupId: String = savedStateHandle.get<String>("groupId").orEmpty()

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (groupId.isBlank()) {
            if ("groupId" in savedStateHandle.keys()) {
                val message = "Missing group identifier"
                showErrorMessage(message)
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        } else {
            loadGroupDetails()
        }
    }

    private fun loadGroupDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            if (!currentUser.isInitialized){
                appRepository.users.getCurrentUserData()
            }
            val currentUserId = currentUser.value?.id/*when (val userResult = appRepository.users.getCurrentUserData()) {
                is DataResult.Success -> userResult.data.id
                is DataResult.Error -> {
                    val message =
                        userResult.errorMessage.orEmpty().ifEmpty { "Error getting user data" }
                    showErrorMessage(message)
                    null
                }

                is DataResult.NotFound -> {
                    showErrorMessage("User not found")
                    null
                }
            }*/
            when (val groupResult = appRepository.groups.getGroupById(groupId)) {
                is DataResult.Success -> {
                    val group = groupResult.data
                    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                    val expensesByMonth = group.expenses.sortedByDescending { it.date }
                        .groupBy { expense -> expense.date.format(formatter) }

                    val totalOwed = currentUserId?.let { userId ->
                        group.expenses.filter { expense -> expense.debtors.any { debtor -> debtor.id == userId } }
                            .sumOf { expense ->
                                val shareCount = expense.debtors.size.takeIf { it > 0 } ?: 1
                                expense.amount / shareCount
                            }
                    } ?: 0.0

                    _uiState.value = GroupDetailUiState(
                        group = group,
                        expensesByMonth = expensesByMonth,
                        totalOwed = totalOwed,
                        debtStatus = if (totalOwed == 0.0) 0 else 1,
                        isLoading = false,
                        errorMessage = null,
                    )
                }

                is DataResult.Error -> {
                    val message =
                        groupResult.errorMessage.orEmpty().ifEmpty { "Error loading group data" }
                    showErrorMessage(message)
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }

                is DataResult.NotFound -> {
                    showErrorMessage("Group not found")
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onAddMemberClicked(){
        _uiState.value = _uiState.value.copy(isAddMemberFieldVisible = true)
    }
    fun onAddMember(currentGroup : Group ,email:String){
    }
    fun onButtonClicked() {
        println("Button clicked!")
    }

    fun onExpenseClick() {
        println("Expense clicked!")
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

    fun navigateBack() {
        showErrorMessage("navigateBack not implemented yet")
    }

    fun onEditGroupClicked() {
        _uiState.update { it.copy(isEditSheetVisible = true) }
    }

    fun onDismissEditSheet() {
        _uiState.update { it.copy(isEditSheetVisible = false) }
    }

    fun onAddExpenseClicked() {
        _uiState.update { it.copy(isExpenseSheetVisible = true) }
    }

    fun onDismissExpenseSheet() {
        _uiState.update { it.copy(isExpenseSheetVisible = false) }
        resetExpenseFields()
    }

    // new changes


    fun onExpenseDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(expenseDescription = newDescription) }
    }

    fun onExpenseAmountChange(newAmount: String) {
        _uiState.update { it.copy(expenseAmount = newAmount) }
    }

    fun onExpenseDateChange(newDate: String) {
        _uiState.update { it.copy(expenseDate = newDate) }
    }


    fun saveExpense() {
        val currentState = _uiState.value
        val amount = currentState.expenseAmount.toDoubleOrNull()
        val description = currentState.expenseDescription

        if (description.isBlank() || amount == null || amount <= 0) {
            showErrorMessage("Please enter a valid description and amount.")
            return
        }

        if (!currentUser.isInitialized && (appRepository.checkLoginStatus())) {
            viewModelScope.launch {

                val userResult = appRepository.users.getCurrentUserData()
                appRepository.groups.getGroupById()

                if (userResult is DataResult.Success) {

                    val user = userResult.data
                    try {
                        val newExpense = Expense(
                            id = "",
                            description = description,
                            amount = amount,
                            payer = user,
                            debtors = mutableListOf(), // TODO: add logic
                            date = LocalDate.parse(currentState.expenseDate),
                        )
                        appRepository.expenses.addGroupExpense(
                            group = "id", //TODO: add correct group object
                            expense = newExpense,
                        )
                        showErrorMessage("Expense added successfully!")
                        onDismissExpenseSheet() // Schließt das Sheet nach Erfolg
                        resetExpenseFields()


                    } catch (e: Exception) {
                        handleException(e, "Failed to add expense.")
                    }

                }

            }
        }
    }


    private fun resetExpenseFields() {
        _uiState.update {
            it.copy(
                expenseDescription = "",
                expenseAmount = "",
                expenseDate = LocalDate.now().toString(),
            )
        }
    }

}

// end new changes


@VisibleForTesting
    internal fun setPreviewState(uiState: GroupDetailUiState) {
        _uiState.value = uiState
    }
