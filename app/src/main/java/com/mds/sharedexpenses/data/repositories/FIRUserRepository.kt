package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

class FIRUserRepository(private val firebaseRepository : FirebaseRepository){

    fun toJsonUser(user : User): Map<String,*> {
        return mapOf(
            "name" to user.name,
            "email" to user.email,
            "phone" to user.phone,
            "notifications" to user.notifications
        )
    }

    suspend fun getCurrentUserData(): DataResult<User> {
        if (!firebaseRepository.checkLoginStatus()){
            return DataResult.Error("LOGIN_ERROR", "User is not logged in.")
        }
        val userRef = firebaseRepository.getUserDirectory()
        val data = firebaseRepository.fetchDBRef<Map<String, *>>(userRef)
        if (data !is DataResult.Success) return DataResult.Error("FIREBASE_ERROR", "Failed to fetch user data>()")
        var user : User = fromJsonUser(data.data)?: return DataResult.Error("FIREBASE_ERROR", "Failed to parse user data>()")
        return DataResult.Success(user)
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
            groupId
        }
    }

    suspend fun fromJsonUser(data: Map<String,*>): User? {
        if(data==null) return null
        val currentUserId = firebaseRepository.getCurrentUserUID() ?: return null
        val name = data["name"] as? String ?: ""
        val email = data["email"] as? String ?: ""
        val phone = data["phone"] as? String ?: ""
        val notifications = data["notifications"] as? Boolean ?: false
        val joinedGroupIds = getJoinedGroupsForUser()
        val firGroupRepository = FIRGroupRepository(firebaseRepository)
        val groupsList = mutableListOf<Group>()
        for (groupId in joinedGroupIds) {
            val group : DataResult<Group> = firGroupRepository.getGroupById(groupId)
            if (group is DataResult.Error) return null
            if (group is DataResult.Success) groupsList.add(group.data)
        }
        return User(
            id = currentUserId,
            name = name,
            email = email,
            phone = phone,
            groups = groupsList,
            notifications = notifications //Added notifications
        )
    }


    suspend fun addUser(user: User): DataResult<Boolean> {
        val  userDirectory : DatabaseReference = firebaseRepository.getUserDirectory()

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(userDirectory, toJsonUser(user))

        if(dataRes is DataResult.Success) {
            return dataRes
        }
        return DataResult.Error("FIREBASE_ERROR", "Failed to write to a new user database reference.")
    }

    suspend fun removeUser(group: Group) {
        val userDirectory : DatabaseReference = firebaseRepository.getUserDirectory()
        firebaseRepository.deleteDBRef(userDirectory)
    }
}
