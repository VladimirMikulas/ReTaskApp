package com.vlamik.core.domain.commons.utils

import com.vlamik.core.domain.models.TaskStatusColor
import com.vlamik.core.domain.models.TaskTimeStatus
import com.vlamik.core.domain.models.TimeComponents
import kotlin.math.abs

// --- Utility Constants (for internal calculations, in milliseconds) ---
private const val SECOND_IN_MILLIS = 1000L
private const val MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS
private const val HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS
private const val DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS
private const val MONTH_IN_MILLIS_APPROX = 30 * DAY_IN_MILLIS // Approximation for a month
private const val YEAR_IN_MILLIS_APPROX = 365 * DAY_IN_MILLIS // Approximation for a year

/**
 * Calculates time components (years, months, days, hours, minutes, seconds)
 * from a given duration in milliseconds.
 *
 * @param durationMillis The total duration in milliseconds. Must be non-negative.
 * @return A TimeComponents object with the decomposed time.
 * Note: Months and years are approximations if a fixed MILLIS value is used.
 */
fun calculateTimeComponents(durationMillis: Long): TimeComponents {
    require(durationMillis >= 0) { "Duration must be non-negative" }

    var remainingMillis = durationMillis

    val years = remainingMillis / YEAR_IN_MILLIS_APPROX
    remainingMillis %= YEAR_IN_MILLIS_APPROX

    val months = remainingMillis / MONTH_IN_MILLIS_APPROX
    remainingMillis %= MONTH_IN_MILLIS_APPROX

    val days = remainingMillis / DAY_IN_MILLIS
    remainingMillis %= DAY_IN_MILLIS

    val hours = remainingMillis / HOUR_IN_MILLIS
    remainingMillis %= HOUR_IN_MILLIS

    val minutes = remainingMillis / MINUTE_IN_MILLIS
    remainingMillis %= MINUTE_IN_MILLIS

    val seconds = remainingMillis / SECOND_IN_MILLIS

    return TimeComponents(years, months, days, hours, minutes, seconds)
}


// Assuming TaskItemModel (or the data source) has `lastExecutedMillis` and `maxIntervalMillis`
fun getTaskTimeStatus(
    lastExecutedMillis: Long,
    maxIntervalMillis: Long,
    currentTimeMillis: Long
): TaskTimeStatus {
    if (lastExecutedMillis == 0L) {
        return TaskTimeStatus.NeverExecuted
    }

    val nextExecutionDueTime = lastExecutedMillis + maxIntervalMillis
    val timeDifferenceMillis =
        nextExecutionDueTime - currentTimeMillis // Positive = remaining, Negative = overdue

    return when {
        // Overdue state
        timeDifferenceMillis <= 0 -> {
            val overdueMillis = abs(timeDifferenceMillis)
            TaskTimeStatus.Overdue(components = calculateTimeComponents(overdueMillis))
        }
        // Remaining time states (ordered from smallest positive interval to largest)
        timeDifferenceMillis <= MINUTE_IN_MILLIS -> TaskTimeStatus.DueInLessThanMinute(
            components = calculateTimeComponents(timeDifferenceMillis)
        )

        timeDifferenceMillis <= HOUR_IN_MILLIS -> TaskTimeStatus.DueInLessThanHour(
            components = calculateTimeComponents(timeDifferenceMillis)
        )

        timeDifferenceMillis <= DAY_IN_MILLIS -> TaskTimeStatus.DueInLessThanDay(
            components = calculateTimeComponents(timeDifferenceMillis)
        )

        timeDifferenceMillis <= MONTH_IN_MILLIS_APPROX -> TaskTimeStatus.DueInLessThanMonth(
            components = calculateTimeComponents(timeDifferenceMillis)
        )

        timeDifferenceMillis <= YEAR_IN_MILLIS_APPROX -> TaskTimeStatus.DueInLessThanYear(
            components = calculateTimeComponents(timeDifferenceMillis)
        )

        else -> TaskTimeStatus.DueInMoreThanYear(
            components = calculateTimeComponents(timeDifferenceMillis)
        )
    }

}

fun calculateTaskStatusColor(
    currentTime: Long,
    lastExecuted: Long,
    minInterval: Long,
    maxInterval: Long
): TaskStatusColor {
    val minExecuteTime = lastExecuted + minInterval
    val maxExecuteTime = lastExecuted + maxInterval

    return when {
        currentTime >= maxExecuteTime -> TaskStatusColor.RED
        currentTime >= minExecuteTime -> TaskStatusColor.ORANGE
        else -> TaskStatusColor.GREEN
    }
}