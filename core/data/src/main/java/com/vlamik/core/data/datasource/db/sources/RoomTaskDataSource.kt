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

    companion object DefaultTasks {
        private val MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1)
        private val HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1)
        private val DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1)
        private val MONTH_IN_MILLIS = 30 * DAY_IN_MILLIS // Approximate month
        private val YEAR_IN_MILLIS = 365 * DAY_IN_MILLIS // Approximate year

        // Reference time for simulating different states.
        // NOTE: REFERENCE_TIME is set at the time of code compilation/app launch,
        // so the 'due dates' are relative to this constant. For dynamic states relative to
        // the *current* real-time, you might need to adjust logic in your domain/UI layer
        // that calculates actual due dates based on System.currentTimeMillis() and lastExecutedMillis.
        private val REFERENCE_TIME = System.currentTimeMillis()

        // --- 1. Task: File Backup (State: GREEN - recently executed) ---
        private val taskBackupFiles = TaskEntity(
            id = 1L,
            name = "Týždenná záloha dát",
            description = "Pripravte externý disk a zálohujte dôležité pracovné a osobné súbory. Skontrolujte integritu zálohy.",
            minIntervalMillis = 5 * DAY_IN_MILLIS,
            maxIntervalMillis = 7 * DAY_IN_MILLIS,
            points = 25,
            executionTimestampsMillis = listOf(REFERENCE_TIME - 2 * DAY_IN_MILLIS - 8 * HOUR_IN_MILLIS),
            lastExecutedMillis = REFERENCE_TIME - 2 * DAY_IN_MILLIS - 8 * HOUR_IN_MILLIS
        )

        // --- 2. Task: Regular Housework (State: ORANGE - can be executed) ---
        private val taskCleanBathroom = TaskEntity(
            id = 2L,
            name = "Upratať kúpeľňu",
            description = "Vyčistiť umývadlo, sprchový kút/vaňu, záchod a podlahu. Doplňte mydlo a toaletný papier.",
            minIntervalMillis = 3 * DAY_IN_MILLIS,
            maxIntervalMillis = 5 * DAY_IN_MILLIS,
            points = 15,
            executionTimestampsMillis = listOf(REFERENCE_TIME - 4 * DAY_IN_MILLIS - 2 * HOUR_IN_MILLIS),
            lastExecutedMillis = REFERENCE_TIME - 4 * DAY_IN_MILLIS - 2 * HOUR_IN_MILLIS
        )

        // --- 3. Task: Service Activity (State: RED - overdue) ---
        private val taskBoilerMaintenance = TaskEntity(
            id = 3L,
            name = "Ročný servis kotla",
            description = "Kontaktovať autorizovaného technika pre ročnú revíziu a údržbu vykurovacieho kotla. Skontrolujte dátum posledného servisu.",
            minIntervalMillis = 11 * MONTH_IN_MILLIS,
            maxIntervalMillis = 12 * MONTH_IN_MILLIS,
            points = 50,
            executionTimestampsMillis = listOf(REFERENCE_TIME - 13 * MONTH_IN_MILLIS - 7 * DAY_IN_MILLIS),
            lastExecutedMillis = REFERENCE_TIME - 13 * MONTH_IN_MILLIS - 7 * DAY_IN_MILLIS
        )

        // --- 4. Task: Device Check (State: NEVER EXECUTED / PENDING) ---
        private val taskCheckFireExtinguisher = TaskEntity(
            id = 4L,
            name = "Kontrola hasiacich prístrojov",
            description = "Skontrolovať tlak a platnosť hasiacich prístrojov v domácnosti a garáži. Zaznamenať dátumy expirácie.",
            minIntervalMillis = 6 * MONTH_IN_MILLIS,
            maxIntervalMillis = 1 * YEAR_IN_MILLIS,
            points = 30,
            executionTimestampsMillis = emptyList(),
            lastExecutedMillis = null
        )

        // --- 5. Task: Frequent Maintenance (State: GREEN - recently executed, short interval) ---
        private val taskCheckCoffeeMachine = TaskEntity(
            id = 5L,
            name = "Vyčistiť kávovar",
            description = "Vyprázdniť a vyčistiť zásobník na vodu a odpadovú nádobku kávovaru. Prepláchnuť systém.",
            minIntervalMillis = 6 * HOUR_IN_MILLIS,
            maxIntervalMillis = 12 * HOUR_IN_MILLIS,
            points = 5,
            executionTimestampsMillis = listOf(
                REFERENCE_TIME - 1 * DAY_IN_MILLIS,
                REFERENCE_TIME - 18 * HOUR_IN_MILLIS,
                REFERENCE_TIME - 3 * HOUR_IN_MILLIS - 20 * MINUTE_IN_MILLIS
            ),
            lastExecutedMillis = REFERENCE_TIME - 3 * HOUR_IN_MILLIS - 20 * MINUTE_IN_MILLIS
        )

        val allTasks = listOf(
            taskBackupFiles,
            taskCleanBathroom,
            taskBoilerMaintenance,
            taskCheckFireExtinguisher,
            taskCheckCoffeeMachine
        )
    }
}