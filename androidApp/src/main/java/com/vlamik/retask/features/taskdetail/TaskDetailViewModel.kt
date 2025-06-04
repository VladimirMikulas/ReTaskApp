package com.vlamik.retask.features.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vlamik.core.commons.AppText
import com.vlamik.core.commons.PublishFlow
import com.vlamik.core.commons.onFailureIgnoreCancellation
import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.usecase.ExecuteTaskUseCase
import com.vlamik.core.domain.usecase.GetTaskDetailUseCase
import com.vlamik.retask.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel for the Task Detail screen, responsible for fetching and managing task details,
 * and handling task execution.
 * Uses Hilt's AssistedInject for dependency injection with a runtime parameter (taskId).
 */
class TaskDetailViewModel @AssistedInject constructor(
    @Assisted private val taskId: Long,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val executeTaskUseCase: ExecuteTaskUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.LoadingData)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _executingTaskState: MutableStateFlow<ExecutingTaskState> =
        MutableStateFlow(ExecutingTaskState.Idle)
    val executingTaskState: StateFlow<ExecutingTaskState> = _executingTaskState.asStateFlow()

    private val _events = PublishFlow<TaskDetailEvent>()
    val events: Flow<TaskDetailEvent> = _events

    init {
        loadTaskDetail()
    }

    /**
     * Initiates the loading of task details and updates the uiState.
     */
    private fun loadTaskDetail() {
        viewModelScope.launch {
            getTaskDetailUseCase(taskId)
                .map { result ->
                    result.fold(
                        onSuccess = { task ->
                            task?.let {
                                UiState.Success(it)
                            } ?: UiState.DataError(AppText.from(R.string.data_error))
                        },
                        onFailure = { throwable ->
                            UiState.DataError(
                                throwable.message?.let(AppText::dynamic)
                                    ?: AppText.from(R.string.data_error)
                            )
                        }
                    )
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    /**
     * Handles the action of executing a task.
     * Launched in viewModelScope to perform suspend operations.
     */
    fun onExecuteTask() {
        viewModelScope.launch {
            if (_executingTaskState.value == ExecutingTaskState.Executing) {
                return@launch
            }

            val currentTask = (_uiState.value as? UiState.Success)?.task
            currentTask?.let {
                _executingTaskState.value = ExecutingTaskState.Executing

                runCatching {
                    executeTaskUseCase(taskId)
                }.onSuccess {
                    _executingTaskState.value = ExecutingTaskState.Idle
                    _events.emit(TaskDetailEvent.ShowSnackbar(AppText.from(R.string.task_executed_success)))
                }.onFailureIgnoreCancellation {
                    _executingTaskState.value = ExecutingTaskState.Idle
                    _events.emit(TaskDetailEvent.ShowSnackbar(AppText.from(R.string.task_executed_failure)))
                }
            }
        }
    }

    /**
     * Sealed interface representing the various UI states of the Task Detail screen.
     */
    sealed interface UiState {
        data object LoadingData : UiState
        data class Success(val task: TaskDetailModel) : UiState
        data class DataError(val message: AppText) : UiState
    }

    /**
     * Sealed interface representing the state of a task execution operation.
     * Used to manage the UI state of the "Execute Task" button and related indicators.
     * Defined within the ViewModel as it's primarily used in this context.
     */
    sealed interface ExecutingTaskState {
        data object Idle : ExecutingTaskState
        data object Executing : ExecutingTaskState
    }

    /**
     * Sealed interface representing one-time events that the UI should react to.
     */
    sealed interface TaskDetailEvent {
        data class ShowSnackbar(val message: AppText) : TaskDetailEvent
    }

    /**
     * AssistedFactory for creating TaskDetailViewModel instances with a runtime taskId.
     * Hilt uses this to generate the factory implementation.
     */
    @AssistedFactory
    interface Factory {
        fun create(taskId: Long): TaskDetailViewModel
    }

    /**
     * Companion object providing a ViewModelProvider.Factory.
     * This factory allows creating ViewModel instances with assisted injection parameters.
     */
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            factory: Factory,
            taskId: Long,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Creates an instance of TaskDetailViewModel using the AssistedFactory.
                return factory.create(taskId) as T
            }
        }
    }
}