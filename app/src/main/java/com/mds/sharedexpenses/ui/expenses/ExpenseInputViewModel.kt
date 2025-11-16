package com.mds.sharedexpenses.ui.expenses

data class ExpenseInputViewModel(
    val title: String = "",
    val amount: String = "",
    val category: String = "",
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val allowNotifications: Boolean = false
) {
    val isValid: Boolean
        get() = title.isNotBlank() && amount.toDoubleOrNull()?.let { it > 0 } == true
}
