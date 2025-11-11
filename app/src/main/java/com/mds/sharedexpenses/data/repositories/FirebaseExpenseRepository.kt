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
        var newDebtDirectory : DatabaseReference? = null
        // This will give us a database reference like this
        // debts ↳
        //        new_id ↳
        //              **our debt  data will go here**

        // Always check if everything went successfully with the DataResult type
        if (result is DataResult.Success){
            // Now we know that the operation has been successful
            newDebtDirectory = result.data
        }
        else{
            println("Error: Could not create a new child reference in Firebase. $createRefResult")
            return false // Stop execution
        }

        // Now that it's sorted out, we can write the code to "write" our serialized data to the database
        // To serialize means to turn something (like an object in memory) into a sequence of bytes, a string, JSON, XML, or another transportable format.
        // Don't forget that the value that you have to write have to be basic json Object, not types that we have in the app
        val jsonDebt: Map<String,*> =  mapOf("amount" to 20, "description" to "JE") // Here put when available a debt.toJson() for example
        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newDebtDirectory!!, jsonDebt)
        // Now check if the data was successfully written
        dataRes
        if(dataRes is DataResult.Success) {
            println("Success !")
        }

    }
}