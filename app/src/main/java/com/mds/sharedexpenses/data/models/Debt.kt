package com.mds.sharedexpenses.data.models

data class Debt(
    val id: String,
    val group: Group,
    val user: User,
    val expenses: Expense){


    /*fun addExpense (expenseId : Int): Debt {
        if (expenses.contains(expenseId)) return this

        val newExpense = expenses.toMutableList()
        newExpense.add(expenseId)
        return copy(expenses = newExpense)
    }

    fun removeExpense (expenseId : Int): Debt {
        val newExpenses = expenses.toMutableList()
        if (!newExpenses.remove(expenseId)) {
            return this
        }
        return copy(expenses = newExpenses)
    }
    fun checkExpenses (expenseId : Int): Boolean {
        for(expense in expenses) {
            if(expense == expenseId) return true
        }
        return false
    }*/
}
