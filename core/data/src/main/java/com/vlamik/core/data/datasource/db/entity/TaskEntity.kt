package com.vlamik.core.data.datasource.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Entity representing a task in the Room database.
 */
@Entity(tableName = "tasks")
@TypeConverters(ExecutionTimestampsConverter::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "min_interval_ms")
    val minIntervalMillis: Long,

    @ColumnInfo(name = "max_interval_ms")
    val maxIntervalMillis: Long,

    @ColumnInfo(name = "points")
    val points: Int,

    /**
     * List of timestamps (in milliseconds from epoch) when the task was executed.
     * Stored as a String using a TypeConverter (ExecutionTimestampsConverter).
     * The most recent execution record (lastExecutedMillis) can be obtained as the maximum from this list.
     */
    @ColumnInfo(name = "execution_timestamps_ms")
    val executionTimestampsMillis: List<Long> = emptyList(),

    @ColumnInfo(name = "last_executed_ms")
    val lastExecutedMillis: Long? = null,
)

/**
 * Class for converting List<Long> to String and back, so it can be stored in a Room database.
 */
class ExecutionTimestampsConverter {
    @TypeConverter
    fun fromTimestampList(timestamps: List<Long>?): String? {
        // If the list is null or empty, return null or an empty string as needed.
        // Here, we return an empty string for an empty list, null for a null list.
        return timestamps?.joinToString(",")
    }

    @TypeConverter
    fun toTimestampList(data: String?): List<Long>? {
        // If the data is null or empty, return null or an empty list.
        // Here, we return an empty list for an empty string, null for null data.
        return data?.split(',')?.mapNotNull {
            // Try to convert each element to Long, filter out invalid values.
            // Empty strings after splitting (e.g., if the original string was empty) will be filtered out.
            if (it.isNotBlank()) it.toLongOrNull() else null
        }
    }
}