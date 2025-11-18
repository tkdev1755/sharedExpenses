package com.mds.sharedexpenses.ui.groupdetail

import androidx.lifecycle.ViewModel
import com.mds.sharedexpenses.data.models.Expense

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


}