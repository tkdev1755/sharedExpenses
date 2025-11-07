package com.mds.sharedexpenses.data.models

data class Expense(val id: Int,
                   var payer: String = "",
                   val debtors: List<String> = emptyList(),
                   val amount: Double = 0.0,
                   var name: String = "",
                   var description: String = "") {


    fun amountPerPerson(): Double {
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
    }

}
