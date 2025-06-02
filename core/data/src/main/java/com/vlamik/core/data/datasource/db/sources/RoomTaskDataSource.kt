package com.vlamik.core.data.datasource.db.sources

import com.vlamik.core.commons.logd
import com.vlamik.core.commons.loge
import com.vlamik.core.commons.onFailureIgnoreCancellation
import com.vlamik.core.data.datasource.TaskDataSource
import com.vlamik.core.data.datasource.db.dao.TaskDao
import com.vlamik.core.data.datasource.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RoomTaskDataSource @Inject constructor(private val taskDao: TaskDao) : TaskDataSource {
    override suspend fun initTasksData() {
        runCatching {
            val existingTasks = taskDao.getAllTasksSortedByDueDate().firstOrNull()
            if (existingTasks.isNullOrEmpty()) {
                logd("Database is empty, inserting default tasks.")
                allTasks.forEach { task ->
                    taskDao.insertTask(task)
                }
            } else {
                logd("Database already contains tasks, skipping default insertion.")
            }
        }.onFailureIgnoreCancellation { e ->
            loge("Failed to insert tasks: ${e.message}", e)
        }
    }

    override fun getAllTasks(): Flow<Result<List<TaskEntity>>> {
        return taskDao.getAllTasksSortedByDueDate()
            .map { tasks ->
                Result.success(tasks)
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getTaskById(taskId: Long): Flow<Result<TaskEntity?>> {
        return taskDao.getTaskById(taskId)
            .map { task ->
                Result.success(task)
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override suspend fun executeTask(taskId: Long) {
        runCatching {
            val currentTaskEntity = taskDao.getTaskById(taskId).firstOrNull()

            currentTaskEntity?.let {
                val currentTime = System.currentTimeMillis()
                val updatedTimestamps = it.executionTimestampsMillis + currentTime
                val newLastExecuted = updatedTimestamps.maxOrNull()

                val updatedTaskEntity = it.copy(
                    executionTimestampsMillis = updatedTimestamps,
                    lastExecutedMillis = newLastExecuted
                )
                taskDao.updateTask(updatedTaskEntity)
            } ?: throw NoSuchElementException("Task with ID $taskId not found for execution.")
        }.onFailureIgnoreCancellation { e ->
            loge("Failed to execute task ID $taskId: ${e.message}", e)
        }
    }

    companion object {
        private val MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1)
        private val HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1)
        private val DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1)
        private val MONTH_IN_MILLIS = 30L * DAY_IN_MILLIS
        private val YEAR_IN_MILLIS = 365L * DAY_IN_MILLIS

        private fun now(): Long = System.currentTimeMillis()

        // --- 1. Task: GREEN
        private val taskBackupFiles = TaskEntity(
            id = 1L,
            name = "Weekly Data Backup",
            description = "Prepare an external drive and back up important work and personal files. Check backup integrity.",
            minIntervalMillis = 5 * DAY_IN_MILLIS,
            maxIntervalMillis = 7 * DAY_IN_MILLIS,
            points = 25,
            executionTimestampsMillis = listOf(now() - 2 * DAY_IN_MILLIS - 8 * HOUR_IN_MILLIS),
            lastExecutedMillis = now() - 2 * DAY_IN_MILLIS - 8 * HOUR_IN_MILLIS
        )

        // --- 2. Task: ORANGE
        private val taskCleanBathroom = TaskEntity(
            id = 2L,
            name = "Clean Bathroom",
            description = "Clean the sink, shower/bathtub, toilet, and floor. Refill soap and toilet paper.",
            minIntervalMillis = 3 * DAY_IN_MILLIS,
            maxIntervalMillis = 5 * DAY_IN_MILLIS,
            points = 15,
            executionTimestampsMillis = listOf(now() - 4 * DAY_IN_MILLIS - 2 * HOUR_IN_MILLIS),
            lastExecutedMillis = now() - 4 * DAY_IN_MILLIS - 2 * HOUR_IN_MILLIS
        )

        // --- 3. Task: RED (Long overdue)
        private val taskBoilerMaintenance = TaskEntity(
            id = 3L,
            name = "Annual Boiler Service",
            description = "Contact an authorized technician for annual inspection and maintenance of the heating boiler. Check last service date.",
            minIntervalMillis = 11 * MONTH_IN_MILLIS,
            maxIntervalMillis = 12 * MONTH_IN_MILLIS,
            points = 50,
            executionTimestampsMillis = listOf(now() - 13 * MONTH_IN_MILLIS - 7 * DAY_IN_MILLIS),
            lastExecutedMillis = now() - 13 * MONTH_IN_MILLIS - 7 * DAY_IN_MILLIS
        )

        // --- 4. Task: RED
        private val taskCheckFireExtinguisher = TaskEntity(
            id = 4L,
            name = "Fire Extinguisher Check",
            description = "Check pressure and validity of fire extinguishers in the home and garage. Record expiration dates.",
            minIntervalMillis = 6 * MONTH_IN_MILLIS,
            maxIntervalMillis = 1 * YEAR_IN_MILLIS,
            points = 30,
            executionTimestampsMillis = emptyList(),
            lastExecutedMillis = null
        )

        // --- 5. Task: GREEN
        private val taskCheckCoffeeMachine = TaskEntity(
            id = 5L,
            name = "Clean Coffee Machine",
            description = "Empty and clean the water reservoir and waste bin of the coffee machine. Flush the system.",
            minIntervalMillis = 6 * HOUR_IN_MILLIS,
            maxIntervalMillis = 12 * HOUR_IN_MILLIS,
            points = 5,
            executionTimestampsMillis = listOf(
                now() - 1 * DAY_IN_MILLIS,
                now() - 18 * HOUR_IN_MILLIS,
                now() - 3 * HOUR_IN_MILLIS - 20 * MINUTE_IN_MILLIS
            ),
            lastExecutedMillis = now() - 3 * HOUR_IN_MILLIS - 20 * MINUTE_IN_MILLIS
        )

        // --- 6. Task: ORANGE
        private val taskQuarterlyReview = TaskEntity(
            id = 6L,
            name = "Quarterly Financial Review",
            description = "Review Q2 financial statements and prepare for stakeholder meeting.",
            minIntervalMillis = 2 * MONTH_IN_MILLIS,
            maxIntervalMillis = 3 * MONTH_IN_MILLIS,
            points = 70,
            executionTimestampsMillis = listOf(now() - 2 * MONTH_IN_MILLIS - 20 * DAY_IN_MILLIS),
            lastExecutedMillis = now() - 2 * MONTH_IN_MILLIS - 20 * DAY_IN_MILLIS
        )

        val allTasks = listOf(
            taskBackupFiles,
            taskCleanBathroom,
            taskBoilerMaintenance,
            taskCheckFireExtinguisher,
            taskCheckCoffeeMachine,
            taskQuarterlyReview
        )
    }
}