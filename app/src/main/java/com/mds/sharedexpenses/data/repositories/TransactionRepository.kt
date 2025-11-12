package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Transaction

// In Kotlin, a data class is used to represent data objects


interface TransactionRepositoryInterface {
    fun serialize(transaction: Transaction)
    suspend fun deserialize(data: Map<String, Any?>, groupId: String)
    fun createTransaction(transaction: Transaction)
    fun deleteTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
    fun getTransactionbyGroup (transaction: Transaction)
}