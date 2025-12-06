package com.mds.sharedexpenses.ui.exampleView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.di.AppContainer
import kotlinx.coroutines.launch

// View Model only for illustrational purposes
class ExampleViewModel : ViewModel() {
    private val appRepository = AppContainer.appRepository


    private val _groupDetails = MutableLiveData<Group>()
    val groupDetails: LiveData<Group> = _groupDetails
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser
    private val _expenses = MutableLiveData<Map<String, Expense>>()
    val expenses: LiveData<Map<String, Expense>> = _expenses
    val _errorData = MutableLiveData<String>()
    val errorData : LiveData<String> = _errorData
    fun getUserData() {
        viewModelScope.launch {

            val userData = appRepository.users.getCurrentUserData()
            if (userData is DataResult.Success) {
                _currentUser.postValue(userData.data)
            }
            else{
                // For instance here parse the error message and code and display the according message
                _errorData.postValue("Error getting user data")
            }

        }
    }

    fun createGroup(group: Group){
        viewModelScope.launch {
            val result = appRepository.groups.createGroup(group)
            if (result is DataResult.Success){
                _groupDetails.postValue(group)
            }
            else{
                // Here parse the message to display the error message
            }
        }
    }

    fun createExpense(group: Group, newExpense: Expense){
        viewModelScope.launch {
            val result = appRepository.expenses.addGroupExpense(group, newExpense)
            if (result is DataResult.Success){
                // Do something
            }
            else{
                // Here parse the message to display the error message
            }
        }
    }

}
