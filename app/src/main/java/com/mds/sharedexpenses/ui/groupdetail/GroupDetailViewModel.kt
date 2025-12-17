package com.mds.sharedexpenses.ui.groupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ChipItem(
    val id: User,
    val label: String,
    var isSelected: Boolean = false,
)

data class GroupDetailUiState(
    // Data
    val group: Group? = null,
    val currentUser: User? = null,
    val expensesByMonth: Map<String, List<Pair<Expense, Debt?>>> = emptyMap(),
    val totalOwed: Double = 0.0,
    // UI
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    //Sheet
    val activeSheet: SheetType? = null,
    //Form state
    val expenseForm: ExpenseFormState = ExpenseFormState(),
    val isAddMemberFieldVisible: Boolean = false,
    val isPayerSelectionVisible: Boolean = false,
    // Dialog
    val detailDialogVisible: Boolean = false,
    val selectedExpense: Expense? = null,
    val deleteDialogVisible : Boolean = false,
)

data class ExpenseFormState(
    val id : String? = null,
    val name: String = "",
    val description: String = "",
    val amount: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val selectedUsers: MutableSet<User> = mutableSetOf(),
    var chips: MutableList<ChipItem> = mutableListOf<ChipItem>(),
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
                        isLoading = false,
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
    ): Pair<Map<String, List<Pair<Expense, Debt?>>>, Double> {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

        val expensesByMonth = group.expenses
            .sortedByDescending { it.date }
            .groupBy { expense -> expense.date.format(formatter) }
        val expensesWithDebts: Map<String, List<Pair<Expense, Debt?>>> =
            expensesByMonth.mapValues { (month, expenses) ->
                expenses.map { expense ->

                    val associatedDebt = group.debts.firstOrNull { debt ->
                        debt.expenses.id == expense.id
                    }

                    expense to associatedDebt
                }
            }


        val filterExpenses = group.expenses.filter { expense ->
            expense.debtors.any { it.id == currentUserId } && expense.payer.id != currentUserId
        }


        val totalOwed = group.debts.filter {
            it.debtor == currentUserId
                && it.expenses.payer.id != currentUserId
        }
            .sumOf {
                it.amount
            }


        return Pair(expensesWithDebts, totalOwed)
    }

    fun onAddMemberClicked() {
        _uiState.value = _uiState.value.copy(isAddMemberFieldVisible = true)
    }

    fun onAddMember(currentGroup: Group, email: String) {
    }

    fun members(): List<User> {
        return _uiState.value.group?.users.orEmpty()
    }

    fun addMember(email: String) {
        if (_uiState.value.group == null) {
            showErrorMessage("Group was not loaded correctly")
            return
        }
        viewModelScope.launch {
            val res: DataResult<Boolean> =
                appRepository.groups.inviteUser(_uiState.value.group!!, email)
            when (res) {
                is DataResult.Success -> {
                    _uiState.value = _uiState.value.copy(isAddMemberFieldVisible = false)
                }

                is DataResult.Error -> {
                    val message =
                        res.errorMessage.orEmpty().ifEmpty { "Error inviting user" }
                    println("Error while calling cloud function -> inviteUser ${res.errorMessage} ${res.errorCode}")
                    showErrorMessage(message)

                }

                is DataResult.NotFound -> {

                }
            }
        }
    }

    fun removeMember(member: User) {
        if (_uiState.value.group == null) {
            showErrorMessage("Group was not loaded correctly")
            return
        }
        viewModelScope.launch {
            val res: DataResult<Boolean> =
                appRepository.groups.removeGroupUser(_uiState.value.group!!, member)
            if (res is DataResult.Error) {
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

    fun toggleChip(index: Int) {
        _uiState.update { state ->
            val chips = state.expenseForm.chips.toMutableList()
            val chip = chips[index]

            val newChip = chip.copy(isSelected = !chip.isSelected)
            chips[index] = newChip

            val newSelectedUsers = state.expenseForm.selectedUsers.toMutableSet()
            if (newChip.isSelected) {
                newSelectedUsers.add(newChip.id)
            } else {
                newSelectedUsers.remove(newChip.id)
            }

            state.copy(
                expenseForm = state.expenseForm.copy(
                    chips = chips,
                    selectedUsers = newSelectedUsers,
                ),
            )
        }
    }

    fun onEditExpenseClicked(expenseToEdit: Expense) {
        val prefilledForm = ExpenseFormState(
            name = expenseToEdit.name,
            description = expenseToEdit.description,
            amount = expenseToEdit.amount.toString(), // for input field
            date = expenseToEdit.date,
            selectedUsers = expenseToEdit.debtors.toMutableSet(),
            editingExpenseId = expenseToEdit.id,
        )
        if (_uiState.value.group != null){
            prefilledForm.chips = _uiState.value.group?.users!!.map { ChipItem(it,it.name, isSelected = (expenseToEdit.debtors.any { user -> user.id == it.id })) }.toMutableList()

        }
        _uiState.update {
            it.copy(
                activeSheet = SheetType.EDIT_EXPENSE,
                expenseForm = prefilledForm,
            )
        }
    }

    fun onDeleteExpenseClicked(expenseToRemove:Expense){
        _uiState.update {
            it.copy(
                selectedExpense = expenseToRemove,
                deleteDialogVisible = true,
            )
        }
    }
    fun onDeleteExpenseDismissed(){
        _uiState.update {
            it.copy(
                selectedExpense = null,
                deleteDialogVisible = false
            )
        }
    }

    fun onDeleteExpense(expense:Expense){
        deleteExpense(expense)
        onDeleteExpenseDismissed()
    }
    private fun resetExpenseForm() {
        _uiState.update {
            it.copy(
                expenseForm = ExpenseFormState(),
            )
        }
        if (_uiState.value.group != null) {
            _uiState.value.expenseForm.chips =
                _uiState.value.group?.users!!.map { ChipItem(it, it.name) }.toMutableList()
        }
    }

    fun onDismissSheet() {
        _uiState.update { it.copy(activeSheet = null) }
        resetExpenseForm()
    }

    fun onExpenseNameChange(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    name = newName,
                ),
            )
        }
    }

    fun onExpenseDescriptionChange(newDescription: String) {
        _uiState.update { currentState ->
            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    description = newDescription,
                ),
            )
        }
    }

    fun onExpenseAmountChange(newAmount: String) {
        _uiState.update { currentState ->
            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    amount = newAmount,
                ),
            )
        }
    }

    fun onExpenseDateChange(newDate: String) {
        _uiState.update { currentState ->
            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    date = LocalDateTime.parse(newDate),
                ),
            )
        }
    }

    fun onGroupNameChange(newName: String) {
        val groupToUpdate = _uiState.value.group ?: return
        updateGroup(groupToUpdate.copy(name = newName))
    }

    fun onGroupDescriptionChange(newDescription: String) {
        val groupToUpdate = _uiState.value.group ?: return
        updateGroup(groupToUpdate.copy(description = newDescription))
    }

    private fun updateGroup(newGroupObject: Group) {
        _uiState.update { currentState ->
            currentState.copy(
                group = newGroupObject,
            )
        }
        viewModelScope.launch {
            try {
                appRepository.groups.createGroup(newGroupObject)
            } catch (e: Exception) {
                showErrorMessage("Group update error")
            }
        }
    }
    /*fun onExpensePayerToggle(userId: String) {
        _uiState.update { currentState ->
            val currentSelection = currentState.expenseForm.selectedPayerIds

            val newSelection = if (userId in currentSelection) {
                currentSelection - userId
            } else {
                currentSelection + userId
            }

            currentState.copy(
                expenseForm = currentState.expenseForm.copy(
                    selectedPayerIds = newSelection,
                ),
            )
        }
    }*/


    fun deleteExpense(expense : Expense){
        val currentState = _uiState.value
        val currentGroup = currentState.group
        if (currentGroup == null){
            showErrorMessage("No groups were loaded beforehand")
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {

                val result = appRepository.expenses.removeGroupExpense(currentGroup!!, expense)

                if (result is DataResult.Success) {
                    showErrorMessage("Deleted Expense (fixme: not an error)")
                    loadGroupDetails()
                } else {
                    showErrorMessage("There was an error while deleting this expense")
                }
            } catch (e: Exception) {
                showErrorMessage("an error occurred")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun saveExpense(edit: Boolean=false) {
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
                println("expenseID is ${currentState.expenseForm.editingExpenseId}")
                val newExpense = Expense(
                    id = if (edit) currentState.expenseForm.editingExpenseId!! else "", // indeed this is automatically generated
                    description = currentState.expenseForm.description,
                    name = currentState.expenseForm.name,
                    amount = amount,
                    payer = currentUser,
                    debtors = currentState.expenseForm.selectedUsers.toMutableList(),
                    date = currentState.expenseForm.date,
                )

                val result = appRepository.expenses.addGroupExpense(currentGroup, newExpense, edit=edit)

                if (result is DataResult.Success) {
                    showErrorMessage("${if (edit) "ExpenseUpdated" else "ExpenseAdded"} (fixme: not an error)")
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

    fun onShowExpenseInfo(expense: Expense) {
        _uiState.update {
            it.copy(
                detailDialogVisible = true,
                selectedExpense = expense,
            )
        }
    }

    fun onDismissDialog() {
        _uiState.update {
            it.copy(
                detailDialogVisible = true,
                selectedExpense = null,
            )
        }
    }

    fun onPayButtonClicked(expense: Expense, user: User) {
        val group = uiState.value.group
        if (group == null) {
            showErrorMessage("Group data wasn't fetched correctly")
            return
        }
        val associatedDebt =
            group.debts.filter { it.expenses.id == expense.id && it.debtor == user.id }
        if (associatedDebt.isEmpty()) {
            showErrorMessage("Expense was not found")
            return
        }
        val amount = associatedDebt.sumOf {
            it.amount
        }
        val newTransaction = Transaction(
            "",
            expense,
            amount,
            issuer = user,
            receiver = expense.payer,
        )
        println("Created transaction")
        viewModelScope.launch {
            println("Adding transaction right now")
            val result: DataResult<Boolean> =
                appRepository.transactions.addGroupTransaction(group!!, newTransaction)
            if (result is DataResult.Success) {
                println("Transaction added successfully")
            } else if (result is DataResult.Error) {
                showErrorMessage("${result.errorMessage}")
                println("Transaction was not added : ${result.errorMessage}")
            }
        }
    }

    fun onPayAllButtonClicked(user: User) {
        val group = uiState.value.group
        if (group == null) {
            showErrorMessage("Group data wasn't fetched correctly")
            return
        }
        val associatedDebts =
            group.debts.filter { it.debtor == user.id }
        if (associatedDebts.isEmpty()) {
            showErrorMessage("Expense was not found")
            return
        }

        for(debt in associatedDebts) {
            val amount = debt.amount
            val expense = debt.expenses
            val receiver = expense.payer
            val newTransaction = Transaction(
                "",
                expense,
                amount,
                issuer = user,
                receiver = receiver
            )
            println("Created transaction")
            viewModelScope.launch {
                println("Adding transaction right now")
                val result: DataResult<Boolean> =
                    appRepository.transactions.addGroupTransaction(group!!, newTransaction)
                if (result is DataResult.Success) {
                    println("Transaction added successfully")
                } else if (result is DataResult.Error) {
                    showErrorMessage("${result.errorMessage}")
                    println("Transaction was not added : ${result.errorMessage}")
                }
            }
        }
        println("All transactions added")
    }


    fun onNotifyButtonClicked(expense: Expense, user: User) {
        viewModelScope.launch {
            if (uiState.value.group != null) {
                appRepository.groups.notifyUserFromExpense(
                    uiState.value.group!!,
                    user,
                    expense,
                )
            }

        }
        return
    }

    fun getOwedAmountFromUser(expense: Expense, user: User): Double {
        if (uiState.value.group != null) {
            val group = uiState.value.group!!
            val debts = group.debts.filter { it.expenses.id == expense.id && it.debtor == user.id }
            val transactions = group.transactions.filter {
                it.expense.id == expense.id && it.issuer.id == user.id
            }
            if (debts.size > 1) {
                println("Debts are superior to 1 -> Weird....")
            }
            val amount: Double = debts.sumOf { it.amount }
            return amount
        } else {
            return -1.0;
        }
    }

        fun getDebtTowardPerson(expense: Expense, creditor : User):Double {
            val currentUser = uiState.value.currentUser ?: return 0.0
            if (uiState.value.group != null) {
                val group = uiState.value.group!!
                val debts = group.debts.filter { it.expenses.id == expense.id && it.debtor == currentUser.id && it.user.id == creditor.id}
                if (debts.size > 1) {
                    println("Debts are superior to 1 -> Weird....")
                }
                val amount: Double = debts.sumOf { it.amount }
                return amount
            } else {
                return 0.0;
            }
        }

        fun getDebtAllPerson(group: Group, user: User): List<Pair<User, Double>> {
            val currentUser = uiState.value.currentUser ?: return emptyList()
            val userAmounts = mutableListOf<Pair<User, Double>>()
            for(user in group.users) {
                if(user.id != currentUser.id) {
                    var totalDebt = 0.0
                    for(expense in group.expenses) {
                        totalDebt += getDebtTowardPerson(expense, user)
                    }
                    if (totalDebt != 0.0) {
                        userAmounts.add(Pair(user, totalDebt))
                    }
                }
            }
            return userAmounts
        }



    // Methods for the user Selection
    fun onDismissPayerSelection() {
        _uiState.update { it.copy(isPayerSelectionVisible = false) }
        resetExpenseForm()
    }
    // end: Methods for the user Selection
}
