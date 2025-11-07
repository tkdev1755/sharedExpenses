package com.mds.sharedexpenses.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.repositories.FirebaseRepositoryImpl
import com.mds.sharedexpenses.domain.usecase.GetUserUseCase
import com.mds.sharedexpenses.domain.usecase.LoginUseCase
import com.mds.sharedexpenses.domain.usecase.SaveNotificationTokenUseCase
import kotlinx.coroutines.launch

object HomeModelFactory {
    private val firebaseService = FirebaseService.getInstance()
    private val repository = FirebaseRepositoryImpl(firebaseService)

    val loginUseCase = LoginUseCase(repository)
    val getUserUseCase = GetUserUseCase(repository)
    val saveTokenUseCase = SaveNotificationTokenUseCase(repository)

    fun createHomeViewModel() = HomeViewModel(loginUseCase, getUserUseCase, saveTokenUseCase)
}
class HomeViewModel(  private val loginUseCase: LoginUseCase,
                      private val getUserUseCase: GetUserUseCase,
                      private val saveTokenUseCase: SaveNotificationTokenUseCase
) : ViewModel() {
    fun onButtonClicked(){
        
        fun callback (success: Boolean) = {println("This message is called when the function ends ? ${success}")}
        viewModelScope.launch {
            val success = loginUseCase("khetibh@gmail.com", "ahah204")
            callback(success)
        }

        println("Button clicked!")
    }
}