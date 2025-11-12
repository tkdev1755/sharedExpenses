package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.models.Expense

class FirebaseTransactionRepository(private val firebaseRepository: FirebaseRepository) {

    fun serialize(transaction: Transaction): Map<String, Any?> {
        return mapOf(
            "id" to transaction.id,
            "expense_id" to transaction.expense.id,
            "amount" to transaction.amount,
            "issuer" to transaction.issuer.id,
            "receiver" to transaction.receiver.id
        )
    }

    suspend fun deserialize(data: Map<String, Any?>, groupId: String): Transaction? {
        val expensesRes = FirebaseGroupRepository(firebaseRepository).getExpensesForGroup(groupId)
        val expenses = expensesRes ?: emptyMap()
        val expenseId = data["expense_id"] as? String ?: return null
        val expense = expenses[expenseId] ?: return null
        val issuerId = data["issuer"] as? String ?: return null
        val receiverId = data["receiver"] as? String ?: return null
        val usersMap = (FirebaseGroupRepository(firebaseRepository).getUsersByGroup(groupId) ?: emptyMap()) as Map<String, String>
        val issuerUser = User(
            id = issuerId,
            name = usersMap[issuerId] ?: "",
            email = "",
            groups = mutableListOf()
        )
        val receiverUser = User(
            id = receiverId,
            name = usersMap[receiverId] ?: "",
            email = "",
            groups = mutableListOf()
        )

        return Transaction(
            id = data["id"] as? String ?: "",
            expense = expense,
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            issuer = issuerUser,
            receiver = receiverUser
        )
    }
}
