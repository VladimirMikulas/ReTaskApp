package com.vlamik.retask.features.taskdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.vlamik.core.domain.models.TaskDetailModel
import com.vlamik.retask.R
import com.vlamik.retask.common.utils.asString
import com.vlamik.retask.common.utils.preview.DeviceFormatPreview
import com.vlamik.retask.common.utils.preview.FontScalePreview
import com.vlamik.retask.common.utils.preview.ThemeModePreview
import com.vlamik.retask.component.ErrorMessage
import com.vlamik.retask.component.LoadingIndicator
import com.vlamik.retask.component.appbars.ReTaskAppBar
import com.vlamik.retask.theme.TemplateTheme
import com.vlamik.retask.theme.dimensions

@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var isExecutingTask by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TaskDetailViewModel.TaskDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }

                is TaskDetailViewModel.TaskDetailEvent.ExecutingTaskStarted -> {
                    isExecutingTask = true
                }

                TaskDetailViewModel.TaskDetailEvent.ExecutingTaskFinished -> {
                    isExecutingTask = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ReTaskAppBar(
                title = (uiState as? TaskDetailViewModel.UiState.Success)?.task?.name
                    ?: stringResource(R.string.task_details_title_fallback),
                addBackButton = true,
                backButtonClickAction = onBackClicked
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val taskDetail = (uiState as? TaskDetailViewModel.UiState.Success)?.task
            Button(
                onClick = viewModel::onExecuteTask,
                enabled = !isExecutingTask && (taskDetail?.canExecute ?: false),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimensions.buttonHeight + MaterialTheme.dimensions.large)
                    .padding(
                        start = MaterialTheme.dimensions.large,
                        end = MaterialTheme.dimensions.large,
                        bottom = MaterialTheme.dimensions.large
                    )
            ) {
                if (isExecutingTask) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(MaterialTheme.dimensions.iconSizeLarge),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(R.string.execute_task_button))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.dimensions.large)
        ) {
            when (val state = uiState) {
                TaskDetailViewModel.UiState.LoadingData -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                is TaskDetailViewModel.UiState.DataError -> {
                    ErrorMessage(errorMessage = state.message)
                }

                is TaskDetailViewModel.UiState.Success -> {
                    TaskDetailContent(
                        task = state.task
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: TaskDetailModel
) {
    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.dimensions.large)
        ) {
            Text(
                text = stringResource(R.string.task_description_label),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))

            Text(
                text = stringResource(R.string.execution_count_label),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
            Text(
                text = task.numberOfExecutions.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@ThemeModePreview
@FontScalePreview
@DeviceFormatPreview
@Composable
private fun TaskDetailScreenPreview_Success() {
    TemplateTheme {
        TaskDetailContent(
            task = TaskDetailModel(
                id = 1L,
                name = "Weekly Data Backup",
                description = "Prepare an external drive and back up important work and personal files. Check backup integrity. Ensure all critical documents are secured off-site.",
                numberOfExecutions = 15,
                canExecute = true
            )
        )
    }
}

@ThemeModePreview
@FontScalePreview
@DeviceFormatPreview
@Composable
private fun TaskDetailScreenPreview_Executing() {
    TemplateTheme {
        TaskDetailContent(
            task = TaskDetailModel(
                id = 1L,
                name = "Weekly Data Backup",
                description = "Prepare an external drive and back up important work and personal files. Check backup integrity.",
                numberOfExecutions = 15,
                canExecute = true,
            )
        )
    }
}

@ThemeModePreview
@FontScalePreview
@DeviceFormatPreview
@Composable
private fun TaskDetailScreenPreview_DisabledButton() {
    TemplateTheme {
        TaskDetailContent(
            task = TaskDetailModel(
                id = 2L,
                name = "Daily Meditation",
                description = "Meditate for 15 minutes to improve focus and reduce stress. Use a guided meditation app or practice mindfulness.",
                numberOfExecutions = 10,
                canExecute = false
            )
        )
    }
}