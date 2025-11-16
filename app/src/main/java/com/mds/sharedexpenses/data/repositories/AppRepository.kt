package com.mds.sharedexpenses.data.repositories
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.models.User
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


    // --- Base functions (authentification, etc.) ---

    suspend fun login(email: String, password: String) = firebaseRepository.login(email, password)


    fun logout() = firebaseRepository.logout()

    suspend fun registerUser(email: String, password: String, name: String) =
        firebaseRepository.registerUser(email, password, name)

    suspend fun checkLoginStatus() = firebaseRepository.checkLoginStatus()

}
