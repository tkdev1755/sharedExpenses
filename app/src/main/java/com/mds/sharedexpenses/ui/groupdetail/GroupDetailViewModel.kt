package com.mds.sharedexpenses.ui.groupdetail

import androidx.lifecycle.ViewModel
import com.mds.sharedexpenses.data.repositories.Expense
import java.time.LocalDate

data class GroupDetailUiState(
    val isLoading: Boolean = true,
    val groupedExpenses: Map<String, List<Expense>> = emptyMap(),
    val errorMessage: String? = null
)
class GroupDetailViewModel(
): ViewModel() {
    fun onButtonClicked(){
        println("Button clicked!")
    }
    fun onExpenseClick(){
        println("Expense clicked!")
    }

    fun groupName(): String {
        return "Group Name"
    }

    fun expenses(): Map<String, List<Expense>> {
        return mapOf(
            "January 2025" to listOf(
                Expense(45.90, "Groceries – Lidl", LocalDate.of(2025, 1, 27)),
                Expense(120.00, "Electricity Bill", LocalDate.of(2025, 1, 10)),
                Expense(15.49, "Coffee – Studenterhuset", LocalDate.of(2025, 1, 3))
            ),
            "February" to listOf(
                Expense(32.10, "Groceries – Rema1000", LocalDate.of(2025, 2, 21)),
                Expense(60.00, "Phone Bill", LocalDate.of(2025, 2, 13)),
                Expense(9.99, "Spotify", LocalDate.of(2025, 2, 7))
            ),
            "March" to listOf(
                Expense(55.70, "Groceries – Føtex", LocalDate.of(2025, 3, 30)),
                Expense(18.00, "Laundry", LocalDate.of(2025, 3, 22)),
                Expense(45.00, "Night out – Lambda", LocalDate.of(2025, 3, 5))
            )
        )
    }

    fun members(): List<String> {
        return listOf("Person 1", "Person 2", "Person 3")
    }

    fun removeMember(member: String) {

    }

    fun navigateBack() {
        TODO("Not yet implemented")
    }
}