package com.mds.sharedexpenses.data.models

data class Transaction(
    val id: String,
    val expense: Expense,
    val amount: Double = 0.0,
    val issuer: User,
    val receiver: User
)
