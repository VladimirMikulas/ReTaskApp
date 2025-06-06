package com.vlamik.retask.features.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlamik.core.commons.AppText
import com.vlamik.core.commons.DispatcherProvider
import com.vlamik.core.commons.onFailureIgnoreCancellation
import com.vlamik.core.domain.usecase.GetTaskListUseCase
import com.vlamik.retask.R
import com.vlamik.retask.commons.StringResourceProvider
import com.vlamik.retask.mappers.TaskUiMapper.toTaskItemUiModel
import com.vlamik.retask.models.TaskItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * ViewModel for the Task List screen.
 * Responsible for fetching and managing the list of tasks to be displayed.
 * Uses Hilt for dependency injection.
 */
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTaskListUseCase: GetTaskListUseCase,
    private val stringResourceProvider: StringResourceProvider,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _state = MutableStateFlow<TaskListScreenUiState>(TaskListScreenUiState.LoadingData)
    val state: StateFlow<TaskListScreenUiState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    /**
     * Loads tasks from the repository and updates the UI state accordingly.
     * Uses collectLatest to ensure only the latest data emission is processed.
     */
    private fun loadTasks() {
        viewModelScope.launch {
            getTaskListUseCase().collectLatest { result ->
                result
                    .onSuccess { tasks ->
                        val taskItemUiModels = withContext(dispatcherProvider.default) {
                            tasks.map {
                                it.toTaskItemUiModel(stringResourceProvider)
                            }
                        }
                        _state.value = TaskListScreenUiState.UpdateSuccess(taskItemUiModels)
                    }
                    .onFailureIgnoreCancellation { throwable ->
                        _state.value = TaskListScreenUiState.DataError(throwable.message?.let {
                            AppText.dynamic(it)
                        } ?: AppText.from(R.string.data_error))
                    }
            }
        }
    }

    /**
     * Sealed interface representing the various UI states of the Task List screen.
     */
    sealed interface TaskListScreenUiState {
        data object LoadingData : TaskListScreenUiState
        data class UpdateSuccess(
            val tasks: List<TaskItemUiModel>
        ) : TaskListScreenUiState

        data class DataError(val error: AppText) : TaskListScreenUiState
    }
}