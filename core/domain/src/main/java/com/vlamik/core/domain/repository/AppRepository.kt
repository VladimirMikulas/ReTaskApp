package com.vlamik.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun saveHasBeenOpenedPreference()
    fun hasBeenOpened(): Flow<Boolean>
}