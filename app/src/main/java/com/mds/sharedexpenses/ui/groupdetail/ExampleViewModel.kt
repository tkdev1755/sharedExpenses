package com.mds.sharedexpenses.ui.groupdetail

import androidx.activity.result.launch
import androidx.compose.ui.tooling.data.UiToolingDataApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.di.AppContainer
import com.mds.sharedexpenses.data.models.Group
import kotlinx.coroutines.launch

class ExampleViewModel {
    private val appRepository = AppContainer.appRepository


    private val _groupDetails = MutableLiveData<Group>()
    val groupDetails: LiveData<Group> = _groupDetails

    private val _expenses = MutableLiveData<Map<String, Expense>>()
    val expenses: LiveData<Map<String, Expense>> = _expenses

    fun loadGroupData(groupId: String) {
        /*viewModelScope.launch {

            //val groupData = TODO("Add future function to fetch data ->  // appRepository.groups.getGroups")
            //_groupDetails.postValue(groupData)
            // Utiliser le repository de dépenses via la même façade !
            //val expensesData = appRepository.expenses.getGroupExpenses(groupData)
            //+_expenses.postValue(expensesData)
        }*/
    }

    suspend fun createExpense(group: Group, newExpense: Expense) : DataResult<Boolean> {
        return appRepository.expenses.addGroupExpense(group, newExpense)
    }
}