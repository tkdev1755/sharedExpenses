package com.mds.sharedexpenses.data.repositories



import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult

class FIRTransactionRepository(private val firebaseRepository: FirebaseRepository) {

    fun toJsonTransaction(transaction: Transaction): Map<String, Any?> {
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
    fun getTransactionParticipants(transaction : Transaction): Pair<User,User>? {
        return Pair(transaction.issuer, transaction.receiver)
    }

    //Get the total amount of transaction in a group
    fun getTotalAmountTransaction(group : Group): Double?{
        return group.transactions.sumOf { it.amount }
    }

    //Get all the transaction between two specific user
    fun getTransactionBetweenUsers(group : Group, useraId: String, userbId : String): List<Transaction>?{
        return group.transactions.filter { it.issuer.id == useraId && it.receiver.id == userbId ||
                                            it.issuer.id == userbId && it.receiver.id == useraId}
    }

    //Calculate the total payed by a user in a group
    fun getTotalPaid(group: Group, userId : String): Double?{
        return group.transactions.filter { it.issuer.id == userId }.sumOf { it.amount }
    }

    //Calculate how much a user have received in a group
    fun getUserTotalReceived(group : Group, userId : String): Double?{
        return group.transactions.filter { it.receiver.id == userId }.sumOf { it.amount }
    }

    //Calculate how much an user received and payed in a group
    fun getUserBalance(group: Group, userId : String): Double?{
        val paid = getTotalPaid(group, userId) ?: 0.0
        val received = getUserTotalReceived(group, userId) ?: 0.0
        return paid-received
    }

    //Get the last transaction
    fun getLastTransaction(group: Group): Transaction?{
        return group.transactions.lastOrNull()
    }

    //Get the amount transactions mad by an user
    fun getTransactionsCountUser(group : Group, userId : String): Int?{
        return group.transactions.count{it.issuer.id == userId || it.receiver.id == userId}
    }

    //Get the path to a specific transaction in the firebase
    fun getGroupTransactionsDirectory(groupID: String) : DatabaseReference{
        val groupDirectory : DatabaseReference = firebaseRepository.getGroupDirectory(groupID)
        return groupDirectory.child("transactions")
    }

    //Get a specific in a transaction for a specific group
    suspend fun getGroupTransactions(group: Group) : Map<String,*>?{
        val  transactionsDirectory : DatabaseReference = getGroupTransactionsDirectory(group.id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(transactionsDirectory)

        if(dataRes is DataResult.Success) {
            val transactionMap : Map<String,*>? = dataRes.data
            return transactionMap
        }
        else{
            return null
        }
    }

    suspend fun addGroupTransaction(group: Group, transaction: Transaction): DataResult<Boolean> {
        val  transactionsDirectory : DatabaseReference = getGroupTransactionsDirectory(group.id)
        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(transactionsDirectory)
        var newTransactionDirectory : DatabaseReference? = null
        if (result is DataResult.Success){
            newTransactionDirectory = result.data
        }
        else{
            return DataResult.Error("FIREBASE_ERROR", "Failed to create transaction child reference.")
        }

        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newTransactionDirectory!!, toJsonTransaction(transaction))

        if(dataRes is DataResult.Success) {
            return dataRes
        }

        return DataResult.Error("FIREBASE_ERROR", "Failed to write to a new transaction database reference.")
    }

    suspend fun removeGroupTransaction(group: Group, transaction: Transaction) {
        val groupExpensesDirectory : DatabaseReference = getGroupTransactionsDirectory(group.id)

        val expenseDirectory : DatabaseReference = groupExpensesDirectory.child(transaction.id.toString())
        firebaseRepository.deleteDBRef(expenseDirectory)
    }
}
