package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

class FIRUserRepository(private val firebaseRepository : FirebaseRepository){

    suspend fun addUser(user: User): DataResult<Boolean> {
        val  userDirectory : DatabaseReference = firebaseRepository.getUserDirectory()

        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(userDirectory)
        var newUserDirectory : DatabaseReference? = null


        if (result is DataResult.Success){
            newUserDirectory = result.data
        }
        else{
            return DataResult.Error("FIREBASE_ERROR", "Failed to create user child reference.")
        }

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newUserDirectory!!, user.toJson())

        if(dataRes is DataResult.Success) {
            return dataRes
        }

        return DataResult.Error("FIREBASE_ERROR", "Failed to write to a new user database reference.")
    }

    suspend fun removeUSer(group: Group) {
        val userDirectory : DatabaseReference = firebaseRepository.getUserDirectory()

        firebaseRepository.deleteDBRef(userDirectory)
    }
}