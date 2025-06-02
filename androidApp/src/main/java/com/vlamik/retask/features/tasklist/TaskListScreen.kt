package com.vlamik.retask.features.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.vlamik.core.commons.AppText
import com.vlamik.core.domain.models.TaskItemModel
import com.vlamik.core.domain.models.TaskStatus
import com.vlamik.retask.R
import com.vlamik.retask.common.utils.preview.DeviceFormatPreview
import com.vlamik.retask.common.utils.preview.FontScalePreview
import com.vlamik.retask.common.utils.preview.ThemeModePreview
import com.vlamik.retask.component.LoadingIndicator
import com.vlamik.retask.component.appbars.ReTaskAppBar
import com.vlamik.retask.component.asString
import com.vlamik.retask.features.tasklist.TaskListViewModel.TaskListScreenUiState
import com.vlamik.retask.theme.Green
import com.vlamik.retask.theme.Orange
import com.vlamik.retask.theme.Red
import com.vlamik.retask.theme.TemplateTheme
import com.vlamik.retask.theme.dimensions

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    navigateToTaskDetail: (taskId: Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    TaskListScreenContent(
        state = state,
        onTaskClick = navigateToTaskDetail
    )
}

@Composable
private fun TaskListScreenContent(
    state: TaskListScreenUiState,
    onTaskClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            ReTaskAppBar(
                title = stringResource(id = R.string.tasks_title)
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when (state) {
                is TaskListScreenUiState.LoadingData -> {
                    LoadingIndicator()
                }

                is TaskListScreenUiState.UpdateSuccess -> TaskList(
                    tasks = state.tasks,
                    onTaskClick = onTaskClick
                )

                is TaskListScreenUiState.DataError -> ErrorState(
                    errorMessage = state.error
                )
            }

        }
    }
}

@Composable
private fun TaskList(
    tasks: List<TaskItemModel>,
    onTaskClick: (Long) -> Unit
) {

    if (tasks.isEmpty()) {
        EmptyState()
    } else {
        Surface(
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.dimensions.large,
                    vertical = MaterialTheme.dimensions.medium
                )
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large),
            color = MaterialTheme.colorScheme.surface,
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.tiny),
                modifier = Modifier.padding(vertical = MaterialTheme.dimensions.medium)
            ) {
                itemsIndexed(tasks, key = { _, task -> task.id }) { index, task ->
                    TaskListItem(
                        task = task,
                        onClicked = { onTaskClick(task.id) },
                    )
                    if (index < tasks.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = MaterialTheme.dimensions.thin,
                            modifier = Modifier.padding(
                                start = MaterialTheme.dimensions.large,
                                end = MaterialTheme.dimensions.large
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskListItem(
    task: TaskItemModel,
    onClicked: () -> Unit
) {
    val indicatorColor = when (task.status) {
        TaskStatus.GREEN -> Green
        TaskStatus.ORANGE -> Orange
        TaskStatus.RED -> Red
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClicked)
            .padding(
                horizontal = MaterialTheme.dimensions.large,
                vertical = MaterialTheme.dimensions.mediumLarge
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.large)
    ) {
        Box(
            modifier = Modifier
                .size(MaterialTheme.dimensions.iconSizeSmall)
                .background(indicatorColor, CircleShape)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (task.formattedRemainingTime.isNotBlank()) {
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.extraSmall))
                Text(
                    text = task.formattedRemainingTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = stringResource(R.string.cd_view_task_details),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(MaterialTheme.dimensions.iconSizeMedium)
        )
    }
}

@Composable
private fun ErrorState(errorMessage: AppText) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimensions.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage.asString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimensions.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ListAlt,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.dimensions.iconSizeExtraLarge),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))
        Text(
            text = stringResource(id = R.string.no_tasks_available),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))
    }
}

@ThemeModePreview
@FontScalePreview
@DeviceFormatPreview
@Composable
private fun TaskListScreenPreview_Success() {
    val sampleTasks = listOf(
        TaskItemModel(1L, "Týždenná záloha dát", TaskStatus.GREEN, "Splatné za 5 dní"),
        TaskItemModel(2L, "Upratať kúpeľňu", TaskStatus.ORANGE, "Splatné do 1 dňa"),
        TaskItemModel(3L, "Ročný servis kotla", TaskStatus.RED, "Po termíne 2 týždne"),
        TaskItemModel(
            4L,
            "Kontrola hasiacich prístrojov",
            TaskStatus.ORANGE,
            "Splatné za 3 mesiace"
        ),
        TaskItemModel(5L, "Vyčistiť kávovar", TaskStatus.GREEN, "Splatné za 4 hodiny")
    )
    TemplateTheme {
        TaskListScreenContent(
            state = TaskListScreenUiState.UpdateSuccess(sampleTasks),
            onTaskClick = {}
        )
    }
}

@ThemeModePreview
@FontScalePreview
@DeviceFormatPreview
@Composable
private fun TaskListScreenPreview_Loading() {
    TemplateTheme {
        TaskListScreenContent(
            state = TaskListScreenUiState.LoadingData,
            onTaskClick = {}
        )
    }
}

@ThemeModePreview
@FontScalePreview
@DeviceFormatPreview
@Composable
private fun TaskListScreenPreview_Error() {
    TemplateTheme {
        TaskListScreenContent(
            state = TaskListScreenUiState.DataError(AppText.from(R.string.data_error)),
            onTaskClick = {}
        )
    }
}

@ThemeModePreview
@FontScalePreview
@DeviceFormatPreview
@Composable
private fun TaskListScreenPreview_Empty() {
    TemplateTheme {
        TaskListScreenContent(
            state = TaskListScreenUiState.UpdateSuccess(emptyList()),
            onTaskClick = {}
        )
    }
}

