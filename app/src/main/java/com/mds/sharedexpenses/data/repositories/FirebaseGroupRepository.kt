package com.mds.sharedexpenses.data.repositories

// Add the functions that will communicate with the Firebase
import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User

class FirebaseGroupRepository(private val firebaseRepository: FirebaseRepository) {
    fun serializeGroup(group: Group): Map<String, *> {
        return mapOf("id" to group.id, "name" to group.name, "description" to group.description, "users" to group.users, "expenses" to group.expenses, "transactions" to group.transactions, "debts" to group.debts)
    }

    suspend fun deserializeGroup(data: Map<String, *>?): Group? {
        if (data == null) return null
        val usersList = data?.keys?.map { id ->
            User(
                id = id,
                name = "",
                email = "",
                groups = mutableListOf()
            )
        }?.toMutableList() ?: mutableListOf()
        val isOwner = checkOwners(data)
        return Group(
            id = data["id"] as? String ?: return null,
            name = data["name"] as? String ?: "",
            description = data["description"] as? String ?: "",
            users = usersList,
            expenses = data["expenses"] as? Map<String, Boolean> ?: emptyMap(),
            transactions = data["transactions"] as? Map<String, Boolean> ?: emptyMap(),
            isOwner = isOwner
        )
    }

    //Here begins the getters

    //Check if the current user is owner of the group
    fun checkOwners(data: Map<String,*>?): Boolean? {
        val currentUID = firebaseRepository.getCurrentUserUID()
        val usersMap = data?.get("users") as? Map<String, Any> ?: emptyMap()
        val currentUserData = usersMap[currentUID] as? Map<String, Any>
        return currentUserData?.get("owner") as? Boolean ?: false
    }
    //Get the groups according to the current user and return Map<userId, userName>
    suspend fun getUsersByGroup (group_id : String) : Map<String,*>? {
        val groupRepository = firebaseRepository.getGroupDirectory(group_id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(groupRepository)
        if (dataRes is DataResult.Success) {
            val usersMap = dataRes.data["users"] as? Map<String, Any> ?: emptyMap()
            val result = usersMap.mapValues { (_, userData) ->
                val userMap = userData as? Map<String, Any>
                userMap?.get("name") as? String ?: "" }
            return if (result.isEmpty()) null else result
            }
        else{return null}
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
        val jsonGroup : Map<String,*> = serializeGroup(group)
        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newGroupDirectory, jsonGroup)
        if(dataRes is DataResult.Success) {
            return true
        }
        else{
            return false
        }
    }
}