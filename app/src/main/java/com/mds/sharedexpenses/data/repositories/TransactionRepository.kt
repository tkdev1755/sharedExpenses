package com.mds.sharedexpenses.data.repositories


data class Transaction(
    val id: Int = 0,
    val expense: Int = 0,
    val amount: Double = 0.0,
    val issuer: String = "",
    val receiver: String = "") // TODO: please adoptx

/* interface UserRepositoryInterface {
    fun getUserById(id: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}*/