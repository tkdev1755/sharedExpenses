package com.mds.sharedexpenses.ui.profile

import androidx.compose.animation.core.copy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.atomics.update

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val notificationsEnabled: Boolean = false
)

class ProfileViewModel {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onNotificationsChange(isEnabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(notificationsEnabled = isEnabled)
        }
    }

    fun updateDetailsButtonClicked() {
        // Now the ViewModel has access to the current state when the button is clicked
        val currentState = _uiState.value
        println("Updating details: Name=${currentState.name}, Email=${currentState.email}, Notifications=${currentState.notificationsEnabled}")
        // TODO: Add logic to save this data to a repository or Firebase

    }

    // You would add similar functions for name and email changes
    fun onNameChange(newName: String) {
        println("New Name: ${newName}")
        _uiState.update { it.copy(name = newName) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }
}