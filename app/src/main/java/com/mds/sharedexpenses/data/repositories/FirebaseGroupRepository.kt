package com.mds.sharedexpenses.data.repositories

// Add the functions that will communicate with the Firebase
import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Group

class FirebaseGroupRepository(private val firebaseRepository: FirebaseRepository) {

    //Get the groups according to the current user
    suspend fun getUsersByGroup (group_id : String) : Map<String,*>? {
        val groupRepository = firebaseRepository.getGroupDirectory(group_id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(groupRepository)
        if(dataRes is DataResult.Success) {
            val usersMap : Map<String,*>? = dataRes.data
            return usersMap
        }
        else{
            return null
        }
    }

    //Create a group
    suspend fun createGroup(group : Group) : Boolean {
        val groupsDirectory = firebaseRepository.getGroupsDirectory()
        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(groupsDirectory)
        var newGroupDirectory : DatabaseReference? = null
        if (result is DataResult.Success){
            newGroupDirectory = result.data
        }
        else {
            return false
        }
        val jsonGroup: Map<String,*> =  mapOf("id" to group.id, "name" to group.name, "description" to group.description, "users" to group.users, "expenses" to group.expenses, "transactions" to group.transactions) // Here put when available a debt.toJson() for example
        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newGroupDirectory, jsonGroup)
        if(dataRes is DataResult.Success) {
            return true
        }
        else{
            return false
        }
    }
}