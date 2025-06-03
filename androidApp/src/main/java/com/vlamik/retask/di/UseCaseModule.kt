package com.vlamik.retask.di

import com.vlamik.core.data.repository.AppRepositoryImpl
import com.vlamik.core.data.repository.TaskRepositoryImpl
import com.vlamik.core.domain.usecase.AppSettingsUseCase
import com.vlamik.core.domain.usecase.ExecuteTaskUseCase
import com.vlamik.core.domain.usecase.GetTaskDetailUseCase
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

    @Provides
    @Singleton
    fun providesGetTaskDetailUseCase(
        repo: TaskRepositoryImpl
    ): GetTaskDetailUseCase {
        return GetTaskDetailUseCase(repo)
    }

    @Provides
    @Singleton
    fun providesExecuteTaskUseCase(
        repo: TaskRepositoryImpl
    ): ExecuteTaskUseCase {
        return ExecuteTaskUseCase(repo)
    }
}