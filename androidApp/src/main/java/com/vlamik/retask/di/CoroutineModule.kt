package com.vlamik.retask.di

import com.vlamik.core.commons.AppDispatcherProvider
import com.vlamik.core.commons.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class CoroutineModule {


    @Singleton
    @Provides
    fun providesDispatcherProvider(): DispatcherProvider {
        return AppDispatcherProvider()
    }
}
