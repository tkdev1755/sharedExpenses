package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.User


//In Kotlin, a data class is used to represent data objects


interface ExpenseRepositoryInterface {
    fun fromJsonExpense(data: Map<String, Any>?, usersList: List<User>, expenseId: String)
    fun createExpense(expense: Expense)
    fun updateExpense(expense: Expense)
    fun deleteExpense(expense: Expense)
    fun getExpensesByGroup(groupId: String): List<Expense>
}

