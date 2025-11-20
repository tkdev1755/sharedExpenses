package com.mds.sharedexpenses.ui.addgroup

import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.launch

class AddGroupViewModel : BaseViewModel() {

    fun createGroup(name: String, description: String) {
        viewModelScope.launch {
            // get current user
            val userResult = appRepository.users.getCurrentUserData()

            if (userResult is DataResult.Success) {

                val owner = userResult.data


                val group = Group(
                    name = name,
                    description = description,
                    users = mutableListOf(owner)
                )

                val result = appRepository.groups.createGroup(group)

                if (result is DataResult.Error) {
                    val message = result.errorMessage
                        .orEmpty()
                        .ifEmpty { "Error creating group." }
                    showErrorMessage(message)
                }

                } else if (userResult is DataResult.Error) {

                val message = userResult.errorMessage
                    .orEmpty()
                    .ifEmpty { "Error getting user data" }
                showErrorMessage(message)
            }
        }
    }
}
