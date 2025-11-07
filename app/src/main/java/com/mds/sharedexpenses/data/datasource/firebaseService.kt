package com.mds.sharedexpenses.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseService private constructor(){
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseDatabase by lazy {  FirebaseDatabase.getInstance()}
    val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance()}
    companion object{
        @Volatile private var instance: FirebaseService? = null
        fun getInstance() = instance ?: synchronized(this){
            instance ?: FirebaseService().also { instance = it }
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUser(uid: String): Map<String, Any>? {
        return try {
            val snapshot = db.getReference("users/$uid").get().await()
            snapshot.value as? Map<String, Any>
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun saveNotificationToken(token: String) {
        val uid = auth.currentUser?.uid ?: return
        try {
            db.getReference("users/$uid/fcmToken").setValue(token).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

