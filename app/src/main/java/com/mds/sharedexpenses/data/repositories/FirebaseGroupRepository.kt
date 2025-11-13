package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

class FIRGroupRepository(private val firebaseRepository : FirebaseRepository) {

    fun getGroupUsersDirectory(groupID: String) : DatabaseReference{
        val groupDirectory : DatabaseReference = firebaseRepository.getGroupDirectory(groupID)
        return groupDirectory.child("users")
    }

    //requires to have the owner in the users mutable list
    suspend fun createGroup(group: Group): DataResult<Boolean> {
        val groupsDirectory: DatabaseReference = firebaseRepository.getGroupsDirectory()

        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(groupsDirectory)
        var newGroupDirectory : DatabaseReference? = null

        if (result is DataResult.Success){
            newGroupDirectory = result.data
        }
        else{
            return DataResult.Error("FIREBASE_ERROR", "Failed to create group child reference.")
        }

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newGroupDirectory!!, group.toJson())

        if(dataRes is DataResult.Success) {
            if(group.users.size > 0) {
                return addGroupUser(group, group.users.get(0))
            }
            else {
                return DataResult.Error("FUNCTION_PARAMETER_ERROR", "Owner was not added to the users mutable list")
            }
        }

        return DataResult.Error("FIREBASE_ERROR", "Failed to write to a new group database reference.")
    }

    suspend fun addGroupUser(group: Group, user: User): DataResult<Boolean> {
        val groupUserDirection : DatabaseReference = getGroupUsersDirectory(group.id)

        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(groupUserDirection)
        var newGroupUserDirection : DatabaseReference? = null

        if (result is DataResult.Success){
            newGroupUserDirection = result.data
        }
        else{
            return DataResult.Error("FIREBASE_ERROR", "Failed to create user child reference.")
        }

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newGroupUserDirection!!, user.toJson())

        if(dataRes is DataResult.Success) {
            return dataRes
        }

        return DataResult.Error("FIREBASE_ERROR", "Failed to write to a new user database reference.")
    }

    suspend fun removeGroupUser(group: Group, user: User) {
        //call cloud func
    }

}