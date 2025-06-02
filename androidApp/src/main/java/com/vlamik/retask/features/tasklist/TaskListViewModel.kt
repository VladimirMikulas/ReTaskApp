package com.vlamik.retask.features.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlamik.core.commons.AppText
import com.vlamik.core.domain.models.TaskItemModel
import com.vlamik.core.domain.repository.TaskRepository
import com.vlamik.retask.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<TaskListScreenUiState>(TaskListScreenUiState.LoadingData)
    val state: StateFlow<TaskListScreenUiState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.value = TaskListScreenUiState.LoadingData
            taskRepository.getAllTasks().collectLatest { result ->
                result
                    .onSuccess { tasks ->
                        _state.value = TaskListScreenUiState.UpdateSuccess(tasks)
                    }
                    .onFailure { throwable ->
                        _state.value = TaskListScreenUiState.DataError(throwable.message?.let {
                            AppText.dynamic(it)
                        } ?: AppText.from(R.string.data_error))
                    }
            }
        }
    }

    sealed interface TaskListScreenUiState {
        data object LoadingData : TaskListScreenUiState
        data class UpdateSuccess(
            val tasks: List<TaskItemModel>
        ) : TaskListScreenUiState

        data class DataError(val error: AppText) : TaskListScreenUiState
    }
}