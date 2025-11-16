package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User


//In Kotlin, a data class is used to represent data objects


interface ExpenseRepositoryInterface {
    fun toJsonExpense(expense: Expense)
    fun fromJsonExpense(data: Map<String, Any>?, usersList: List<User>, expenseId: String)
    fun createExpense(expense: Expense)
    fun updateExpense(expense: Expense)
    fun deleteExpense(expense: Expense)
    fun getExpensesByGroup(groupId: String): List<Expense>
    fun getExpensebyId(expenseId:String, group: Group)
    fun getPayedbyUser(group: Group, userId: String)
    fun getOwnedbyUser(group: Group, userId: String)
    fun getTotalGroupExpenses(group: Group)
    fun getExpensesByPayer(group: Group, payerId : String)
    fun getDebtorsForExpense(group: Group, expenseId : String)
}

