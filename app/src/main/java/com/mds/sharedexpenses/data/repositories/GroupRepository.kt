package com.mds.sharedexpenses.data.repositories

// In Kotlin, a data class is used to represent data objects
data class Group(
    val id: String = "",
    var name: String = "",
    var description: String = "",
    val users: List<Int> = emptyList(),
    val expenses: List<Int> = emptyList(),
    val transactions : List<Int> = emptyList())

interface GroupRepositoryInterface {
    fun createGroup(group: Group)
    fun deleteGroup(group: Group)
    fun updateGroup(group: Group)
}
