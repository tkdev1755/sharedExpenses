package com.mds.sharedexpenses.ui.home

import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.mds.sharedexpenses.domain.di.AppContainer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class AuthStep {
    LOGIN,
    SIGNUP,
    ONBOARDING,
    WELCOME,
}

data class HomeUiState(
    val groupWithRecentActivity: Group? = null, // TODO: if there are no groups with recent activity, dont show the corresponding block
    val groups: List<Group> = emptyList(),
    val showLoginSheet: Boolean = false,
    val showGroupAddSheet: Boolean = false,
    val currentStep : AuthStep = AuthStep.WELCOME,
    val isLoggedIn : Boolean = false
)

sealed class HomeNavigationEvent {
    data class ToGroupDetails(val groupId: String) : HomeNavigationEvent()
    object ToCreateGroup : HomeNavigationEvent()
}

class HomeViewModel : BaseViewModel() {
    // TODO: add Group Repository
    // private val groupRepository = GroupRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        fetchGroups()
    }
    private fun fetchGroups() {
        // TODO: fetch groups from repository
        if (!currentUser.isInitialized && (appRepository.checkLoginStatus())){
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
        else if (currentUser.isInitialized){
            _uiState.value = HomeUiState(
                groupWithRecentActivity = currentUser.value?.groups?.firstOrNull(),
                groups = currentUser.value?.groups!!
            )
        }
        /**/
    }

    fun onGroupClicked(group: Group) {
        // TODO: navigate to group details page
        viewModelScope.launch {
            _navigationEvents.emit(HomeNavigationEvent.ToGroupDetails(group.id))
        }
    }
    suspend fun checkLoginStatus() : Boolean {
        return appRepository.checkLoginStatus()
    }

    fun onDisconnect(){
        _uiState.value = _uiState.value.copy(showLoginSheet = true)
    }
     fun logout(){
        appRepository.logout()
    }
    fun onAddNewGroupClicked(){
        viewModelScope.launch {
            _navigationEvents.emit(HomeNavigationEvent.ToCreateGroup)
        }
    }

    fun onSheetDismiss() {
        _uiState.value = _uiState.value.copy(showLoginSheet = false)
    }
    fun goToLogin(){
        _uiState.value = _uiState.value.copy(currentStep = AuthStep.LOGIN)
    }
    fun goToSignUp(){
        _uiState.value = _uiState.value.copy(currentStep = AuthStep.SIGNUP)
    }
    fun goToOnboarding(){
        _uiState.value = _uiState.value.copy(currentStep = AuthStep.ONBOARDING)
    }

    fun onLogin(email:String, password:String){
        viewModelScope.launch {
            appRepository.login(email,password)
            _uiState.value = _uiState.value.copy(showLoginSheet = false)
        }
    }

    fun onSignUp(email:String, password:String, name:String, phone:String){
        viewModelScope.launch {
            val result: Boolean = appRepository.registerUser(email, password,name)
            if (!result){
                showErrorMessage("Unable to register user")
            }
            else{
                val userID = appRepository.getUserID()
                val newUser: User = User(
                    userID,
                    name,
                    email,
                    phone,
                )
                val userRes: DataResult<Boolean> = appRepository.users.addUser(newUser)
                when (userRes) {
                    is DataResult.Success -> {
                        _uiState.value = _uiState.value.copy(showLoginSheet = false)
                    }
                    is DataResult.Error -> {
                        showErrorMessage(userRes.errorMessage.orEmpty())
                    }

                    DataResult.NotFound -> {
                        showErrorMessage("User not found")
                    }
                }
            }
        }
    }

    fun finishOnboarding(){
        _uiState.value = _uiState.value.copy(showLoginSheet = false)
        viewModelScope.launch {
            getUserData()
        }
    }
}
