package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.User

// In Kotlin, a data class is used to represent data objects


interface UserRepositoryInterface {
    fun getUserById(id: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}