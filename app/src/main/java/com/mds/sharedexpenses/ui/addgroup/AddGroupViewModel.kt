package com.mds.sharedexpenses.ui.addgroup

import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.launch

@Deprecated("unused")
class AddGroupViewModel : BaseViewModel() {

    fun createGroup(name: String, description: String) {
        viewModelScope.launch {
            // get current user
            if (!currentUser.isInitialized){
                println("User data isn't initialized, getting it now")
                getUserData()
            }
            if (currentUser.value == null){
                showErrorMessage("Error getting user data")
                return@launch
            }

            val owner = currentUser.value!!

            val group = Group(
                name = name,
                description = description,
                users = mutableListOf(owner)
            )

            val result = appRepository.groups.createGroup(group)

            if (result is DataResult.Error) {
                val message = result.errorMessage
                    .orEmpty()
                    .ifEmpty { "Error while creating a group." }
                showErrorMessage(message)
            }

        }
    }
}
