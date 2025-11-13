package com.mds.sharedexpenses.ui.groupdetail

// 1. Add these necessary imports
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// 2. REMOVE this incorrect line:
// private val GroupDetailViewModel.viewModelScope: Any

// 3. Inherit from ViewModel()
class GroupDetailViewModel : ViewModel() {

    fun onButtonClicked(){
        println("Button clicked!")
    }
}