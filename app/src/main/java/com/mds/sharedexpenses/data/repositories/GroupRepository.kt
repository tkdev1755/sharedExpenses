package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User

// In Kotlin, a data class is used to represent data objects


interface GroupRepositoryInterface {
    fun toJsonGroup(group: Group)
    fun fromJsonGroup(data: Map<String, *>?)
    fun checkOwners(data: Map<String,*>?)
    suspend fun notifyUserFromExpense(group:Group ,user:User, expense:Expense)
    suspend fun getGroupById(groupId: String)
    suspend fun getUsersByGroup (group_id : String)
    suspend fun getExpensesForGroup(groupId: String)
    suspend fun getTransactionsbyGroup(groupId : String)
    fun getUserBalanceInGroup(transactions: List<Transaction>, userId: String)
    suspend fun getExpensebyId(groupId: String, expenseId: String)
    suspend fun getExpenseforUser(groupId: String, userId: String)
    suspend fun getTransactionbyId(groupId : String, transactionId : String)
    suspend fun getTransactionsforUser(groupId : String, userId : String)
    suspend fun getTransactionsForExpense(groupId: String, expenseId: String)
    fun getGroupUsersDirectory(groupID: String)
    suspend fun createGroup(group: Group)
    suspend fun addGroupUser(group: Group, user: User)
    suspend fun removeGroupUser(group: Group, user: User)
}
