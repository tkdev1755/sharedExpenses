package com.mds.sharedexpenses.data.repositories
data class User(val id: String, val email: String) // TODO: feel free to change :)

interface UserRepositoryInterface {
    fun getUserById(id: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}