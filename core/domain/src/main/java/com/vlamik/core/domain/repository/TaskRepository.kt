package com.vlamik.core.domain.repository

import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.models.TaskItemModel
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun initTasksData()
    fun getAllTasks(): Flow<Result<List<TaskItemModel>>>
    fun getTaskById(taskId: Long): Flow<Result<TaskDetailModel?>>
    suspend fun executeTask(taskId: Long)

}