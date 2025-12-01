package com.mds.sharedexpenses.ui.profile

import androidx.activity.result.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val notificationsEnabled: Boolean = false
)

class ProfileViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = currentUser.value?.name ?: "Loading...",
            email = currentUser.value?.email ?: "Loading...",
            notificationsEnabled = currentUser.value?.notifications ?: false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        currentUser.observeForever { user ->
            user?.let {
                _uiState.update { currentState ->
                    currentState.copy(
                        name = it.name,
                        email = it.email,
                        notificationsEnabled = it.notifications ?: false
                    )
                }
            }
        }
    }
    fun onNotificationsChange(isEnabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(notificationsEnabled = isEnabled)
        }
    }

    suspend fun updateDetailsButtonClicked() {
        val currentState = _uiState.value

        appRepository.users.addUser(
            User(name = currentState.name, email = currentState.email)
        )
    }

    fun logUserOut(){
        appRepository.logout()
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }
}
