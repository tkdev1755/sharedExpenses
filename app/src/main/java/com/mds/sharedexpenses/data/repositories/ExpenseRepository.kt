package com.mds.sharedexpenses.data.repositories

data class Expense(val amount: Double, val description: String) // TODO: please adopt

interface ExpenseRepositoryInterface {
    fun createExpense(expense: Expense)
    fun updateExpense(expense: Expense)
    fun deleteExpense(expense: Expense)
    fun getExpensesByGroup(groupId: String): List<Expense>
}


