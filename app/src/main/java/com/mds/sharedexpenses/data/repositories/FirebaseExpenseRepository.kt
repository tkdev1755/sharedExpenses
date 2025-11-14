package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

// Add the functions that will communicate with the Firebase
class FIRExpenseRepository(private val firebaseRepository : FirebaseRepository){

    fun getGroupExpensesDirectory(groupID: String) : DatabaseReference{
        val groupDirectory : DatabaseReference = firebaseRepository.getGroupDirectory(groupID)
        return groupDirectory.child("expenses")
    }

    suspend fun getGroupExpenses(group: Group) : Map<String,*>?{
        val  expensesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(expensesDirectory)

        if(dataRes is DataResult.Success) {
            val expenseMap : Map<String,*>? = dataRes.data
            return expenseMap
        }
        else{
            return null
        }
    }

    suspend fun addGroupExpense(group: Group, expense: Expense): DataResult<Boolean> {
        val expensesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)

        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(expensesDirectory)
        var newExpenseDirectory : DatabaseReference? = null

        if (result is DataResult.Success){
            newExpenseDirectory = result.data
        }
        else{
            return DataResult.Error("FIREBASE_ERROR", "Failed to create expense child reference.")
        }

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newExpenseDirectory!!, expense.toJson())

        if(dataRes is DataResult.Success) {
            return dataRes
        }

        return DataResult.Error("FIREBASE_ERROR", "Failed to write to new expense database reference.")
    }

    suspend fun removeGroupExpense(group: Group, expense: Expense) {
        val groupExpensesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)

        val expenseDirectory : DatabaseReference = groupExpensesDirectory.child(expense.id.toString())
        firebaseRepository.deleteDBRef(expenseDirectory)
    }
}