package com.vlamik.retask.component.appbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vlamik.retask.R
import com.vlamik.retask.theme.SoftGray
import com.vlamik.retask.theme.dimensions


@Composable
fun ReTaskAppBar(
    title: String,
    modifier: Modifier = Modifier,
    addBackButton: Boolean = false,
    backButtonClickAction: () -> Unit = {}
) {
    var navigationIconWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Surface(
        shadowElevation = MaterialTheme.dimensions.small,
        color = SoftGray,
        modifier = modifier
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                if (addBackButton) {
                    Row(
                        modifier = Modifier
                            .padding(start = MaterialTheme.dimensions.medium)
                            .clickable(onClick = { backButtonClickAction() })
                            .onSizeChanged { size ->
                                navigationIconWidth = with(density) { size.width.toDp() }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            actions = {
                if (addBackButton) {
                    Spacer(modifier = Modifier.width(navigationIconWidth))
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

