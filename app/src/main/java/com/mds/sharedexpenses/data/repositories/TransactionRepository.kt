package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User

// In Kotlin, a data class is used to represent data objects


interface TransactionRepositoryInterface {
    fun toJson(transaction: Transaction)
    fun fromJsonTransaction(data: Map<String, Any>?, usersList: List<User>, transactionId: String, expensesList: List<Expense>)
    fun getTransactionParticipants(transaction : Transaction)
    fun getTtotalAmountTransaction(group : Group)
    fun GetTransactionBetweenUsers(group : Group, useraId: String, userbId : String)
    fun createTransaction(transaction: Transaction)
    fun deleteTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
}