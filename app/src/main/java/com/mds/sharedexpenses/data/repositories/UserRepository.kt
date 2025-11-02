package com.mds.sharedexpenses.data.repositories

// In Kotlin, a data class is used to represent data objects
data class User(
    val id: String = "",
    var name: String = "",
    var email: String = "",
    val groups: List<String> = emptyList(),
    val debts: List<Int> = emptyList())

interface UserRepositoryInterface {
    fun getUserById(id: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}