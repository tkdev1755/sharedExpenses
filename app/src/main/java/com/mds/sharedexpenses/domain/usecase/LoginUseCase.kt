package com.mds.sharedexpenses.domain.usecase

import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.repositories.FIRDebtRepository

class LoginUseCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}

class GetUserUseCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke(uid: String) = repository.getCurrentUser()
}

class SaveNotificationTokenUseCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke(token: String) = repository.saveNotificationToken()
}

class CheckLoginStatusUseCase(private val repository: FirebaseRepository){
    suspend operator fun invoke() = repository.checkLoginStatus()
}
class AddDebtCase(private val repository: FirebaseRepository) {

}
class LoadDebtCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke() = FIRDebtRepository(repository).getUserDebt();
}
class RemoveDebtCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke() = FIRDebtRepository(repository);
}