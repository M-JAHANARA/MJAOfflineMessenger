package com.mja.messenger.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier

/**
 * Standard screen insets for MJA:
 * status bar + navigation bar + keyboard (IME) handling.
 * Apply to the root of EVERY screen.
 */
fun Modifier.mjaInsets(): Modifier =
    this
        .windowInsetsPadding(WindowInsets.systemBars)
        .imePadding()
