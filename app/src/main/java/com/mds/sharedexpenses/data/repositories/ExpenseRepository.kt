package com.mds.sharedexpenses.data.repositories

import java.time.LocalDate

data class Expense(
    val amount: Double,
    val description: String,
    val date: LocalDate
)

interface ExpenseRepositoryInterface {
    fun createExpense(expense: Expense)
    fun updateExpense(expense: Expense)
    fun deleteExpense(expense: Expense)
    fun getExpensesByGroup(groupId: String): List<Expense>
}


