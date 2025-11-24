package com.mds.sharedexpenses.data.models

data class Debt(
    val id: String,
    val group: Group,
    val user: User,
    val debtor: String,
    val amount: Double,
    val expenses: Expense
)
