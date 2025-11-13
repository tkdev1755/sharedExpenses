package com.mds.sharedexpenses.data.repositories


import com.mds.sharedexpenses.data.models.Expense
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

    fun fromJsonTransaction(data: Map<String, Any>?, usersList: List<User>, transactionId: String, expensesList: List<Expense>): Transaction? {
        if(data == null) return null
        val transactionsMap = data[transactionId] as? Map<String, Map<String, Any>> ?: emptyMap()
        val expId = transactionsMap["expense_id"] as? String ?: ""
        val linkExpense = expensesList.firstOrNull { expense -> expense.id == expId } ?: Expense(id = expId, payer = usersList.first(), amount = 0.0, debtors = mutableListOf())
        val issuerId = transactionsMap["issuer"] as? String ?: ""
        val issuerUser = usersList.firstOrNull { user -> user.id == issuerId } ?: User(id = issuerId, name = "", email = "", groups = mutableListOf())
        val receiverId = transactionsMap["receiver"] as? String ?: ""
        val receiverUser = usersList.firstOrNull { user -> user.id == receiverId } ?: User(id = receiverId, name = "", email = "", groups = mutableListOf())
        return Transaction(
            id = transactionId,
            expense = linkExpense,
            amount = 0.0,
            issuer = issuerUser,
            receiver = receiverUser
        )
    }

    //Here begins the getters


    //Get the participants fro a specific transactions
    fun getTransactionParticipants(transaction : Transaction): Pair<User,User> {
        return Pair(transaction.issuer, transaction.receiver)
    }


}
