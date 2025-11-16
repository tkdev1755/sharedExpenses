package com.mds.sharedexpenses.data.repositories
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.domain.repository.FirebaseRepository

// /data/repositories/AppRepository.kt


/**
 * Façade qui sert de point d'entrée unique pour toutes les opérations de données de l'application.
 * Les ViewModels ne devraient dépendre que de cette classe.
 */
class AppRepository(private val firebaseService: FirebaseService) {

    // Initialisation de l'implémentation de base de Firebase
    private val firebaseRepository: FirebaseRepository = FirebaseRepositoryImpl(firebaseService)

    // Initialisation des repositories spécifiques en leur passant la dépendance nécessaire
    val groups: FIRGroupRepository = FIRGroupRepository(firebaseRepository)
    val expenses: FIRExpenseRepository = FIRExpenseRepository(firebaseRepository)
    val transactions: FIRTransactionRepository = FIRTransactionRepository(firebaseRepository)
    val users: FIRUserRepository = FIRUserRepository(firebaseRepository)
    // val debts: FIRDebtRepository = FIRDebtRepository(firebaseRepository) // Si vous en avez un

    // --- Fonctions de base (authentification, etc.) ---
    // Vous pouvez exposer directement les fonctions du firebaseRepository de base si nécessaire

    suspend fun login(email: String, password: String) = firebaseRepository.login(email, password)


    fun logout() = firebaseRepository.logout()

    suspend fun registerUser(email: String, password: String, name: String) =
        firebaseRepository.registerUser(email, password, name)

    suspend fun checkLoginStatus() = firebaseRepository.checkLoginStatus()

}
