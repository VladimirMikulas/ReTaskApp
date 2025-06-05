package com.vlamik.retask.models


import androidx.compose.ui.graphics.Color
import com.vlamik.retask.theme.Green
import com.vlamik.retask.theme.Orange
import com.vlamik.retask.theme.Red

enum class TaskStatusColor {
    GREEN, ORANGE, RED;

    fun toComposeColor(): Color {
        return when (this) {
            GREEN -> Green
            ORANGE -> Orange
            RED -> Red
        }
    }
}

data class TaskItemUiModel(
    val id: Long,
    val name: String,
    val statusColor: TaskStatusColor,
    val timeStatusText: String
)

