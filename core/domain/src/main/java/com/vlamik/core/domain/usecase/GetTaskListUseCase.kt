package com.vlamik.core.domain.usecase


import com.vlamik.core.domain.models.TaskItemModel
import com.vlamik.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskListUseCase(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<Result<List<TaskItemModel>>> {
        return taskRepository.getAllTasks()
    }
}