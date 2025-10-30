package com.mds.sharedexpenses.data.repositories
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val groups: List<String> = emptyList(),
    val debts: List<Int> = emptyList())

interface UserRepositoryInterface {
    fun getUserById(id: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}