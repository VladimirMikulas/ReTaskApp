package com.vlamik.core.domain.models

enum class TaskStatus {
    GREEN,    // Task does not need to be performed
    ORANGE,   // Task can be performed, but it's not mandatory
    RED       // Task must be performed
}


/**
 * Domain model representing a task item for list (TaskList).
 * Contains only attributes relevant for the list, minimizing dependence on details.
 */

data class TaskItemModel(
    val id: Long,
    val name: String,
    val status: TaskStatus,
    val formattedRemainingTime: String
)