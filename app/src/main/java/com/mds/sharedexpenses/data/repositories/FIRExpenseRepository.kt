package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

// Add the functions that will communicate with the Firebase
class FIRExpenseRepository(private val firebaseRepository: FirebaseRepository) {

    fun toJson(expense: Expense): Map<String, Any?> {
        return mapOf(
            "id" to expense.id,
            "payer" to expense.payer.id,
            "debtors" to expense.debtors,
            "amount" to expense.amount,
            "amount" to expense.name,
            "description" to expense.description,
            "icon" to expense.icon
        )
    }

    suspend fun fromJson(data: Map<String, Any?>, groupId: String): Transaction? {
        val expensesRes = FIRGroupRepository(firebaseRepository).getExpensesForGroup(groupId)
        val expenses = expensesRes ?: emptyMap()
        val expenseId = data["expense_id"] as? String ?: return null
        val expense = expenses[expenseId] ?: return null
        val issuerId = data["issuer"] as? String ?: return null
        val receiverId = data["receiver"] as? String ?: return null
        val usersData = FIRGroupRepository(firebaseRepository).getUsersByGroup(groupId)
        val usersMap: Map<String, com.mds.sharedexpenses.data.models.User> =
            usersData?.mapValues { (_, userData) ->
                (userData as? com.mds.sharedexpenses.data.models.User
                    ?: User()) as com.mds.sharedexpenses.data.models.User
            } ?: emptyMap()
        return Transaction(
            id = data["id"] as? String ?: "",
            expense = expense,
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            issuer = usersMap[issuerId] ?: User(
                id = issuerId,
                name = "",
                email = "",
                groups = mutableListOf()
            ),
            receiver = usersMap[receiverId] ?: User(
                id = receiverId,
                name = "",
                email = "",
                groups = mutableListOf()
            )
        )
    }
}
