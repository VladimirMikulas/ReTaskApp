package com.vlamik.core.data.datasource.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vlamik.core.data.datasource.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for TaskEntity.
 * Defines methods for interacting with the 'tasks' table in the Room database.
 */
@Dao
interface TaskDao {

    /**
     * Inserts a new task into the database. If a task with the same ID already exists, it will be replaced.
     * @param task The task entity to insert.
     * @return The ID of the newly inserted task (typically for suspend functions returning Long).
     * If the method has no return value (Unit), the ID is not directly obtained.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    /**
     * Updates an existing task in the database.
     * @param task The task entity with updated data.
     */
    @Update
    suspend fun updateTask(task: TaskEntity)

    /**
     * Deletes a task from the database.
     * @param task The task entity to delete.
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    /**
     * Retrieves a specific task by its ID.
     * Returns a Flow, so the UI automatically updates when data changes.
     * @param taskId The ID of the task.
     * @return A Flow emitting the TaskEntity or null if the task does not exist.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskEntity?>

    /**
     * Retrieves all tasks from the database.
     * Tasks are sorted by their due date,
     * which is calculated as (last executed time + maximum interval).
     * Tasks that have never been executed (last_executed_ms IS NULL)
     * are considered executed at time 0 (epoch) for sorting purposes.
     * Returns a Flow, so the UI automatically updates when data changes.
     *
     * NOTE: This query assumes TaskEntity has a 'last_executed_ms' column.
     */
    @Query("SELECT * FROM tasks ORDER BY (IFNULL(last_executed_ms, 0) + max_interval_ms) ASC")
    fun getAllTasksSortedByDueDate(): Flow<List<TaskEntity>>

    /**
     * Deletes all tasks from the database.
     * Use with caution!
     */
    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

}