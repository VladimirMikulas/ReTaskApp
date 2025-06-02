@file:Suppress("MatchingDeclarationName")

package com.vlamik.retask.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import com.vlamik.retask.MainActivity.ViewModelFactoryProvider
import dagger.hilt.android.EntryPointAccessors

interface BaseViewModelFactoryProvider {
//    fun getRocketDetailViewModelFactory(): TaksDetailViewModel.Factory
}

@Composable
private fun getViewModelFactoryProvider() = EntryPointAccessors.fromActivity(
    LocalActivity.current!!,
    ViewModelFactoryProvider::class.java
)
