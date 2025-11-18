package com.mds.sharedexpenses.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.repositories.FirebaseRepositoryImpl
import com.mds.sharedexpenses.domain.usecase.GetUserUseCase
import com.mds.sharedexpenses.domain.usecase.LoginUseCase
import com.mds.sharedexpenses.domain.usecase.SaveNotificationTokenUseCase
import kotlinx.coroutines.launch


class HomeViewModel() : ViewModel() {
    fun onButtonClicked(){
        
        fun callback (success: Boolean) = {println("This message is called when the function ends ? ${success}")}

        println("Button clicked!")
    }
}