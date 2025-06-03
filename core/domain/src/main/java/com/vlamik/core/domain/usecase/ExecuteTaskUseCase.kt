package com.vlamik.core.domain.usecase

import com.vlamik.core.domain.repository.TaskRepository

class ExecuteTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) {
        taskRepository.executeTask(taskId)
    }
}