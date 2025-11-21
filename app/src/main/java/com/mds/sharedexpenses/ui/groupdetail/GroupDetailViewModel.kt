package com.mds.sharedexpenses.ui.groupdetail

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class GroupDetailUiState(
    val group: Group? = null,
    val expenses: List<Expense> = emptyList(),
    val debtStatus: Int
)

class GroupDetailViewModel() : BaseViewModel() {
    // private val groupRepository = GroupRepository()

    private val groupId: String = ""

    private val _uiState = MutableSharedFlow<GroupDetailUiState>()
    val uiState = _uiState.asSharedFlow()

    init {
        // group id cant be null, otherwise the function checkNotNull already threw an exception
        loadGroupDetails()
    }

    private fun loadGroupDetails() {
        showErrorMessage("oh no error!")
    }


    fun onButtonClicked() {
        println("Button clicked!")
    }

    fun onExpenseClick() {
        println("Expense clicked!")
    }

    fun groupName(): String {
        return "Group Name"
    }

    fun expenses(): Map<String, List<Expense>> {
        return emptyMap()
    }

    fun members(): List<User> {
        return emptyList()
    }

    fun addMember(member: User) {

    }

    fun removeMember(member: User) {

    }

    fun navigateBack() {
        showErrorMessage("navigateBack not implemented yet")
    }
}
