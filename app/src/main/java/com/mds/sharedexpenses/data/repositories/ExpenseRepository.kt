package com.mds.sharedexpenses.data.repositories

import androidx.annotation.Nullable

data class Expense(val id: Int,
                    val payer: String = "",
                    val deptors: List<String> = emptyList(),
                    val amount: Double = 0.0,
                    var name: String = "",
                    var description: String = "")

interface ExpenseRepositoryInterface {
    fun createExpense(expense: Expense)
    fun updateExpense(expense: Expense)
    fun deleteExpense(expense: Expense)
    fun getExpensesByGroup(groupId: String): List<Expense>
}


