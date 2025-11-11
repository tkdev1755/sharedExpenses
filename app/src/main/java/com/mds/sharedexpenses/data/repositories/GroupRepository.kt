package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Group

// In Kotlin, a data class is used to represent data objects


interface GroupRepositoryInterface {
    fun serializeGroup(group: Group)
    suspend fun deserializeGroup(data: Map<String, *>?)
    fun checkOwners(data: Map<String,*>?)
    suspend fun createGroup(group: Group)
    fun deleteGroup(group: Group)
    fun updateGroup(group: Group)
}
