package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import java.time.LocalDateTime

// Add the functions that will communicate with the Firebase
class FIRExpenseRepository(private val firebaseRepository: FirebaseRepository) {

    fun toJsonExpense(expense: Expense): Map<String, Any?> {
        val debtorIds = expense.debtors.map { it.id }
        return mapOf(
            "payer" to expense.payer.id,
            "debtors" to debtorIds,
            "amount" to expense.amount,
            "name" to expense.name,
            "description" to expense.description,
            "icon" to expense.icon,
            "date" to firebaseRepository.formatter.format(expense.date)
        )
    }

    fun fromJsonExpense(data: Map<String, Any>?, usersList: List<User>, expenseId: String): Expense? {
        if(data == null) return null
        val expensesMap = data[expenseId] as? Map<String, Map<String, Any>> ?: emptyMap()
        val payerId = expensesMap["payer"] as? String ?: ""

        val amount = expensesMap["amount"].toString().toDoubleOrNull() ?: -1.0
        val payerUser = usersList.firstOrNull { user -> user.id == payerId } ?: User(id = payerId, name = "", email = "", groups = mutableListOf())
        val debtorIds = expensesMap["debtors"] as? List<String> ?: emptyList()
        val debtorUsers = debtorIds.map { debtorId -> usersList.firstOrNull { user -> user.id == debtorId } ?: User(id = debtorId, name = "", email = "", groups = mutableListOf()) }.toMutableList()
        return Expense(
                id = expenseId,
                payer = payerUser,
                amount = amount,
                debtors = debtorUsers,
                name = expensesMap["name"] as? String ?: "",
                description = expensesMap["description"] as? String ?: "",
                icon = expensesMap["icon"] as? String ?: "",
                date = LocalDateTime.parse(expensesMap["date"] as? String ?: "20-02-2024-12-10", firebaseRepository.formatter )
            )
    }


    //Here is the getters
    //Get a specific Expense
    fun getExpensebyId(expenseId:String, group: Group): Expense?{
        return group.expenses.firstOrNull() {it.id == expenseId}
    }

    //Calculate how much an user have payed in a group
    fun getPayedbyUser(group: Group, userId: String): Double?{
        return group.expenses.filter{it.payer.id == userId}.sumOf {it.amount}
    }

    //Calculte how much an user should pay in a group
    fun getOwnedbyUser(group: Group, userId: String): Double?{
        return group.expenses.filter{it.debtors.any{debtor-> debtor.id == userId}}.sumOf { it.amount }
    }

    //Calculate the total expense of the group
    fun getTotalGroupExpenses(group: Group): Double?{
        return group.expenses.sumOf { it.amount }
    }

    //Get all the expenses creates by a specific payer
    fun getExpensesByPayer(group: Group, payerId : String): List<Expense>?{
        return group.expenses.filter{it.payer.id == payerId}
    }

    //Get all the debtors for a specific expense
     fun getDebtorsForExpense(group: Group, expenseId : String): List<User>?{
        return getExpensebyId(expenseId, group)?.debtors ?: emptyList()
    }

    fun getGroupExpensesDirectory(groupID: String) : DatabaseReference{
        val groupDirectory : DatabaseReference = firebaseRepository.getGroupDirectory(groupID)
        return groupDirectory.child("expenses")
    }

    suspend fun getGroupExpenses(group: Group) : Map<String,*>?{
        val  expensesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(expensesDirectory)

        if(dataRes is DataResult.Success) {
            val expenseMap : Map<String,*>? = dataRes.data
            return expenseMap
        }
        else{
            return null
        }
    }

    suspend fun addGroupExpense(group: Group, expense: Expense,edit:Boolean=false): DataResult<Boolean> {
        val expensesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)
        val result : DataResult<DatabaseReference> = if (!edit) firebaseRepository.createChildReference(expensesDirectory) else DataResult.Success(expensesDirectory.child(expense.id))
        var newExpenseDirectory : DatabaseReference? = null
        if (result is DataResult.Success){
            newExpenseDirectory = result.data
        }
        else{
            return DataResult.Error("FIREBASE_ERROR", "Failed to create expense child reference.")
        }
        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newExpenseDirectory!!, toJsonExpense(expense))
        if(dataRes is DataResult.Success) {
            return dataRes
        }
        return DataResult.Error("FIREBASE_ERROR", "Failed to write to new expense database reference.")
    }

    suspend fun removeGroupExpense(group: Group, expense: Expense) : DataResult<Boolean> {
        val groupExpensesDirectory : DatabaseReference = getGroupExpensesDirectory(group.id)

        val expenseDirectory : DatabaseReference = groupExpensesDirectory.child(expense.id.toString())
        return firebaseRepository.deleteDBRef(expenseDirectory)
    }
}
