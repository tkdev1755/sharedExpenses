package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction

// In Kotlin, a data class is used to represent data objects


interface GroupRepositoryInterface {
    fun toJsonGroup(group: Group)
    fun fromJsonGroup(data: Map<String, *>?)
    fun checkOwners(data: Map<String,*>?)
    suspend fun getUsersByGroup (group_id : String)
    suspend fun getExpensesForGroup(groupId: String)
    suspend fun getTransactionsbyGroup(groupId : String)
    fun getUserBalanceInGroup(transactions: List<Transaction>, userId: String)
    suspend fun createGroup(group: Group)
    fun deleteGroup(group: Group)
    fun updateGroup(group: Group)
}
