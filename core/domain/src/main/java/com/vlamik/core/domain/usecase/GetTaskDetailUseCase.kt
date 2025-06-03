package com.vlamik.core.domain.usecase


import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskDetailUseCase(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(taskId: Long): Flow<Result<TaskDetailModel?>> {
        return taskRepository.getTaskById(taskId)
    }
}