package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User

// In Kotlin, a data class is used to represent data objects

interface DebtRepositoryInterface {
    fun toJsonDebt(debt : Debt)
    fun fromJsonDebt(data : Map<String, Any>?, usersList : List<User>, expensesList: List<Expense>, debtId : String, group : Group)
    fun getDebtorName(debt: Debt)
    fun getPayerofDebt(debt: Debt)
    fun isDebtofUser(debt: Debt, userId: String)
    fun getExpenseName(debt: Debt)
    fun getExpenseDescription(debt: Debt)
    fun getUserDebtDirectory()
    suspend fun getUserDebt()
}
