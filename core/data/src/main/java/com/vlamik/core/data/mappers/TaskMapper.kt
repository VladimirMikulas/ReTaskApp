package com.vlamik.core.data.mappers

import com.vlamik.core.data.datasource.db.entity.TaskEntity
import com.vlamik.core.domain.commons.utils.calculateTaskStatusColor
import com.vlamik.core.domain.commons.utils.getTaskTimeStatus
import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.models.TaskItemModel

/**
 * Object responsible for mapping between domain models and the TaskEntity database entity.
 */
object TaskMapper {
    /**
     * Maps a TaskEntity to a simplified domain model TaskItemModel
     */
    fun TaskEntity.toTaskItemModel(currentTime: Long = System.currentTimeMillis()): TaskItemModel {
        val taskTimeStatus = getTaskTimeStatus(
            lastExecutedMillis = this.lastExecutedMillis,
            maxIntervalMillis = this.maxIntervalMillis,
            currentTimeMillis = currentTime
        )
        val taskStatusColor = calculateTaskStatusColor(
            currentTime,
            this.lastExecutedMillis,
            this.minIntervalMillis,
            this.maxIntervalMillis
        )

        return TaskItemModel(
            id = this.id,
            name = this.name,
            status = taskStatusColor,
            timeStatus = taskTimeStatus
        )
    }

    /**
     * Maps a TaskEntity to the TaskDetail domain model
     */
    fun TaskEntity.toTaskDetailModel(): TaskDetailModel {
        val currentTime = System.currentTimeMillis()
        val actualLastExecuted = this.lastExecutedMillis
        val numberOfExecutions = this.executionTimestampsMillis.size
        val canExecute = currentTime >= (actualLastExecuted + this.minIntervalMillis)

        return TaskDetailModel(
            id = this.id,
            name = this.name,
            description = this.description,
            numberOfExecutions = numberOfExecutions,
            canExecute = canExecute
        )
    }
}