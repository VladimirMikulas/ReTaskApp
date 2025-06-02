package com.vlamik.core.domain.usecase

import com.vlamik.core.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow

class AppSettingsUseCase(
    private val appRepository: AppRepository,
) {

    fun hasBeenOpened(): Flow<Boolean> {
        return appRepository.hasBeenOpened()
    }

    suspend fun appOpened() {
        appRepository.saveHasBeenOpenedPreference()
    }
}
