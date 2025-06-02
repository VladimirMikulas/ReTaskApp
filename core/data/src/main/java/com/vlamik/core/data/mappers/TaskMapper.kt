package com.vlamik.core.data.mappers

import com.vlamik.core.data.datasource.db.entity.TaskEntity
import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.models.TaskItemModel
import com.vlamik.core.domain.models.TaskStatus
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Object responsible for mapping between domain models and the TaskEntity database entity.
 */
object TaskMapper {

    /**
     * Maps a TaskEntity to a simplified domain model TaskItemModel
     */
    fun TaskEntity.toTaskItemModel(): TaskItemModel {
        val currentTime = System.currentTimeMillis()
        val actualLastExecuted = this.lastExecutedMillis ?: 0L
        val status = calculateTaskStatus(
            currentTime,
            actualLastExecuted,
            this.minIntervalMillis,
            this.maxIntervalMillis
        )
        val dueDate = actualLastExecuted + this.maxIntervalMillis
        val remainingTimeMillis = max(0, dueDate - currentTime)
        val formattedRemainingTime = formatRemainingTime(remainingTimeMillis)

        return TaskItemModel(
            id = this.id,
            name = this.name,
            status = status,
            formattedRemainingTime = formattedRemainingTime
        )
    }


    /**
     * Maps a TaskEntity to the TaskDetail domain model
     */
    fun TaskEntity.toTaskDetailModel(): TaskDetailModel {
        val currentTime = System.currentTimeMillis()
        val actualLastExecuted = this.lastExecutedMillis ?: 0L
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

    private fun formatRemainingTime(millis: Long): String {
        if (millis <= 0) return "Overdue!"

        val totalDays = TimeUnit.MILLISECONDS.toDays(millis)
        val totalHours = TimeUnit.MILLISECONDS.toHours(millis)

        val years = totalDays / 365L
        val remainingDaysAfterYears = totalDays % 365L

        val months = remainingDaysAfterYears / 30L
        val remainingDaysAfterMonths = remainingDaysAfterYears % 30L

        val days = remainingDaysAfterMonths
        val hours = totalHours % 24

        return when {
            years > 0 -> {
                val yearString = if (years == 1L) "year" else "years"
                if (months > 0) {
                    val monthString = if (months == 1L) "month" else "months"
                    "$years $yearString $months $monthString"
                } else if (days > 0) {
                    "$years $yearString ${days}d"
                } else {
                    "$years $yearString"
                }
            }

            months > 0 -> {
                val monthString = if (months == 1L) "month" else "months"
                if (days > 0) {
                    "$months $monthString ${days}d"
                } else {
                    "$months $monthString"
                }
            }

            days > 0 -> {
                "${days}d ${hours}h"
            }

            hours > 0 -> {
                "${hours}h"
            }

            else -> "Soon"
        }
    }

    private fun calculateTaskStatus(
        currentTime: Long,
        lastExecuted: Long,
        minInterval: Long,
        maxInterval: Long
    ): TaskStatus {
        val minExecuteTime = lastExecuted + minInterval
        val maxExecuteTime = lastExecuted + maxInterval

        return when {
            currentTime >= maxExecuteTime -> TaskStatus.RED
            currentTime >= minExecuteTime -> TaskStatus.ORANGE
            else -> TaskStatus.GREEN
        }
    }
}