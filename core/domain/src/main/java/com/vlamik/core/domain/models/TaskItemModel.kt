package com.vlamik.core.domain.models

enum class TaskStatus {
    DONE,    // Task does not need to be performed
    OPTIONAL,   // Task can be performed, but it's not mandatory
    REQUIRED       // Task must be performed
}

/**
 * Domain model representing a task item for list (TaskList).
 * Contains only attributes relevant for the list, minimizing dependence on details.
 */

data class TaskItemModel(
    val id: Long,
    val name: String,
    val status: TaskStatus,
    val timeStatus: TaskTimeStatus
)