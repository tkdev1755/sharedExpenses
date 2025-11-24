package com.mds.sharedexpenses.data.models

import kotlin.collections.mapOf
import kotlin.to

data class Group(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    val users: MutableList<User> = mutableListOf<User>(),
    val expenses: MutableList<Expense> = mutableListOf<Expense>(),
    val transactions : MutableList<Transaction> = mutableListOf<Transaction>(),
    val debts : MutableList<Debt> = mutableListOf<Debt>(),
    val isOwner : Boolean? = false
)
