package com.vlamik.retask.mappers

import com.vlamik.core.domain.models.TaskItemModel
import com.vlamik.core.domain.models.TaskStatus
import com.vlamik.core.domain.models.TaskTimeStatus
import com.vlamik.core.domain.models.TimeComponents
import com.vlamik.core.domain.models.TimeUnitGranularity
import com.vlamik.retask.R
import com.vlamik.retask.commons.StringResourceProvider
import com.vlamik.retask.models.TaskItemUiModel
import com.vlamik.retask.models.TaskStatusColor

/**
 * Object responsible for mapping between domain models and the presentation models.
 */
object TaskUiMapper {
    /**
     * Maps a TaskItemModel to a presentation model TaskItemUiModel
     */
    fun TaskItemModel.toTaskItemUiModel(stringResourceProvider: StringResourceProvider): TaskItemUiModel {
        return TaskItemUiModel(
            id = this.id,
            name = this.name,
            statusColor = this.status.toTaskStatusColor(),
            timeStatusText = this.timeStatus.toFormattedString(stringResourceProvider)
        )
    }

    /**
     * Extension function for TaskTimeStatus to convert it into a formatted display string.
     * This function handles different task statuses and delegates time component formatting.
     *
     * @param stringResourceProvider The provider to access string resources.
     */
    fun TaskTimeStatus.toFormattedString(
        stringResourceProvider: StringResourceProvider
    ): String {
        val strDueIn = stringResourceProvider.getString(R.string.due_in_placeholder)
        val strOverdue = stringResourceProvider.getString(R.string.overdue_placeholder)
        val strDueNow = stringResourceProvider.getString(R.string.due_now_placeholder)
        val strNeverExecuted = stringResourceProvider.getString(R.string.never_executed_placeholder)

        return when (this) {
            is TaskTimeStatus.NeverExecuted -> strNeverExecuted
            is TaskTimeStatus.Overdue -> {
                if (components.isDueNow) return strDueNow
                strOverdue
            }

            is TaskTimeStatus.DueInLessThanMinute -> {
                val formatted = components.toFormattedString(
                    stringResourceProvider,
                    TimeUnitGranularity.SECONDS,
                    TimeUnitGranularity.SECONDS,
                    maxParts = 1
                )
                "$strDueIn $formatted"
            }

            is TaskTimeStatus.DueInLessThanHour -> {
                val formatted = components.toFormattedString(
                    stringResourceProvider,
                    TimeUnitGranularity.MINUTES,
                    TimeUnitGranularity.SECONDS,
                    maxParts = 2
                )
                "$strDueIn $formatted"
            }

            is TaskTimeStatus.DueInLessThanDay -> {
                val formatted = components.toFormattedString(
                    stringResourceProvider,
                    TimeUnitGranularity.HOURS,
                    TimeUnitGranularity.MINUTES,
                    maxParts = 2
                )
                "$strDueIn $formatted"
            }

            is TaskTimeStatus.DueInLessThanMonth -> {
                val formatted = components.toFormattedString(
                    stringResourceProvider,
                    TimeUnitGranularity.DAYS,
                    TimeUnitGranularity.HOURS,
                    maxParts = 2
                )
                "$strDueIn $formatted"
            }

            is TaskTimeStatus.DueInLessThanYear -> {
                val formatted = components.toFormattedString(
                    stringResourceProvider,
                    TimeUnitGranularity.MONTHS,
                    TimeUnitGranularity.DAYS,
                    maxParts = 2
                )
                "$strDueIn $formatted"
            }

            is TaskTimeStatus.DueInMoreThanYear -> {
                val formatted = components.toFormattedString(
                    stringResourceProvider,
                    TimeUnitGranularity.YEARS,
                    TimeUnitGranularity.MONTHS,
                    maxParts = 2
                )
                "$strDueIn $formatted"
            }
        }
    }
}

/**
 * Helper function to get a plural string for a given value and resource ID using a provider.
 */
private fun StringResourceProvider.getPluralStringHelper(value: Long, pluralResId: Int): String {
    return getPluralString(pluralResId, value.toInt(), value)
}

/**
 * A general helper function (extension on TimeComponents) for formatting time components.
 *
 * @param stringResourceProvider The provider to access string resources.
 * @param maxRelevantUnit The largest unit to consider (e.g., for "days and hours", this is DAYS).
 * @param minRelevantUnit The smallest unit to consider (e.g., for "days and hours", this is HOURS).
 * @param maxParts The maximum number of non-zero units to display (defaults to 2).
 */
private fun TimeComponents.toFormattedString(
    stringResourceProvider: StringResourceProvider,
    maxRelevantUnit: TimeUnitGranularity,
    minRelevantUnit: TimeUnitGranularity,
    maxParts: Int = 2
): String {
    val strAndSeparator = stringResourceProvider.getString(R.string.and_separator)

    val formattedParts = mutableListOf<String>()

    val unitOrder = listOf(
        Pair(this.years, TimeUnitGranularity.YEARS),
        Pair(this.months, TimeUnitGranularity.MONTHS),
        Pair(this.days, TimeUnitGranularity.DAYS),
        Pair(this.hours, TimeUnitGranularity.HOURS),
        Pair(this.minutes, TimeUnitGranularity.MINUTES),
        Pair(this.seconds, TimeUnitGranularity.SECONDS)
    )

    val pluralResourceMap = mapOf(
        TimeUnitGranularity.YEARS to R.plurals.years_plural,
        TimeUnitGranularity.MONTHS to R.plurals.months_plural,
        TimeUnitGranularity.DAYS to R.plurals.days_plural,
        TimeUnitGranularity.HOURS to R.plurals.hours_plural,
        TimeUnitGranularity.MINUTES to R.plurals.minutes_plural,
        TimeUnitGranularity.SECONDS to R.plurals.seconds_plural
    )

    for ((value, unitGranularity) in unitOrder) {
        if (value > 0 &&
            unitGranularity.ordinal >= maxRelevantUnit.ordinal &&
            unitGranularity.ordinal <= minRelevantUnit.ordinal
        ) {
            pluralResourceMap[unitGranularity]?.let { resId ->
                formattedParts.add(stringResourceProvider.getPluralStringHelper(value, resId))
            }
        }
    }

    val partsToDisplay = formattedParts.take(maxParts)

    return when (partsToDisplay.size) {
        0 -> ""
        1 -> partsToDisplay[0]
        else -> "${partsToDisplay[0]} $strAndSeparator ${partsToDisplay[1]}"
    }
}

private fun TaskStatus.toTaskStatusColor(): TaskStatusColor {
    return when (this) {
        TaskStatus.DONE -> TaskStatusColor.GREEN
        TaskStatus.OPTIONAL -> TaskStatusColor.ORANGE
        TaskStatus.REQUIRED -> TaskStatusColor.RED
    }
}