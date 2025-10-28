package com.mds.sharedexpenses.data.repositories

data class Group(val id: String, val name: String, val members: List<User>) // TODO: please adopt

interface GroupRepositoryInterface {
    fun createGroup(group: Group)
    fun deleteGroup(group: Group)
    fun updateGroup(group: Group)
}