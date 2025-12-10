package com.mds.sharedexpenses.data.repositories
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

// /data/repositories/AppRepository.kt


/**
 * Entrypoint for all data-related functions in the app
 * Viewmodels should be only depending on this Repository
 */
class AppRepository(private val firebaseService: FirebaseService) {


    private val firebaseRepository: FirebaseRepository = FirebaseRepositoryImpl(firebaseService)


    val groups: FIRGroupRepository = FIRGroupRepository(firebaseRepository)
    val expenses: FIRExpenseRepository = FIRExpenseRepository(firebaseRepository)
    val transactions: FIRTransactionRepository = FIRTransactionRepository(firebaseRepository)
    val users: FIRUserRepository = FIRUserRepository(firebaseRepository)
    public var hasUpdatedFirebaseFCM : Boolean = false

    // --- Base functions (authentification, etc.) ---

    suspend fun login(email: String, password: String) : DataResult<Boolean> = firebaseRepository.login(email, password)


    fun logout() = firebaseRepository.logout()

    fun getUserID() = firebaseRepository.getCurrentUserUID()

    suspend fun registerUser(email: String, password: String, name: String) =
        firebaseRepository.registerUser(email, password, name)
    suspend fun saveFCMToken() : Boolean {
        return firebaseRepository.saveNotificationToken()
    }
    fun checkLoginStatus() = firebaseRepository.checkLoginStatus()

}
