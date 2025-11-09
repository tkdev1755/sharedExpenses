package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

/// Follow this convention to write Firebase...Repository class name, FIR
class FIRDebtRepository(private val firebaseRepository : FirebaseRepository) {
    fun getUserDebtDirectory() : DatabaseReference{
        val userDirectory : DatabaseReference = firebaseRepository.getUserDirectory()
        return userDirectory.child("debt")
    }

    /// Example Function for fetching data from the database
    suspend fun getUserDebt() : Map<String,*>?{
        val  debtDirectory : DatabaseReference = getUserDebtDirectory()
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(debtDirectory)
        // Now check if the data was successfully fetched
        if(dataRes is DataResult.Success) {
            val debtMap : Map<String,*>? = dataRes.data
            return debtMap
        }
        else{
            return null
        }
    }


    /// Example Function for writing data from the database
    suspend fun addUserDebt(debt: Debt){
        // Here is a directory where all the debts are stored
        // So this isn't where we are going to write our Debt object,as its a "list of debts"
        val  debtsDirectory : DatabaseReference = getUserDebtDirectory()
        // Luckily we have a function that creates a unique child reference thats under the original reference that we had before
        // So we write
        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(debtsDirectory)
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
            // The function should throw an error at this moment and not continue, because an error happened in the data creation process
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

    suspend fun removeUserDebt(debt: Debt){
        val  debtsDirectory : DatabaseReference = getUserDebtDirectory()
        val debtDirectory : DatabaseReference = debtsDirectory.child(debt.id.toString())
        firebaseRepository.deleteDBRef(debtDirectory)
    }
}



// Add the functions that will communicate with the Firebase