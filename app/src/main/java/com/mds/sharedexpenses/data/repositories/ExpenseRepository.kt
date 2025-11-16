package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User


//In Kotlin, a data class is used to represent data objects


interface ExpenseRepositoryInterface {
    fun toJsonExpense(expense: Expense)
    fun fromJsonExpense(data: Map<String, Any>?, usersList: List<User>, expenseId: String)
    fun getExpensebyId(expenseId:String, group: Group)
    fun getPayedbyUser(group: Group, userId: String)
    fun getOwnedbyUser(group: Group, userId: String)
    fun getTotalGroupExpenses(group: Group)
    fun getExpensesByPayer(group: Group, payerId : String)
    fun getDebtorsForExpense(group: Group, expenseId : String)
    fun getGroupExpensesDirectory(groupID: String)
    suspend fun getGroupExpenses(group: Group)
    suspend fun addGroupExpense(group: Group, expense: Expense)
    suspend fun removeGroupExpense(group: Group, expense: Expense)
}

