package com.vlamik.core.domain.models


/**
 * Sealed class representing the time status of a task relative to its due date.
 * Contains specific time units for easier display formatting.
 */
sealed class TaskTimeStatus {

    /**
     * The task has not been executed yet.
     * This indicates a new task that is yet to start its regular cycle.
     */
    data object NeverExecuted : TaskTimeStatus()

    /**
     * The task is overdue and needs to be executed immediately.
     * Contains the exact duration by which it's overdue.
     */
    data class Overdue(val components: TimeComponents) : TaskTimeStatus()

    /**
     * The task is due within a minute.
     * Contains the remaining seconds.
     */
    data class DueInLessThanMinute(val components: TimeComponents) : TaskTimeStatus()

    /**
     * The task is due within an hour.
     * Contains the remaining minutes and seconds.
     */
    data class DueInLessThanHour(val components: TimeComponents) : TaskTimeStatus()

    /**
     * The task is due within a day.
     * Contains the remaining hours, minutes, and seconds.
     */
    data class DueInLessThanDay(val components: TimeComponents) : TaskTimeStatus()

    /**
     * The task is due within a month (approx. 30 days).
     * Contains the remaining days, hours, minutes.
     */
    data class DueInLessThanMonth(val components: TimeComponents) : TaskTimeStatus()

    /**
     * The task is due within a year (approx. 365 days).
     * Contains the remaining months, days, hours.
     */
    data class DueInLessThanYear(val components: TimeComponents) : TaskTimeStatus()

    /**
     * The task is due in more than a year.
     * Contains the remaining years, months, and days.
     */
    data class DueInMoreThanYear(val components: TimeComponents) : TaskTimeStatus()
}
