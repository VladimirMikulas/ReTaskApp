package com.vlamik.core.domain.models

/**
 * Domain model representing a detailed task
 */
data class TaskDetailModel(
    val id: Long,
    val name: String,
    val description: String,
    val numberOfExecutions: Int,
    val canExecute: Boolean
)