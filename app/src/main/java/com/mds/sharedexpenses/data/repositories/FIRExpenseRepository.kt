package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.Transaction
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import kotlin.math.exp

// Add the functions that will communicate with the Firebase
class FIRExpenseRepository(private val firebaseRepository: FirebaseRepository) {

    fun toJson(expense: Expense): Map<String, Any?> {
        return mapOf(
            "id" to expense.id,
            "payer" to expense.payer.id,
            "debtors" to expense.debtors,
            "amount" to expense.amount,
            "name" to expense.name,
            "description" to expense.description,
            "icon" to expense.icon
        )
    }

    fun fromJsonExpense(data: Map<String, Any>?, usersList: List<User>, expenseId: String): Expense? {
        if(data == null) return null
        val expensesMap = data[expenseId] as? Map<String, Map<String, Any>> ?: emptyMap()
        val payerId = expensesMap["payer"] as? String ?: ""
        val payerUser = usersList.firstOrNull { user -> user.id == payerId } ?: User(id = payerId, name = "", email = "", groups = mutableListOf())
        val debtorIds = expensesMap["debtors"] as? List<String> ?: emptyList()
        val debtorUsers = debtorIds.map { debtorId -> usersList.firstOrNull { user -> user.id == debtorId } ?: User(id = debtorId, name = "", email = "", groups = mutableListOf()) }.toMutableList()
        return Expense(
                id = expenseId,
                payer = payerUser,
                amount = 0.0,
                debtors = debtorUsers,
                name = expensesMap["name"] as? String ?: "",
                description = expensesMap["description"] as? String ?: "",
                icon = expensesMap["icon"] as? String ?: ""
            )
    }


    //Here is the getters
    //Get a specific Expense
    fun getExpensebyId(expenseId:String, group: Group): Expense?{
        return group.expenses.firstOrNull() {it.id == expenseId}
    }

    //Calculate how much an user have payed in a group
    fun getPayedbyUser(group: Group, userId: String): Double {
        return group.expenses.filter{it.payer.id == userId}.sumOf {it.amount}
    }

    //Calculte how much an user should pay in a group
    fun getOwnedbyUser(group: Group, userId: String): Double{
        return group.expenses.filter{it.debtors.any{debtor-> debtor.id == userId}}.sumOf { it.amount }
    }
}
