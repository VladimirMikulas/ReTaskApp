package com.vlamik.core.data.repository

import com.vlamik.core.data.datasource.TaskDataSource
import com.vlamik.core.data.datasource.db.entity.TaskEntity
import com.vlamik.core.data.mappers.TaskMapper.toTaskDetailModel
import com.vlamik.core.data.mappers.TaskMapper.toTaskItemModel
import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.models.TaskItemModel
import com.vlamik.core.domain.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val taskDataSource: TaskDataSource) :
    TaskRepository {
    override suspend fun initTasksData() {
        taskDataSource.initTasksData()
    }

    // Helper function that creates a "ticker" Flow
    private fun tickerFlow(periodMillis: Long = 1000L) = flow {
        while (true) {
            emit(Unit)
            delay(periodMillis)
        }
    }

    override fun getAllTasks(): Flow<Result<List<TaskItemModel>>> {
        val taskEntitiesFlow: Flow<Result<List<TaskEntity>>> = taskDataSource.getAllTasks()
        // Combine the task entities flow with a ticker flow.
        // The 'combine' operator ensures that this lambda block is re-executed
        // whenever either 'taskEntitiesFlow' emits new data OR 'tickerFlow()' emits a tick (e.g., every second).
        return combine(taskEntitiesFlow, tickerFlow()) { entitiesResult, _ ->
            entitiesResult.map { taskEntities ->
                taskEntities.map { entity ->
                    entity.toTaskItemModel()
                }
            }
        }.catch { e ->
            emit(Result.failure(e))
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