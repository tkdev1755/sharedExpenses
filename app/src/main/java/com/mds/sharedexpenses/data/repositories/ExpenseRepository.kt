package com.mds.sharedexpenses.data.repositories

import androidx.annotation.Nullable

//In Kotlin, a data class is used to represent data objects
data class Expense(val id: Int,
                    var payer: String = "",
                    val deptors: List<String> = emptyList(),
                    val amount: Double = 0.0,
                    var name: String = "",
                    var description: String = "") {

    fun amountPerPerson(): Double {
        return if (deptors.isNotEmpty()) amount / deptors.size else 0.0
    }


}

interface ExpenseRepositoryInterface {
    fun createExpense(expense: Expense)
    fun updateExpense(expense: Expense)
    fun deleteExpense(expense: Expense)
    fun getExpensesByGroup(groupId: String): List<Expense>
}


