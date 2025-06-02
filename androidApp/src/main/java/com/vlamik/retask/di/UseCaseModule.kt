package com.vlamik.retask.di

import com.vlamik.core.data.repository.AppRepositoryImpl
import com.vlamik.core.domain.usecase.AppSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    @Singleton
    fun providesAppSettingUseCase(
        repo: AppRepositoryImpl
    ): AppSettingsUseCase {
        return AppSettingsUseCase(repo)
    }
}