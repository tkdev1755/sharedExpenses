package com.mds.sharedexpenses.data.models

data class Transaction(
    val id: String,
    val expense: Expense,
    val amount: Double = 0.0,
    val issuer: User,
    val receiver: User){

/*
    fun isValid(): Boolean {
        return amount > 0 && issuer.isNotBlank() && receiver.isNotBlank() && issuer != receiver
    }

    fun involvesUser(userId: String): Boolean {
        return issuer == userId || receiver == userId
    }*/

    fun toJson() {
        TODO("Not yet implemented")
    }
}
