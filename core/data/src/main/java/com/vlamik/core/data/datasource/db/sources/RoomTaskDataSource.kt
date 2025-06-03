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
import javax.inject.Inject

/**
 * Implementation of [TaskDataSource] using Room Persistence Library.
 * Handles database operations for tasks.
 */
class RoomTaskDataSource @Inject constructor(private val taskDao: TaskDao) : TaskDataSource {

    /**
     * Initializes the database with default tasks if it's empty.
     * This ensures the app has some initial data on first launch.
     */
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

    /**
     * Retrieves all tasks from the database as a [Flow] of [Result] containing a list of [TaskEntity].
     * The tasks are sorted by due date.
     */
    override fun getAllTasks(): Flow<Result<List<TaskEntity>>> {
        return taskDao.getAllTasksSortedByDueDate()
            .map { tasks ->
                Result.success(tasks)
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    /**
     * Retrieves a single task by its ID from the database as a [Flow] of [Result] containing a [TaskEntity].
     */
    override fun getTaskById(taskId: Long): Flow<Result<TaskEntity?>> {
        return taskDao.getTaskById(taskId)
            .map { task ->
                Result.success(task)
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    /**
     * Marks a specific task as "executed" by updating its execution timestamps.
     * Throws [NoSuchElementException] if the task is not found.
     */
    override suspend fun executeTask(taskId: Long) {
        runCatching {
            val currentTaskEntity =
                taskDao.getTaskById(taskId).firstOrNull() // Get current task state

            currentTaskEntity?.let {
                val currentTime = System.currentTimeMillis()
                // Add the current execution time to the list of timestamps
                val updatedTimestamps = it.executionTimestampsMillis + currentTime
                // Find the latest execution time from the updated list
                val newLastExecuted = updatedTimestamps.max()

                // Create a new TaskEntity with updated timestamps and last executed time
                val updatedTaskEntity = it.copy(
                    executionTimestampsMillis = updatedTimestamps,
                    lastExecutedMillis = newLastExecuted
                )
                taskDao.updateTask(updatedTaskEntity) // Update the task in the database
            } ?: throw NoSuchElementException("Task with ID $taskId not found for execution.")
        }.onFailureIgnoreCancellation { e ->
            loge("Failed to execute task ID $taskId: ${e.message}", e)
        }
    }


    companion object {
        private const val SECOND_IN_MILLIS = 1000L
        private const val MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS
        private const val HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS
        private const val DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS
        private const val MONTH_IN_MILLIS = 30 * DAY_IN_MILLIS
        private const val YEAR_IN_MILLIS = 365 * DAY_IN_MILLIS

        private fun now(): Long = System.currentTimeMillis()

        // --- 1. Task: GREEN
        private val taskBackupFiles = TaskEntity(
            id = 1L,
            name = "Týždenná záloha dát",
            description = "Pripravte externý disk a zálohujte dôležité pracovné a osobné súbory. Skontrolujte integritu zálohy.",
            minIntervalMillis = 5 * DAY_IN_MILLIS,
            maxIntervalMillis = 7 * DAY_IN_MILLIS,
            points = 25,
            executionTimestampsMillis = listOf(now() - 2 * DAY_IN_MILLIS - 8 * HOUR_IN_MILLIS),
            lastExecutedMillis = now() - 2 * DAY_IN_MILLIS - 8 * HOUR_IN_MILLIS
        )

        // --- 2. Task: ORANGE
        private val taskCleanBathroom = TaskEntity(
            id = 2L,
            name = "Upratať kúpeľňu",
            description = "Vyčistite umývadlo, sprchu/vaňu, toaletu a podlahu. Doplňte mydlo a toaletný papier.",
            minIntervalMillis = 3 * DAY_IN_MILLIS,
            maxIntervalMillis = 5 * DAY_IN_MILLIS,
            points = 15,
            executionTimestampsMillis = listOf(now() - 4 * DAY_IN_MILLIS - 2 * HOUR_IN_MILLIS),
            lastExecutedMillis = now() - 4 * DAY_IN_MILLIS - 2 * HOUR_IN_MILLIS
        )

        // --- 3. Task: ORANGE
        private val taskBoilerMaintenance = TaskEntity(
            id = 3L,
            name = "Ročná údržba kotla",
            description = "Kontaktujte autorizovaného technika pre ročnú kontrolu a údržbu vykurovacieho kotla. Skontrolujte dátum poslednej služby.",
            minIntervalMillis = 11 * MONTH_IN_MILLIS,
            maxIntervalMillis = 14 * MONTH_IN_MILLIS,
            points = 50,
            executionTimestampsMillis = listOf(now() - 13 * MONTH_IN_MILLIS - 7 * DAY_IN_MILLIS),
            lastExecutedMillis = now() - 13 * MONTH_IN_MILLIS - 7 * DAY_IN_MILLIS
        )

        // --- 4. Task: RED
        private val taskCheckFireExtinguisher = TaskEntity(
            id = 4L,
            name = "Kontrola hasiaceho prístroja",
            description = "Skontrolujte tlak a platnosť hasiacich prístrojov v domácnosti a garáži. Zaznamenajte dátumy expirácie.",
            minIntervalMillis = 6 * MONTH_IN_MILLIS,
            maxIntervalMillis = 1 * YEAR_IN_MILLIS,
            points = 30,
            executionTimestampsMillis = emptyList(),
            lastExecutedMillis = 0L
        )

        // --- 5. Task: GREEN
        private val taskCheckCoffeeMachine = TaskEntity(
            id = 5L,
            name = "Vyčistiť kávovar",
            description = "Vyprázdnite a vyčistite nádržku na vodu a odpadovú nádobu kávovaru. Prepláchnite systém.",
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
            name = "Štvrťročná finančná kontrola",
            description = "Prezrite finančné výkazy Q2 a pripravte sa na stretnutie so zúčastnenými stranami.",
            minIntervalMillis = 2 * MONTH_IN_MILLIS,
            maxIntervalMillis = 3 * MONTH_IN_MILLIS,
            points = 70,
            executionTimestampsMillis = listOf(now() - 2 * MONTH_IN_MILLIS - 20 * DAY_IN_MILLIS),
            lastExecutedMillis = now() - 2 * MONTH_IN_MILLIS - 20 * DAY_IN_MILLIS
        )

        // --- 7. Task: GREEN
        private val taskDueInSeconds = TaskEntity(
            id = 7L,
            name = "Odpovedať na dôležitý email",
            description = "Posledná urgentná pripomienka pred schôdzkou.",
            minIntervalMillis = 15 * SECOND_IN_MILLIS,
            maxIntervalMillis = 30 * SECOND_IN_MILLIS,
            points = 10,
            executionTimestampsMillis = listOf(now() - 10 * SECOND_IN_MILLIS),
            lastExecutedMillis = now() - 10 * SECOND_IN_MILLIS
        )

        // --- 8. Task: GREEN
        private val taskDueInMinutes = TaskEntity(
            id = 8L,
            name = "Pripraviť podklady pre prezentáciu",
            description = "Doplniť grafy a dáta do Powerpointu.",
            minIntervalMillis = 20 * MINUTE_IN_MILLIS,
            maxIntervalMillis = 30 * MINUTE_IN_MILLIS,
            points = 30,
            executionTimestampsMillis = listOf(now() - 15 * MINUTE_IN_MILLIS),
            lastExecutedMillis = now() - 15 * MINUTE_IN_MILLIS
        )

        val allTasks = listOf(
            taskBackupFiles,
            taskCleanBathroom,
            taskBoilerMaintenance,
            taskCheckFireExtinguisher,
            taskCheckCoffeeMachine,
            taskQuarterlyReview,
            taskDueInSeconds,
            taskDueInMinutes
        )
    }
}