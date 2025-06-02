package com.vlamik.core.data.repository

import com.vlamik.core.data.datasource.TaskDataSource
import com.vlamik.core.data.mappers.TaskMapper.toTaskDetailModel
import com.vlamik.core.data.mappers.TaskMapper.toTaskItemModel
import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.models.TaskItemModel
import com.vlamik.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val taskDataSource: TaskDataSource) :
    TaskRepository {
    override suspend fun initTasksData() {
        taskDataSource.initTasksData()
    }

    override fun getAllTasks(): Flow<Result<List<TaskItemModel>>> {
        return taskDataSource.getAllTasks().map { result ->
            result.map { taskEntities ->
                taskEntities.map { taskEntity ->
                    taskEntity.toTaskItemModel()
                }
            }
        }
    }

    override fun getTaskById(taskId: Long): Flow<Result<TaskDetailModel?>> {
        return taskDataSource.getTaskById(taskId).map { result ->
            result.map { taskEntity ->
                taskEntity?.toTaskDetailModel()
            }

        }
    }

    override suspend fun executeTask(taskId: Long) {
        taskDataSource.executeTask(taskId)
    }
}