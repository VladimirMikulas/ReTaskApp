@file:Suppress("MatchingDeclarationName")

package com.vlamik.retask.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vlamik.retask.MainActivity.ViewModelFactoryProvider
import com.vlamik.retask.features.taskdetail.TaskDetailViewModel
import dagger.hilt.android.EntryPointAccessors

interface BaseViewModelFactoryProvider {
    fun getTaskDetailViewModelFactory(): TaskDetailViewModel.Factory
}

@Composable
fun taskDetailViewModel(taskId: String): TaskDetailViewModel = viewModel(
    factory = TaskDetailViewModel.provideFactory(
        getViewModelFactoryProvider().getTaskDetailViewModelFactory(), taskId = taskId.toLong()
    )
)
@Composable
private fun getViewModelFactoryProvider() = EntryPointAccessors.fromActivity(
    LocalActivity.current!!,
    ViewModelFactoryProvider::class.java
)
