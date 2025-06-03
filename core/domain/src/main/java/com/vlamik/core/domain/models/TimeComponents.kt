package com.vlamik.core.domain.models

/**
 * Represents time units, ordered from largest (YEARS) to smallest (SECONDS).
 * Ordinal values reflect this order.
 */
enum class TimeUnitGranularity {
    YEARS, MONTHS, DAYS, HOURS, MINUTES, SECONDS
}

/**
 * A data class to hold decomposed time components.
 * All values are Long to prevent overflow for large durations.
 */
data class TimeComponents(
    val years: Long = 0,
    val months: Long = 0,
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0
) {
    val isDueNow: Boolean
        get() = years == 0L && months == 0L && days == 0L && hours == 0L && minutes == 0L && seconds == 0L
}
