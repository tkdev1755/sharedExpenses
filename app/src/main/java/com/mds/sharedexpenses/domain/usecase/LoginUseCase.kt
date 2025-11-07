package com.mds.sharedexpenses.domain.usecase

import com.mds.sharedexpenses.domain.repository.FirebaseRepository

class LoginUseCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}

class GetUserUseCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke(uid: String) = repository.getUser(uid)
}

class SaveNotificationTokenUseCase(private val repository: FirebaseRepository) {
    suspend operator fun invoke(token: String) = repository.saveNotificationToken(token)
}