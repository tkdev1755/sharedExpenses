package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

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
}



// Add the functions that will communicate with the Firebase