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
    // TODO: add group repository
    // private val groupRepository = GroupRepository()

    private val groupId: String = ""

    private val _uiState = MutableSharedFlow<GroupDetailUiState>()
    val uiState = _uiState.asSharedFlow()

    init {
        // group id cant be null, otherwise the function checkNotNull already threw an exception
        loadGroupDetails()
    }

    private fun loadGroupDetails() {
        // TODO: fetch group details from repository
        //on error:
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
//        return mapOf(
//            "January 2025" to listOf(
//                Expense(45.90, "Groceries – Lidl", LocalDate.of(2025, 1, 27)),
//                Expense(120.00, "Electricity Bill", LocalDate.of(2025, 1, 10)),
//                Expense(15.49, "Coffee – Studenterhuset", LocalDate.of(2025, 1, 3))
//            ),
//            "February" to listOf(
//                Expense(32.10, "Groceries – Rema1000", LocalDate.of(2025, 2, 21)),
//                Expense(60.00, "Phone Bill", LocalDate.of(2025, 2, 13)),
//                Expense(9.99, "Spotify", LocalDate.of(2025, 2, 7))
//            ),
//            "March" to listOf(
//                Expense(55.70, "Groceries – Føtex", LocalDate.of(2025, 3, 30)),
//                Expense(18.00, "Laundry", LocalDate.of(2025, 3, 22)),
//                Expense(45.00, "Night out – Lambda", LocalDate.of(2025, 3, 5))
//            )
//        )
        return emptyMap()
    }

    fun members(): List<User> {
//        return listOf("Person 1", "Person 2", "Person 3")
        return emptyList()
    }

    fun addMember(member: User) {

    }

    fun removeMember(member: User) {

    }

    fun navigateBack() {
        TODO("Not yet implemented")
    }
}