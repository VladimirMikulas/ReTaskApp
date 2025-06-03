package com.vlamik.retask.features.taskdetail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vlamik.core.commons.AppText
import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.core.domain.usecase.ExecuteTaskUseCase
import com.vlamik.core.domain.usecase.GetTaskDetailUseCase
import com.vlamik.retask.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TaskDetailViewModel @AssistedInject constructor(
    @Assisted private val taskId: Long,
    getTaskDetailUseCase: GetTaskDetailUseCase,
    private val executeTaskUseCase: ExecuteTaskUseCase
) : ViewModel() {

    val uiState: StateFlow<UiState> = getTaskDetailUseCase(taskId)
        .map { result ->
            result.fold(
                onSuccess = { task ->
                    if (task == null) {
                        UiState.DataError(AppText.from(R.string.data_error))
                    } else {
                        UiState.Success(task)
                    }
                },
                onFailure = { throwable ->
                    UiState.DataError(
                        throwable.message?.let { AppText.dynamic(it) }
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


    private val _events = MutableSharedFlow<TaskDetailEvent>()
    val events: SharedFlow<TaskDetailEvent> = _events.asSharedFlow()

    fun onExecuteTask() {
        viewModelScope.launch {
            val currentTask = (uiState.value as? UiState.Success)?.task
            if (currentTask != null) {
                _events.emit(TaskDetailEvent.ExecutingTaskStarted)
                executeTaskUseCase(taskId)
                _events.emit(TaskDetailEvent.ShowSnackbar(AppText.from(R.string.task_executed_success)))
                _events.emit(TaskDetailEvent.ExecutingTaskFinished)
            }
        }
    }

    sealed interface UiState {
        data object LoadingData : UiState
        data class Success(val task: TaskDetailModel) : UiState
        data class DataError(val message: AppText) : UiState
    }

    sealed interface TaskDetailEvent {
        data class ShowSnackbar(val message: AppText) : TaskDetailEvent
        data object ExecutingTaskStarted : TaskDetailEvent
        data object ExecutingTaskFinished : TaskDetailEvent
    }

    @AssistedFactory
    interface Factory {
        fun create(taskId: Long): TaskDetailViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            factory: Factory,
            taskId: Long,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(taskId) as T
            }
        }
    }
}