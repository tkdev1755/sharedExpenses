        package com.mds.sharedexpenses.data.repositories

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val users: List<Int> = emptyList(),
    val expenses: List<Int> = emptyList(),
    val transactions : List<Int> = emptyList())

interface GroupRepositoryInterface {
    fun createGroup(group: Group)
    fun deleteGroup(group: Group)
    fun updateGroup(group: Group)
}
