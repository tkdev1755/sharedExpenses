package com.mds.sharedexpenses.domain.di

import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.repositories.AppRepository

object AppContainer {
    private val firebaseService = FirebaseService.getInstance()

    val appRepository = AppRepository(firebaseService)
}


