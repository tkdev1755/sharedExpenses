package com.mds.sharedexpenses.data.repositories
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import kotlinx.coroutines.tasks.await
import com.mds.sharedexpenses.data.models.User
import java.time.format.DateTimeFormatter

class FirebaseRepositoryImpl(
    private val firebaseService: FirebaseService
) : FirebaseRepository {

    override val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm")
    companion object{
        // This is the name of the function to call when wanting to notify a user about a unpaid expense
        public val notifyUserFunction = "notifyUser"
        // This is the name of the function to call when wanting to invite a user to a group
        public val inviteUserFunction = "inviteUser"

        public val removeUserFunction  = "removeUserFromGroup"
    }
    /**
     * Logs the selected user in
     * @return True if the user is correctly logged in, false otherwise
     * TODO : Update return type to have authContext with info about the user and more info if there was an error while trying to log in
     */
    override suspend fun login(email: String, password: String): Boolean {
        return firebaseService.login(email, password)
    }
    /**
     * Get the logged in user info
     * @return The logged in user info if existent
     */
    override fun getCurrentUser(): User? {
        val data = firebaseService.getCurrentUser() ?: return null
        return User(
            data["uid"] as String,
            email = data["email"] as? String ?: "",
            groups = mutableListOf<Group>(),
            name = data["name"] as? String ?:"",
        )
    }

    /**
     * Register the users in the authentification server
     * @return True if the user is logged in, false otherwise
     */
    override suspend fun registerUser(email: String, password: String, name : String) : Boolean{
        return firebaseService.registerUser(email, password, name)
    }


    /**
     * TODO : Finish to implement this function
     * @return
     */
    override suspend fun saveNotificationToken() : Boolean {
        return firebaseService.saveNotificationToken()
    }

    /**
     * Checks whether a user has logged in into our app or not
     * @return True if the user is logged in, false otherwise
     */
    override fun checkLoginStatus(): Boolean {
        return firebaseService.checkLoginStatus()
    }
    /**
     * Logs out the currently logged in user.
     */
    override fun logout() {
        firebaseService.logout()
    }
    /**
     * Returns the logged in User UID
     *
     * @return The logged in user UID
     */
    override fun getCurrentUserUID(): String {
        return firebaseService.getCurrentUserUID()
    }

    /**
     * Returns the logged in User directory database Reference
     *
     * @return The database reference pointing to current user directory (where its info is stored)
     */
    override fun getUserDirectory(): DatabaseReference {
        return firebaseService.getUserDirectory()
    }

    override fun getUsersDirectory(): DatabaseReference {
        return firebaseService.getUsersDirectory()
    }

    override fun getPublicUserDirectory(uid:String): DatabaseReference{
        return getUsersDirectory().child(uid)
    }


    /**
     * Returns the groupsDirectory database Reference
     *
     * @return The database reference pointing to the groups directory (where groups are stored).
     */
    override fun getGroupsDirectory(): DatabaseReference {
        return firebaseService.getGroupsDirectory()
    }

    /**
     * Calls a given cloud function with the provided data
     *
     * This is a generic function. The caller specifies the expected return type.
     *
     * @return A [DataResult] which is either:
     *         - [DataResult.Success<T>] containing the data if the fetch was successful.
     *         - [DataResult.Error] with an error code and message if the fetch failed.
     *         - [DataResult.NotFound] if the data does not exist at the specified location.
     */
    override suspend fun callCloudFunction(functionName: String, data: Map<String, *>): DataResult<Boolean> {
        val errorCodes = mapOf(
            "USER_NOT_FOUND" to "404",
            "PERMISSION_ERROR" to "403",
            "SERVER_ERROR" to 500,
        )
        try {
            val functionResult = firebaseService.callCloudFunction(functionName, data)
            functionResult.await()
            if (functionResult.isSuccessful){
                val data = functionResult.result
                when (data) {
                   "invite_successful;;" -> return DataResult.Success(true)
                   "invite_failed;${errorCodes["USER_NOT_FOUND"]};" -> return DataResult.Error("${errorCodes["USER_NOT_FOUND"]}", "Invite failed because the user does not exist")
                    "invite_failed;${errorCodes["PERMISSION_ERROR"]};" -> return DataResult.Error("${errorCodes["PERMISSION_ERROR"]}", "Invite failed because the user does not have permission to invite other users")
                    "no_message" -> return DataResult.Success(true)
                }
            }
            else{
                val errorInfo = functionResult.exception
                return DataResult.Error("${errorCodes["SERVER_ERROR"]}", "${errorInfo?.message}")
            }
        }
        catch (e: Exception){
            print("ERROR WHEN CALLING CLOUD FUNCTION EXCEPTION -> ${e.printStackTrace()}")
         return DataResult.Error("404", e.message)
        }

        return DataResult.Error("500", "Unknown error")
    }

    /**
     * Fetches data from a given DatabaseReference and returns a DataResult.
     *
     * This is a generic function. The caller specifies the expected return type.
     *
     * @return A [DataResult] which is either:
     *         - [DataResult.Success<Boolean>] containing true if the cloud function execution was successful.
     *         - [DataResult.Error] with an error code and message if the cloud function failed.
     *         - [DataResult.NotFound] if the data does not exist at the specified location.
     */
    override suspend fun <T> fetchDBRef(dbRef: DatabaseReference): DataResult<T> {
        return try {
            val dataSnapshot = dbRef.get().await()

            if (!dataSnapshot.exists()) {
                return DataResult.NotFound
            }

            // Safely cast to the expected type T. 'as? T' returns null if the cast fails.
            val data = dataSnapshot.value as? T
            if (data != null) {
                DataResult.Success(data)
            } else {
                // This error means the data exists, but it's not the type the caller expected.
                DataResult.Error(
                    "TYPE_MISMATCH",
                    "Data found, but it could not be cast to the expected type."
                )
            }
        } catch (e: Exception) {
            // Here you inspect the exception to return a specific error
            when (e) {
                is DatabaseException -> {
                    // Firebase-specific exceptions often contain useful codes.
                    // Example: PERMISSION_DENIED, NETWORK_ERROR
                    val code = e.message?.let {
                        when {
                            it.contains("PERMISSION_DENIED", ignoreCase = true) -> "PERMISSION_DENIED"
                            it.contains("NETWORK_ERROR", ignoreCase = true) -> "NETWORK_ERROR"
                            else -> "FIREBASE_ERROR"
                        }
                    } ?: "UNKNOWN_FIREBASE_ERROR"

                    DataResult.Error(code, e.message)
                }
                else -> {
                    // For any other unexpected exception
                    DataResult.Error("UNKNOWN_ERROR", e.message)
                }
            }
        }
    }

    /**
     * Writes data to a given DatabaseReference and returns if the write was successful.
     *
     * This is a generic function. The caller specifies the expected return type.
     * For example:
     *   - writeToDBRef<Map<String, Any>>(ref) -> Tries to write a Map<String,Dynamic> to the given database reference
     *   - writeToDBRef<List<String>>(ref)    -> Tries to write a List of Strings to the given database reference
     *   - writeToDBRef<String>(ref)           -> Tries to write a String to the given database reference
     *
     * @param T The expected data type (e.g., Map<*, *>, List<*>, String, Long).
     * @param dbRef The DatabaseReference pointing to the data to fetch.
     * @param createChildReference boolean to create a child containing the data or not
     * @return A [DataResult] which is either:
     *         - [DataResult.Success<T>] containing True if the write operation was successful.
     *         - [DataResult.Error] with an error code and message if the write operation failed.
     */
    override suspend fun <T> writeToDBRef(dbRef : DatabaseReference, value: T) : DataResult<Boolean> {
        try {
            val result = dbRef.setValue(value)
            result.await()
            if (result.isSuccessful) return DataResult.Success(result.isSuccessful)
            else return DataResult.Error("500", "Unknown error")
        }
        catch(e : Exception){
            return DataResult.Error("404", e.message)
        }


    }
    /**
     * Generates a new child location using a unique key and returns a DatabaseReference to it. This is useful when the children represent a list of items
     *

     * @param dbRef The DatabaseReference pointing to the directory where the new child reference will be.
     * @return A [DataResult] which is either:
     *         - [DataResult.Success<T>] Containing the new database reference to use .
     *         - [DataResult.Error] with an error code and message if the database reference couldn't be created
     */
    override fun createChildReference(dbRef: DatabaseReference): DataResult<DatabaseReference> {
        try {
            val result = dbRef.push()
            return DataResult.Success(result)
        } catch (e: Exception){
            return DataResult.Error("404", e.message)
        }
    }

    /**
     * Deletes the data present in a given DatabaseReference and returns if the delete was successful.

     * @param dbRef The DatabaseReference pointing to the data to fetch.
     * @return A [DataResult] which is either:
     *         - [DataResult.Success<T>] containing True if the delete was successful.
     *         - [DataResult.Error] with an error code and message if the delete failed.
     *         - [DataResult.NotFound] if the data does not exist.
     */
    override suspend fun  deleteDBRef(dbRef : DatabaseReference) : DataResult<Boolean> {
        try {
            val result = dbRef.removeValue()
            result.await()
            if (result.isSuccessful) return DataResult.Success(result.isSuccessful)
            else return DataResult.Error("500", "Unknown error")
        }
        catch(e : Exception){
            return DataResult.Error("404", e.message)
        }


    }

    /**
     * Gets the group directory based on a specific ID
     *
     * @return The database reference pointing to current user directory (where its info is stored)
     */
    override fun getGroupDirectory(id : String ): DatabaseReference {
        return firebaseService.getGroupDirectory(id)
    }


}
