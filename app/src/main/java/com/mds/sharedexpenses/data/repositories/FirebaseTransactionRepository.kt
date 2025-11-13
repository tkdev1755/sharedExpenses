package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

class FIRTransactionRepository(private val firebaseRepository : FirebaseRepository){

    fun getGroupTransactionsDirectory(groupID: String) : DatabaseReference{
        val groupDirectory : DatabaseReference = firebaseRepository.getGroupDirectory(groupID)
        return groupDirectory.child("transactions")
    }

    suspend fun getGroupTransactions(group: Group) : Map<String,*>?{
        val  transactionsDirectory : DatabaseReference = getGroupTransactionsDirectory(group.id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(transactionsDirectory)

        if(dataRes is DataResult.Success) {
            val transactionMap : Map<String,*>? = dataRes.data
            return transactionMap
        }
        else{
            return null
        }
    }

    suspend fun addGroupTransaction(group: Group, transaction: Transaction): DataResult<Boolean> {
        val  transactionsDirectory : DatabaseReference = getGroupTransactionsDirectory(group.id)

        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(transactionsDirectory)
        var newTransactionDirectory : DatabaseReference? = null


        if (result is DataResult.Success){
            newTransactionDirectory = result.data
        }
        else{
            return DataResult.Error("FIREBASE_ERROR", "Failed to create transaction child reference.")
        }

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newTransactionDirectory!!, transaction.toJson())

        if(dataRes is DataResult.Success) {
            return dataRes
        }

        return DataResult.Error("FIREBASE_ERROR", "Failed to write to a new transaction database reference.")
    }

    suspend fun removeGroupTransaction(group: Group, transaction: Transaction) {
        val groupExpensesDirectory : DatabaseReference = getGroupTransactionsDirectory(group.id)

        val expenseDirectory : DatabaseReference = groupExpensesDirectory.child(transaction.id.toString())
        firebaseRepository.deleteDBRef(expenseDirectory)
    }
}