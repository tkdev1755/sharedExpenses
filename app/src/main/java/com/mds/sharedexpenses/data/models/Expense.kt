package com.mds.sharedexpenses.data.models

data class Expense(val id: String,
                   var payer: User,
                   val debtors: MutableList<User> = mutableListOf<User>(),
                   val amount: Double,
                   var name: String = "",
                   var description: String = "",
                   var icon: String = "") {


    /*fun amountPerPerson(): Double {
        return if (debtors.isNotEmpty()) amount / debtors.size else 0.0
    }

    fun checkTotalDepts(): Boolean {
        if(debtors.isEmpty())  return false
        return kotlin.math.abs(amountPerPerson() * debtors.size - amount) < 0.001
    }

    fun addDebtors (userId : String,): Expense {
        val newDebtors = debtors.toMutableList()
        newDebtors.add(userId)
        return copy(debtors = newDebtors)
    }

    fun removeDebtors (userId : String,): Expense {
        val newDebtors = debtors.toMutableList()
        newDebtors.remove(userId)
        return copy(debtors = newDebtors)
    }

    fun checkDebtors (userId : String,): Boolean {
        for(debtorId in debtors) {
            if(debtorId == userId) return true
        }
        return false
    }*/

    fun toJson(): Map<String, Any> {
        val debtorIds = debtors.map { it.id }

        return mapOf(
            "payer" to payer.id,
            "debtors" to debtorIds,
            "amount" to amount,
            "name" to name,
            "description" to description,
            "icon" to icon
        )
    }

}
