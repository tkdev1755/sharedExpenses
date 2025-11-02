package com.mds.sharedexpenses.data.repositories


//In Kotlin, a data class is used to represent data objects
data class Expense(val id: Int,
                    var payer: String = "",
                    val deptors: List<String> = emptyList(),
                    val amount: Double = 0.0,
                    var name: String = "",
                    var description: String = "") {


    fun amountPerPerson(): Double {
        return if (deptors.isNotEmpty()) amount / deptors.size else 0.0
    }

    fun checkTotalDepts(): Boolean {
        if(deptors.isEmpty())  return false
        return kotlin.math.abs(amountPerPerson() * deptors.size - amount) < 0.001
    }

    fun addDebtors (userId : String,): Expense {
        val newDebtors = deptors.toMutableList()
        newDebtors.add(userId)
        return copy(deptors = newDebtors)
    }

    fun removeDebtors (userId : String,): Expense {
        val newDebtors = deptors.toMutableList()
        newDebtors.remove(userId)
        return copy(deptors = newDebtors)
    }

    fun checkDebtors (userId : String,): Boolean {
        for(debtorId in deptors) {
            if(debtorId == userId) return true
        }
        return false
    }

}

interface ExpenseRepositoryInterface {
    fun createExpense(expense: Expense)
    fun updateExpense(expense: Expense)
    fun deleteExpense(expense: Expense)
    fun getExpensesByGroup(groupId: String): List<Expense>
}


