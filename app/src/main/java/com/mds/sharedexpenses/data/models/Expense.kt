package com.mds.sharedexpenses.data.models

data class Expense(val id: String,
                   var payer: User,
                   val debtors: MutableList<User> = mutableListOf<User>(),
                   val amount: Double,
                   var name: String = "",
                   var description: String = "",
                   var icon: String = ""
)