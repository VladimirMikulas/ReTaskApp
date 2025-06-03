package com.vlamik.retask.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.vlamik.core.commons.AppText
import com.vlamik.core.domain.models.TaskTimeStatus
import com.vlamik.core.domain.models.TimeComponents
import com.vlamik.core.domain.models.TimeUnitGranularity
import com.vlamik.retask.R
import com.vlamik.retask.common.utils.asString

/**
 * Compose extension property to resolve AppText to a String within a Composable function.
 * @return The resolved String.
 */
@Composable
fun AppText.asString(): String {
    val context = LocalContext.current
    return this.asString(context)
}

/**
 * Helper function to get a plural string for a given value and resource ID.
 */
@Composable
fun getPluralString(value: Long, pluralResId: Int): String {
    return pluralStringResource(pluralResId, value.toInt(), value)
}

/**
 * A general helper function (extension on TimeComponents) for formatting time components.
 * It considers only units within the range from `maxRelevantUnit` to `minRelevantUnit`
 * and displays a maximum of `maxParts` non-zero units.
 *
 * @param maxRelevantUnit The largest unit to consider (e.g., for "days and hours", this is DAYS).
 * @param minRelevantUnit The smallest unit to consider (e.g., for "days and hours", this is HOURS).
 * @param maxParts The maximum number of non-zero units to display (defaults to 2).
 */
@Composable
fun TimeComponents.toFormattedString(
    maxRelevantUnit: TimeUnitGranularity,
    minRelevantUnit: TimeUnitGranularity,
    maxParts: Int = 2
): String {
    val strAndSeparator = stringResource(R.string.and_separator)

    val formattedParts = mutableListOf<String>()

    // Define the order of time units and their corresponding plural resource IDs.
    // This list determines the precedence for formatting (e.g., years before months).
    val unitOrder = listOf(
        Pair(this.years, TimeUnitGranularity.YEARS),
        Pair(this.months, TimeUnitGranularity.MONTHS),
        Pair(this.days, TimeUnitGranularity.DAYS),
        Pair(this.hours, TimeUnitGranularity.HOURS),
        Pair(this.minutes, TimeUnitGranularity.MINUTES),
        Pair(this.seconds, TimeUnitGranularity.SECONDS)
    )

    // Map TimeUnitGranularity to its plural string resource ID for easy lookup.
    val pluralResourceMap = mapOf(
        TimeUnitGranularity.YEARS to R.plurals.years_plural,
        TimeUnitGranularity.MONTHS to R.plurals.months_plural,
        TimeUnitGranularity.DAYS to R.plurals.days_plural,
        TimeUnitGranularity.HOURS to R.plurals.hours_plural,
        TimeUnitGranularity.MINUTES to R.plurals.minutes_plural,
        TimeUnitGranularity.SECONDS to R.plurals.seconds_plural
    )

    // Iterate through units and conditionally add them to the list based on relevance and value.
    for ((value, unitGranularity) in unitOrder) {
        // Ensure the value is positive and the unit falls within the specified granularity range.
        if (value > 0 &&
            unitGranularity.ordinal >= maxRelevantUnit.ordinal &&
            unitGranularity.ordinal <= minRelevantUnit.ordinal
        ) {
            // Retrieve the plural resource ID and format the string.
            pluralResourceMap[unitGranularity]?.let { resId ->
                formattedParts.add(getPluralString(value, resId))
            }
        }
    }

    // Take only the required number of non-zero parts to display.
    val partsToDisplay = formattedParts.take(maxParts)

    return when (partsToDisplay.size) {
        0 -> "" // If no relevant non-zero parts were found
        1 -> partsToDisplay[0]
        else -> "${partsToDisplay[0]} $strAndSeparator ${partsToDisplay[1]}" // Join two parts with "and"
    }
}

/**
 * Extension function for TaskTimeStatus to convert it into a formatted display string.
 * This function handles different task statuses and delegates time component formatting.
 */
@Composable
fun TaskTimeStatus.toFormattedString(): String {
    // String resources for common phrases, retrieved from Android resources.
    val strDueIn = stringResource(R.string.due_in_placeholder)
    val strOverdue = stringResource(R.string.overdue_placeholder)
    val strDueNow = stringResource(R.string.due_now_placeholder)
    val strNeverExecuted = stringResource(R.string.never_executed_placeholder)

    // Main logic for determining the formatted string based on the task's time status.
    return when (this) {
        is TaskTimeStatus.NeverExecuted -> strNeverExecuted
        is TaskTimeStatus.Overdue -> {
            if (components.isDueNow) return strDueNow
            strOverdue
        }

        is TaskTimeStatus.DueInLessThanMinute -> {
            // Format time components focusing on seconds, displaying only one part.
            val formatted = components.toFormattedString(
                TimeUnitGranularity.SECONDS,
                TimeUnitGranularity.SECONDS,
                maxParts = 1
            )
            "$strDueIn $formatted"
        }

        is TaskTimeStatus.DueInLessThanHour -> {
            // Format time components focusing on minutes and seconds, displaying up to two parts.
            val formatted = components.toFormattedString(
                TimeUnitGranularity.MINUTES,
                TimeUnitGranularity.SECONDS,
                maxParts = 2
            )
            "$strDueIn $formatted"
        }

        is TaskTimeStatus.DueInLessThanDay -> {
            // Format time components focusing on hours and minutes, displaying up to two parts.
            val formatted = components.toFormattedString(
                TimeUnitGranularity.HOURS,
                TimeUnitGranularity.MINUTES,
                maxParts = 2
            )
            "$strDueIn $formatted"
        }

        is TaskTimeStatus.DueInLessThanMonth -> {
            // Format time components focusing on days and hours, displaying up to two parts.
            val formatted = components.toFormattedString(
                TimeUnitGranularity.DAYS,
                TimeUnitGranularity.HOURS,
                maxParts = 2
            )
            "$strDueIn $formatted"
        }

        is TaskTimeStatus.DueInLessThanYear -> {
            // Format time components focusing on months and days, displaying up to two parts.
            val formatted = components.toFormattedString(
                TimeUnitGranularity.MONTHS,
                TimeUnitGranularity.DAYS,
                maxParts = 2
            )
            "$strDueIn $formatted"
        }

        is TaskTimeStatus.DueInMoreThanYear -> {
            // Format time components focusing on years and months, displaying up to two parts.
            val formatted = components.toFormattedString(
                TimeUnitGranularity.YEARS,
                TimeUnitGranularity.MONTHS,
                maxParts = 2
            )
            "$strDueIn $formatted"
        }
    }
}