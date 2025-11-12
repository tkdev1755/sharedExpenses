package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Transaction

// In Kotlin, a data class is used to represent data objects


interface TransactionRepositoryInterface {
    fun toJson(transaction: Transaction)
    suspend fun fromJson(data: Map<String, Any?>, groupId: String)
    fun createTransaction(transaction: Transaction)
    fun deleteTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
    fun getTransactionbyGroup (transaction: Transaction)
}