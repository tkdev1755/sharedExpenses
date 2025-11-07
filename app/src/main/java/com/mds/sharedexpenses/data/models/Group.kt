package com.mds.sharedexpenses.data.models

data class Group(
    val id: String = "",
    var name: String = "",
    var description: String = "",
    val users: List<String> = emptyList(),
    val expenses: List<Int> = emptyList(),
    val transactions : List<Int> = emptyList()){


    fun addUser(userId: String): Group {
        if (users.contains(userId)) return this

        val newUsers = users.toMutableList()
        newUsers.add(userId)
        return copy(users = newUsers)
    }

    fun removeUser(userId: String): Group {
        val newUsers = users.toMutableList()
        if (!newUsers.remove(userId)) {
            return this
        }
        return copy(users = newUsers)
    }

    fun addExpense(expenseId: Int): Group {
        if (expenses.contains(expenseId)) return this

        val newExpenses = expenses.toMutableList()
        newExpenses.add(expenseId)
        return copy(expenses = newExpenses)
    }

    fun removeExpense (expenseId : Int): Group {
        val newExpenses = expenses.toMutableList()
        if (!newExpenses.remove(expenseId)) {
            return this
        }
        return copy(expenses = newExpenses)
    }

    fun addTransaction(transactionID: Int) : Group {
        if (transactions.contains(transactionID)) return this

        val newTransactions = expenses.toMutableList()
        newTransactions.add(transactionID)
        return copy(transactions = newTransactions)
    }

    fun containsUser(userId: String): Boolean {
        return users.contains(userId)
    }

    fun getMemberCount(): Int {
        return users.size
    }
}
