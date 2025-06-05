package com.vlamik.retask.di

import com.vlamik.retask.commons.AndroidStringResourceProvider
import com.vlamik.retask.commons.StringResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindStringResourceProvider(
        androidStringResourceProvider: AndroidStringResourceProvider
    ): StringResourceProvider
}