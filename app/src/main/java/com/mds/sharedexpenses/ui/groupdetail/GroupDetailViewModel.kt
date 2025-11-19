package com.mds.sharedexpenses.ui.groupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class GroupDetailUiState(
    val group: Group? = null,
    val expenses: List<Expense> = emptyList(),
    val debtStatus: Int
)

class GroupDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // TODO: add group repository
    // private val groupRepository = GroupRepository()

    private val groupId: String = checkNotNull(savedStateHandle["groupId"])

    private val _uiState = MutableSharedFlow<GroupDetailUiState>()
    val uiState = _uiState.asSharedFlow()

    init {
        // group id cant be null, otherwise the function checkNotNull already threw an exception
        loadGroupDetails()

    }

    private fun loadGroupDetails() {
        // TODO: fetch group details from repository
    }


    fun onButtonClicked() {
        println("Button clicked!")
    }
}