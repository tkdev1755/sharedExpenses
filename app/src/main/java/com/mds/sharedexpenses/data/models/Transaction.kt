package com.mds.sharedexpenses.data.models

data class Transaction(
    val id: String,
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
