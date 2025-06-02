package com.vlamik.retask.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.vlamik.core.data.datasource.TaskDataSource
import com.vlamik.core.data.datasource.db.ReTaskDatabase
import com.vlamik.core.data.datasource.db.dao.TaskDao
import com.vlamik.core.data.datasource.db.sources.RoomTaskDataSource
import com.vlamik.core.data.repository.AppRepositoryImpl
import com.vlamik.core.data.repository.TaskRepositoryImpl
import com.vlamik.core.domain.repository.AppRepository
import com.vlamik.core.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class DataModule {
    protected open fun internalDataStore(context: Context) = context.dataStore

    @Provides
    @Singleton
    fun dataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        internalDataStore(context)

    @Provides
    @Singleton
    fun provideReTaskDatabase(app: Application): ReTaskDatabase {
        return Room.databaseBuilder(app, ReTaskDatabase::class.java, ReTaskDatabase.DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun providesAppRepository(
        repo: AppRepositoryImpl
    ): AppRepository {
        return repo
    }

    @Provides
    @Singleton
    fun provideTaskRepository(repo: TaskRepositoryImpl): TaskRepository {
        return repo
    }

    @Provides
    @Singleton
    fun provideTaskDataSource(dataSource: RoomTaskDataSource): TaskDataSource = dataSource

    @Provides
    @Singleton
    fun provideTaskDao(db: ReTaskDatabase): TaskDao = db.taskDao

    companion object {
        private const val DATA_STORE = "store"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE)
    }
}
