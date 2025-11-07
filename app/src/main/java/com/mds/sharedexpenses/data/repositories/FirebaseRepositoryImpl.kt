package com.mds.sharedexpenses.data.repositories
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.domain.repository.FirebaseRepository


class FirebaseRepositoryImpl(
    private val firebaseService: FirebaseService
) : FirebaseRepository {

    override suspend fun login(email: String, password: String): Boolean {
        return firebaseService.login(email, password)
    }

    override suspend fun getUser(uid: String): User? {
        val data = firebaseService.getUser(uid) ?: return null
        return User(
            uid = uid,
            email = data["email"] as? String ?: "",
            name = data["name"] as? String ?:""
        )
    }

    override suspend fun saveNotificationToken(token: String) {
        firebaseService.saveNotificationToken(token)
    }
}