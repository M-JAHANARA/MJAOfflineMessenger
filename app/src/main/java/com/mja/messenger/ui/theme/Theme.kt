package com.mja.messenger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val MjaColorScheme = darkColorScheme(
    primary = MjaBrand,
    onPrimary = MjaBackground,
    primaryContainer = MjaElevated,
    onPrimaryContainer = MjaTextPrimary,
    secondary = MjaAccent,
    onSecondary = MjaBackground,
    secondaryContainer = MjaCard,
    onSecondaryContainer = MjaTextPrimary,
    tertiary = MjaSuccess,
    onTertiary = MjaBackground,
    background = MjaBackground,
    onBackground = MjaTextPrimary,
    surface = MjaBackground2,
    onSurface = MjaTextPrimary,
    surfaceVariant = MjaCard,
    onSurfaceVariant = MjaTextSecondary,
    surfaceContainer = MjaCard,
    surfaceContainerLow = MjaBackground2,
    surfaceContainerHigh = MjaElevated,
    surfaceContainerHighest = MjaElevated,
    outline = MjaBorder,
    outlineVariant = MjaBorder,
    error = MjaError,
    onError = MjaBackground,
    errorContainer = MjaCard,
    onErrorContainer = MjaError,
    scrim = androidx.compose.ui.graphics.Color.Black,
)

/**
 * MJA application theme.
 * Forces RTL for the whole product (Persian primary language) and installs the
 * "Black & Graphite" dark scheme everywhere.
 */
@Composable
fun MjaTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = MjaColorScheme,
            typography = MjaTypography,
            content = content,
        )
    }
}
