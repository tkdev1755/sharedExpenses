package com.mds.sharedexpenses.data.repositories

// Add the functions that will communicate with the Firebase
import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Transaction

class FirebaseTransactionRepository(private val firebaseRepository : FirebaseRepository){
    fun getTransactionDirectory() : DatabaseReference {
        val userDirectory : DatabaseReference = firebaseRepository.getUserDirectory()
        return userDirectory.child("transaction")
    }
}
