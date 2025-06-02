package com.vlamik.core.data.datasource

import com.vlamik.core.data.datasource.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskDataSource {
    suspend fun initTasksData() {}
    fun getAllTasks(): Flow<Result<List<TaskEntity>>>
    fun getTaskById(taskId: Long): Flow<Result<TaskEntity?>>
    suspend fun executeTask(taskId: Long)
}