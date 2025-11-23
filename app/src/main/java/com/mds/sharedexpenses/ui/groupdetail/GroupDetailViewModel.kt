package com.mds.sharedexpenses.ui.groupdetail

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

            val currentUserId = when (val userResult = appRepository.users.getCurrentUserData()) {
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
            }

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

    fun onButtonClicked() {
        println("Button clicked!")
    }

    fun onExpenseClick() {
        println("Expense clicked!")
    }

    fun members(): List<User> {
        return _uiState.value.group?.users.orEmpty()
    }

    fun addMember(member: User) {
        // TODO: Implement member invite flow
    }

    fun removeMember(member: User) {
        // TODO: Implement member removal flow
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
    }

    @VisibleForTesting
    internal fun setPreviewState(uiState: GroupDetailUiState) {
        _uiState.value = uiState
    }
}
