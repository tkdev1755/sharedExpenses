package com.mds.sharedexpenses.ui.home

import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthStep {
    LOGIN,
    SIGNUP,
    ONBOARDING,
    WELCOME,
}

data class HomeUiState(
    val groupWithRecentActivity: Group? = null,
    val groups: List<Group> = emptyList(),
    val showGroupAddSheet: Boolean = false,
    val authenticationStep: AuthStep? = AuthStep.WELCOME,
    val isLoggedIn : Boolean = false,
    val notificationStatus : Boolean = false,
    //Sheet
    val activeSheet: SheetTypeHome? = null,
)

enum class SheetTypeHome  { ADD_GROUP }

sealed class HomeNavigationEvent {
    data class ToGroupDetails(val groupId: String) : HomeNavigationEvent()
}

class HomeViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        fetchGroups()
    }
    private fun fetchGroups() {
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
    }
    fun updateGroup() {
        viewModelScope.launch {
            val userResult = appRepository.users.getCurrentUserData()
            if(userResult is DataResult.Success){
                val user = userResult.data
                val groups = user.groups.toList()

                val recentGroup = groups.firstOrNull()
                _uiState.update { state ->
                    state.copy(
                        groupWithRecentActivity = recentGroup,
                        groups = groups
                    )
                }
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
    fun checkLoginStatus() : Boolean {
        return appRepository.checkLoginStatus()
    }
    fun onNotificationActivation(value:Boolean){
        _uiState.update { state -> state.copy(
            notificationStatus = value
        ) }
        return
    }
    fun onDisconnect(){
        _uiState.value = _uiState.value.copy(authenticationStep = AuthStep.WELCOME)
    }
     fun logout(){
        appRepository.logout()
    }
    fun onAddNewGroupClicked(){
        _uiState.update { it.copy(activeSheet = SheetTypeHome.ADD_GROUP) }
    }
    fun goToAuthStep(authStep: AuthStep){
        _uiState.value = _uiState.value.copy(authenticationStep = authStep)
    }
    fun onLogin(email:String, password:String){
        viewModelScope.launch {
            appRepository.login(email,password)
            hideAuthenticationFlow()
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
                        _uiState.value =
                            _uiState.value.copy(authenticationStep = AuthStep.ONBOARDING)
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
        hideAuthenticationFlow()
        viewModelScope.launch {
            getUserData()
        }
    }
    private fun hideAuthenticationFlow() {
        _uiState.update { it.copy(authenticationStep = null) }
    }
    fun createNewGroup(name: String, description: String){
        val owner = currentUser.value
        if (owner == null) {
            showErrorMessage("Cannot create group: User is not logged in.")
            return
        }

        if(name.isEmpty() || description.isEmpty()){
            showErrorMessage("Group name and description cannot be empty.")
            return
        }

        val group = Group(
            name = name,
            description = description,
            users = mutableListOf(owner)
        )

        viewModelScope.launch {
            val result = appRepository.groups.createGroup(group)
            if (result is DataResult.Error) {
                val message = result.errorMessage
                    .orEmpty()
                    .ifEmpty { "Error while creating a group." }
                showErrorMessage(message)
            }
        }
    }
    fun onDismissRequest(){
        _uiState.update { it.copy(activeSheet = null) }
    }
}
