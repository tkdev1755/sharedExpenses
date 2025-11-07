package com.mds.sharedexpenses.domain.repository

import com.mds.sharedexpenses.data.repositories.User

interface FirebaseRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun getUser(uid: String): User?
    suspend fun saveNotificationToken(token: String)
}