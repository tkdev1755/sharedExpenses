package com.mds.sharedexpenses.domain.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.utils.DataResult
import java.time.format.DateTimeFormatter

interface FirebaseRepository {
    val formatter : DateTimeFormatter
    suspend fun login(email: String, password: String): DataResult<Boolean>
    suspend fun registerUser(email: String, password: String, name :String ) : Boolean
    fun getCurrentUser(): User?
    fun checkLoginStatus(): Boolean
    fun logout()
    fun getCurrentUserUID(): String
    fun getUserDirectory(): DatabaseReference
    fun getUsersDirectory(): DatabaseReference
    fun getPublicUserDirectory(uid:String): DatabaseReference

    fun getGroupsDirectory(): DatabaseReference
    suspend fun <T> fetchDBRef(dbRef: DatabaseReference,): DataResult<T>
    suspend fun <T> writeToDBRef(dbRef : DatabaseReference,value: T) : DataResult<Boolean>
    fun createChildReference(dbRef : DatabaseReference) : DataResult<DatabaseReference>
    suspend fun deleteDBRef(dbRef : DatabaseReference) : DataResult<Boolean>
    fun getGroupDirectory(id : String): DatabaseReference
    suspend fun saveNotificationToken() : Boolean
    suspend fun callCloudFunction(functionName:String, data:Map<String,*>) : DataResult<Boolean>
}
