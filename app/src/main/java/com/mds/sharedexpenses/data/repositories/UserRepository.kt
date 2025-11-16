package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User

// In Kotlin, a data class is used to represent data objects


interface UserRepositoryInterface {
    fun toJsonUser(user : User)
    suspend fun getJoinedGroupsForUser()
    suspend fun fromJsonUser(data: Map<String,Any>?)
    suspend fun addUser(user: User)
    suspend fun removeUSer(group: Group)
}