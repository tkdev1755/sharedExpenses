package com.mds.sharedexpenses.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.di.AppContainer
import com.mds.sharedexpenses.utils.SnackbarManager
import kotlinx.coroutines.launch

data class MainUiState(
    val currentUser: User? = null
)
open class BaseViewModel : ViewModel() {
    protected val appRepository = AppContainer.appRepository
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser
    val _errorData = MutableLiveData<String>()
    val errorData : LiveData<String> = _errorData

    init {
        getUserData()
    }

    fun getUserData() {

        viewModelScope.launch {
            if (appRepository.checkLoginStatus()){
                val userData = appRepository.users.getCurrentUserData()
                println("gettingUserData")
                if (userData is DataResult.Success) {
                    _currentUser.postValue(userData.data)
                }
                else if (userData is DataResult.Error){
                    _errorData.postValue("Error getting user data")
                    showErrorMessage("${userData.errorMessage}")
                }
            }



        }
    }
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
