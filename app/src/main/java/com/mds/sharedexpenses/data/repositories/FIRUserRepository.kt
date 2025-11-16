package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

class FIRUserRepository(private val firebaseRepository : FirebaseRepository){

    fun toJsonUser(user : User): Map<String,*> {
        return mapOf(
            "id" to user.id,
            "name" to user.name,
            "email" to user.email,
            "phone" to user.phone,
            "groups" to user.groups
        )
    }

    //Get a list of the group that the current user joined
    suspend fun getJoinedGroupsForUser(): List<String> {
        val userRef = firebaseRepository.getUserDirectory()
        val dataRes: DataResult<Map<String, Any>> = firebaseRepository.fetchDBRef(userRef)
        if (dataRes !is DataResult.Success) return emptyList()
        val privateData = dataRes.data["private"] as? Map<String, Any> ?: return emptyList()
        val groupsMap = privateData["groups"] as? Map<String, Map<String, Any>> ?: return emptyList()
        return groupsMap.mapNotNull { (groupId, groupData) ->
            val joined = groupData["joined"] as? Boolean ?: false
            if (joined) groupId else null
        }
    }

    suspend fun fromJsonUser(data: Map<String,*>): User? {
        if(data==null) return null
        val currentUserId = firebaseRepository.getCurrentUserUID() ?: return null
        val name = data["name"] as? String ?: ""
        val email = data["email"] as? String ?: ""
        val phone = data["phone"] as? String ?: ""
        val joinedGroupIds = getJoinedGroupsForUser()
        val firGroupRepository = FIRGroupRepository(firebaseRepository)
        val groupsList = mutableListOf<Group>()
        for (groupId in joinedGroupIds) {
            val group = firGroupRepository.getGroupById(groupId)
            if (group != null) groupsList.add(group)
        }
        return User(
            id = currentUserId,
            name = name,
            email = email,
            phone = phone,
            groups = groupsList
        )
    }


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
        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newUserDirectory!!, toJsonUser(user))

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