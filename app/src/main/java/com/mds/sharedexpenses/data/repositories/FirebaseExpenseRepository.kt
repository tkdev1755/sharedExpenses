package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Debt
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
        val  expansesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(expansesDirectory)

        if(dataRes is DataResult.Success) {
            val debtMap : Map<String,*>? = dataRes.data
            return debtMap
        }
        else{
            return null
        }
    }

    suspend fun addGroupExpense(group: Group, expense: Expense){
        val expansesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)

        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(expansesDirectory)
        var newExpenseDirectory : DatabaseReference? = null


        if (result is DataResult.Success){
            newExpenseDirectory = result.data
        }
        else{
            throw Exception("Error: Could not create a new Expense child reference in Firebase. $result")
        }

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newExpenseDirectory!!, expense.toJson())

        if(dataRes is DataResult.Success) {
            println("Success !")
        }
        else{
            println("Fail !")
        }
    }

    suspend fun removeGroupExpense(group: Group, expense: Expense){
        val groupExpansesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)

        val expenseDirectory : DatabaseReference = groupExpansesDirectory.child(expense.id.toString())
        firebaseRepository.deleteDBRef(expenseDirectory)
    }
}