package com.mds.sharedexpenses.data.repositories

// In Kotlin, a data class is used to represent data objects
data class Transaction(
    val id: Int = 0,
    val expense: Int = 0,
    val amount: Double = 0.0,
    val issuer: String = "",
    val receiver: String = ""){


    fun isValid(): Boolean {
        return amount > 0 && issuer.isNotBlank() && receiver.isNotBlank() && issuer != receiver
    }

    fun involvesUser(userId: String): Boolean {
        return issuer == userId || receiver == userId
    }
}

/* interface UserRepositoryInterface {
    fun getUserById(id: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}*/