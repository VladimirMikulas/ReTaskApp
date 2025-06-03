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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * ViewModel for the Task Detail screen, responsible for fetching and managing task details,
 * and handling task execution.
 * Uses Hilt's AssistedInject for dependency injection with a runtime parameter (taskId).
 */
class TaskDetailViewModel @AssistedInject constructor(
    @Assisted private val taskId: Long,
    getTaskDetailUseCase: GetTaskDetailUseCase,
    private val executeTaskUseCase: ExecuteTaskUseCase
) : ViewModel() {

    /**
     * Represents the UI state of the Task Detail screen.
     * It's a StateFlow, meaning it holds and emits the current state,
     * allowing UI to react to changes.
     */
    val uiState: StateFlow<UiState> = getTaskDetailUseCase(taskId)
        .map { result -> // Map the Result from the use case to UiState
            result.fold(
                onSuccess = { task ->
                    // If successful, and task is not null, emit Success state.
                    // Otherwise, emit a DataError.
                    task?.let {
                        UiState.Success(it)
                    } ?: UiState.DataError(AppText.from(R.string.data_error))
                },
                onFailure = { throwable ->
                    // If fetching fails, emit a DataError with a dynamic or default message.
                    UiState.DataError(
                        throwable.message?.let(AppText::dynamic)
                            ?: AppText.from(R.string.data_error)
                    )
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.LoadingData
        )

    private val _events = PublishFlow<TaskDetailEvent>()
    val events: Flow<TaskDetailEvent> = _events

    /**
     * Handles the action of executing a task.
     * Launched in viewModelScope to perform suspend operations.
     */
    fun onExecuteTask() {
        viewModelScope.launch {
            val currentTask = (uiState.value as? UiState.Success)?.task
            currentTask?.let {
                _events.emit(TaskDetailEvent.ExecutingTaskStarted)
                runCatching {
                    executeTaskUseCase(taskId)
                }.onSuccess {
                    _events.emit(TaskDetailEvent.ExecutingTaskFinished)
                    _events.emit(TaskDetailEvent.ShowSnackbar(AppText.from(R.string.task_executed_success)))
                }.onFailureIgnoreCancellation {
                    _events.emit(TaskDetailEvent.ExecutingTaskFinished)
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
     * Sealed interface representing one-time events that the UI should react to.
     */
    sealed interface TaskDetailEvent {
        data class ShowSnackbar(val message: AppText) : TaskDetailEvent
        data object ExecutingTaskStarted : TaskDetailEvent
        data object ExecutingTaskFinished : TaskDetailEvent
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