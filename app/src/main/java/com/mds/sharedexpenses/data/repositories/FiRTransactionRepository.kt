package com.mds.sharedexpenses.data.repositories


import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult

class FiRTransactionRepository(private val firebaseRepository: FirebaseRepository) {

    fun toJson(transaction: Transaction): Map<String, Any?> {
        return mapOf(
            "id" to transaction.id,
            "expense_id" to transaction.expense.id,
            "amount" to transaction.amount,
            "issuer" to transaction.issuer.id,
            "receiver" to transaction.receiver.id
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
        val usersMap: Map<String, User> = usersData?.mapValues { (_, userData) -> (userData as? User ?: User()) as User } ?: emptyMap()
        return Transaction(
            id = data["id"] as? String ?: "",
            expense = expense,
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            issuer = usersMap[issuerId] ?: User(id = issuerId, name = "", email = "", groups = mutableListOf()),
            receiver = usersMap[receiverId] ?: User(id = receiverId, name = "", email = "", groups = mutableListOf())
        )
    }

    //Here begins the getters


    //Get the participants fro a specific transactions
    fun getTransactionParticipants(transaction : Transaction): Pair<User,User> {
        return Pair(transaction.issuer, transaction.receiver)
    }


}
