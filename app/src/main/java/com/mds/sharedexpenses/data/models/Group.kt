package com.mds.sharedexpenses.data.models

data class Group(
    val id: String = "",
    var name: String = "",
    val is_owner : Boolean = false,
    var description: String = "",
    val users: MutableList<User> = mutableListOf<User>(),
    val expenses: MutableList<Expense> = mutableListOf<Expense>(),
    val transactions : MutableList<Transaction> = mutableListOf<Transaction>()){

    companion object{
        fun fromJson(groupID:String , data : Map<String, Any>, userData : Map<String,*>) : Group{
            val id = data["id"] as String
            val name = data["name"] as String
            val description = data["description"] as String
            val is_owner = data["is_owner"] as Boolean
            val users : MutableList<User> = mutableListOf<User>();
            for (user in userData){
                // User.fromJson()
            }
            val jsonExpenses = data["expenses"] as Map<*, *>
            val expenses: MutableList<Expense> = mutableListOf<Expense>();
            for (expense in jsonExpenses){
                // Expense.fromJson()
                // and the Expense.fromJson() would expect 3 things : a Map<String,any> with json data and the list of Users that was decoded beforehand and the Group ID
                // we add all expenses to the group ↓
                // val newExpenses = expenses.toMutableList()
                // newExpenses.add(expense)
                //
            }
            val jsonTransaction = data["transactions"] as Map<String, Any>
            val transactions: MutableList<Transaction> = mutableListOf<Transaction>();
            for (transaction in jsonTransaction) {
                // Transaction.fromJson()
                // the transaction would expect 3 things : a Map<String,any> with json data and the list of Users that was decoded beforehand and the group ID
                // Same here, we add all transactions to the group ↓
                // val newTransactions = transactions.toMutableList()
                // newTransactions.add(transaction)
            }

            return Group(
                groupID,
                name,
                is_owner,
                description,
                users,
                expenses,
                transactions,
            )
        }

    }

    fun toJson(): Map<String, Any> {

        val usersMap = users.associate { it.id to mapOf("owner" to is_owner) }
        val expensesMap = expenses.associate { it.id to it.toJson() }
        val transactionsMap = transactions.associate { it.id to it.toJson() }

        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "users" to usersMap,
            "expenses" to expensesMap,
            "transactions" to transactionsMap
        )
    }
    /*fun addUser(userId: String): Group {
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
    }*/
}

private fun User.toJson() {
    TODO("Not yet implemented")
}
