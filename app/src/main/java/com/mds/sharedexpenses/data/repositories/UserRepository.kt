package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.User

// In Kotlin, a data class is used to represent data objects
data class User(
    val uid: String = "",
    var name: String = "",
    var email: String = "",
    val groups: List<String> = emptyList(),
    val debts: List<Int> = emptyList()){


/* fun addToGroup(groupId: String): User {
        if (groups.contains(groupId)) return this

        val newGroups = groups.toMutableList()
        newGroups.add(groupId)
        return copy(groups = newGroups)
    }

    fun removeFromGroup(groupId: String): User {
        val newGroups = groups.toMutableList()
        if (!newGroups.remove(groupId)) {
            return this
        }
        return copy(groups = newGroups)
    }

    fun isInGroup(groupId: String): Boolean {
        return groups.contains(groupId)
    }

    fun addDebt(debtId: Int): User {
        if (debts.contains(debtId)) return this

        val newDebts = debts.toMutableList()
        newDebts.add(debtId)
        return copy(debts = newDebts)
    }

    fun removeDebt(debtId: Int): User {
        val newDebts = debts.toMutableList()
        if (!newDebts.remove(debtId)) {
            return this
        }
        return copy(debts = newDebts)
    } */
}

interface UserRepositoryInterface {
    fun getUserById(id: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}