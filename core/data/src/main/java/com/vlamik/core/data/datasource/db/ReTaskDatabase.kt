package com.vlamik.core.data.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vlamik.core.data.datasource.db.dao.TaskDao
import com.vlamik.core.data.datasource.db.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
abstract class ReTaskDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao

    companion object {
        const val DATABASE_NAME = "retask_db"
    }
}