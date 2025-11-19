package com.mds.sharedexpenses.ui.home

import androidx.lifecycle.ViewModel
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val groupWithRecentActivity: Group? = null, // TODO: if there are no groups with recent activity, dont show the corresponding block
    val groups: List<Group> = emptyList()
)

class HomeViewModel : BaseViewModel() {
    // TODO: add Group Repository
    // private val groupRepository = GroupRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()


    init {
        fetchGroups()
    }

    private fun fetchGroups() {
        // TODO: fetch groups from repository
        //on error:
        showErrorMessage("oh no error!")
    }

    fun onGroupClicked(group: Group) {
        // TODO: navigate to group details page
        // (but maybe its simpler to handle that in the composable. Not the cleanest approach, but probably the quickest one to get working)
    }

    fun onAddNewGroupClicked(){

    }
}