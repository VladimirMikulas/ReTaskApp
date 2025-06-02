package com.vlamik.retask.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    // Spacing / padding
    val none: Dp = 0.dp,
    val tiny: Dp = 1.dp,
    val extraSmall: Dp = 2.dp,
    val small: Dp = 4.dp,
    val medium: Dp = 8.dp,
    val mediumLarge: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,

    // Divider / lines
    val hairline: Dp = 0.5.dp,
    val thin: Dp = 1.dp,
    val thick: Dp = 2.dp,

    // Component sizes
    val iconSizeSmall: Dp = 20.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 32.dp,
    val iconSizeExtraLarge: Dp = 48.dp,
    val buttonHeightSmall: Dp = 36.dp,
    val buttonHeight: Dp = 48.dp,
    val fabSize: Dp = 56.dp,
    val listItemHeight: Dp = 56.dp,
    val textFieldHeight: Dp = 56.dp
)

val LocalSpacing = staticCompositionLocalOf { Dimensions() }
