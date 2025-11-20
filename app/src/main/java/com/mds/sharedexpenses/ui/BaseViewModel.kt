package com.mds.sharedexpenses.ui

import androidx.lifecycle.ViewModel
import com.mds.sharedexpenses.utils.SnackbarManager

open class BaseViewModel : ViewModel() {

    fun showErrorMessage(message: String) {
        SnackbarManager.showMessage(message)
    }
    fun handleException(
        e: Throwable,
        userFriendlyMessage: String = "We are sorry. An error occurred."
    ) {
        println("Error collected in BaseViewModel: $e")
        SnackbarManager.showMessage(userFriendlyMessage)
    }
}