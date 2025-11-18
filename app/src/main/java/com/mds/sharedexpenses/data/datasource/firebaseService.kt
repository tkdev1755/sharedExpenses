package com.mds.sharedexpenses.data.datasource

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseService private constructor(){
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseDatabase by lazy {  FirebaseDatabase.getInstance()}
    val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance()}
    val functions : FirebaseFunctions by lazy {FirebaseFunctions.getInstance()}
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
    fun logout(){
        auth.signOut()
    }
    suspend fun registerUser(email: String, password: String, displayName: String): Boolean {
        try {
            val result = auth.createUserWithEmailAndPassword(email,password).await()
            return result.user != null
        }
        catch (e: Exception){
            return false
        }
    }


    fun getCurrentUser(): Map<String, Any>? {
        return try {
            val snapshot = auth.currentUser
            if (snapshot == null){
                null
            }else{
                val uid = snapshot.uid
                val email = snapshot.email
                val displayName = snapshot.displayName
                {
                    "uid" to uid
                    "email" to email
                    "displayName" to displayName
                } as Map<String, Any>
            }
        } catch (e: Exception) {
            println("Error while getting current user data")
            e.printStackTrace()
            null
        }
    }

    fun getCurrentUserUID() : String{
        if (auth.currentUser == null){
            throw  Exception("User is not logged in")
        }
        return auth.currentUser!!.uid
    }

    fun getUserDirectory() : DatabaseReference {
        return db.getReference("users/${getCurrentUserUID()}")
    }


    fun getGroupsDirectory() : DatabaseReference {
        return db.getReference("groups")
    }

    fun getGroupDirectory(id : String) : DatabaseReference {
        return getGroupsDirectory().child(id)
    }


    fun checkLoginStatus() : Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    suspend fun saveNotificationToken() {
        val token = messaging.token
        val uid = auth.currentUser?.uid ?: return
        try {
            db.getReference("users/$uid/fcmToken").setValue(token).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun callCloudFunction(name : String, data : Map<String,*>) : Task<String> {
        return functions.getHttpsCallable(name)
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }
}

