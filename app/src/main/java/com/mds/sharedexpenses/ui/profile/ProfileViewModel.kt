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
    // TODO: uncomment when a user Repositry exists
    // private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()


    fun onNotificationsChange(isEnabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(notificationsEnabled = isEnabled)
        }
    }

    fun updateDetailsButtonClicked() {
        val currentState = _uiState.value
        println("Updating details: Name=${currentState.name}, Email=${currentState.email}, Notifications=${currentState.notificationsEnabled}")

        // TODO: save data in firebase!
        // val currentState = _uiState.value
        // userRepository.updateUserProfile(currentState)
    }

    fun onNameChange(newName: String) {
        currentUser.value?.name = newName
        //_uiState.update { it.copy(name = newName) }

    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }
}