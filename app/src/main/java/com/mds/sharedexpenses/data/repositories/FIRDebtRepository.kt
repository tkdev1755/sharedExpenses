package com.mds.sharedexpenses.data.repositories

import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

class FIRDebtRepository(private val firebaseRepository : FirebaseRepository) {
    fun toJsonDebt(debt : Debt): Map <String,Any>? {
        return mapOf(
            "id" to debt.id,
            "group" to debt.group.id,
            "user" to debt.user.id,
            "amount" to debt.amount,
            "expense" to debt.expenses.id
        )
    }

    fun fromJsonDebt(data : Map<String, Any>?, usersList : List<User>, expensesList: List<Expense>, debtId : String, group : Group): Debt? {
        if(data == null) return null
        val debtsMap = data[debtId] as? Map<String, Map<String, Any>> ?: emptyMap()
        val expId = debtsMap["expense_id"] as? String ?: ""
        val linkExpense = expensesList.firstOrNull { expense -> expense.id == expId } ?: Expense(id = expId, payer = usersList.first(), amount = 0.0, debtors = mutableListOf())
        val userId = debtsMap["user"] as? String ?: ""
        val linkUser = usersList.firstOrNull { user -> user.id == userId } ?: User(id = userId, name = "", email = "", groups = mutableListOf())
        return Debt(
            id = debtId,
            group = group,
            user = linkUser,
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            expenses = linkExpense
        )
    }


    //Here are the getters
    //Get the name of the debtor
    fun getDebtorName(debt: Debt): String? {
        return debt.user.name
    }

    //Get the payer that cause the debt
    fun getPayerofDebt(debt: Debt): User?{
        return debt.expenses.payer
    }

    //Check is a debt is link to the current user
    fun isDebtofUser(debt: Debt, userId: String): Boolean? {
        return debt.user.id == userId
    }

    //Get the name of the expense link to debt
    fun getExpenseName(debt: Debt): String?{
        return debt.expenses.name
    }

    //Get the description of the expense link to debt
    fun getExpenseDescription(debt: Debt): String? {
        return debt.expenses.description
    }

    fun getUserDebtDirectory() : DatabaseReference{
        val userDirectory : DatabaseReference = firebaseRepository.getUserDirectory()
        return userDirectory.child("debt")
    }

    /// Example Function for fetching data from the database
    suspend fun getUserDebt() : Map<String,*>?{
        val  debtDirectory : DatabaseReference = getUserDebtDirectory()
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(debtDirectory)
        // Now check if the data was successfully fetched
        if(dataRes is DataResult.Success) {
            val debtMap : Map<String,*>? = dataRes.data
            return debtMap
        }
        else{
            return null
        }
    }
}



// Add the functions that will communicate with the Firebase