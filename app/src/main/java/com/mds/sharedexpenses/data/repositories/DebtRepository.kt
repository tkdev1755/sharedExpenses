package com.mds.sharedexpenses.data.repositories

import com.mds.sharedexpenses.data.models.Debt

// In Kotlin, a data class is used to represent data objects

interface DebtRepositoryInterface {
    fun createDebt(debt: Debt)
    fun updateDebt(expense: Debt)
    fun deleteDebt(expense: Debt)
}
