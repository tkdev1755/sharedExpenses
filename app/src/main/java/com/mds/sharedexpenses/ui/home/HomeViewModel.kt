package com.mds.sharedexpenses.ui.home

import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.mds.sharedexpenses.domain.di.AppContainer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


data class HomeUiState(
    val groupWithRecentActivity: Group? = null, // TODO: if there are no groups with recent activity, dont show the corresponding block
    val groups: List<Group> = emptyList()
)

sealed class HomeNavigationEvent {
    data class ToGroupDetails(val groupId: String) : HomeNavigationEvent()
    object ToCreateGroup : HomeNavigationEvent()
}

class HomeViewModel : BaseViewModel() {
    // TODO: add Group Repository
    // private val groupRepository = GroupRepository()
    private val appRepository = AppContainer.appRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        fetchGroups()
    }

    private fun fetchGroups() {
        // TODO: fetch groups from repository
        viewModelScope.launch {

            val userResult = appRepository.users.getCurrentUserData()

            if(userResult is DataResult.Success){

                val user = userResult.data
                val groups = user.groups.toList()

                //Placeholder for recent activity
                val recentGroup = groups.firstOrNull()

                _uiState.value = HomeUiState(

                    groupWithRecentActivity = recentGroup,
                    groups = groups
                )
            }else if(userResult is DataResult.Error){

                val message = userResult.errorMessage
                    .orEmpty()
                    .ifEmpty { "Error getting user data" }
                showErrorMessage(message)
            }
        }
    }

    fun onGroupClicked(group: Group) {
        // TODO: navigate to group details page
        viewModelScope.launch {
            _navigationEvents.emit(HomeNavigationEvent.ToGroupDetails(group.id))
        }
    }

    fun onAddNewGroupClicked(){
        viewModelScope.launch {
            _navigationEvents.emit(HomeNavigationEvent.ToCreateGroup)
        }
    }
}