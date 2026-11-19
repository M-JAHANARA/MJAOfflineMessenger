package com.mja.messenger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mja.messenger.ui.components.MjaBottomBar
import com.mja.messenger.ui.components.mjaInsets
import com.mja.messenger.ui.theme.MjaBackground

/**
 * Chrome for the five bottom-navigation destinations.
 * Handles system insets + bottom bar; tab screens only receive [PaddingValues].
 */
@Composable
fun MainScaffold(
    current: Int,
    onTab: (Int) -> Unit,
    unreadCount: Int,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .mjaInsets()
            .background(MjaBackground),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = com.mja.messenger.ui.theme.MjaTextPrimary,
            bottomBar = {
                MjaBottomBar(
                    currentIndex = current,
                    onSelect = onTab,
                    unreadCount = unreadCount,
                )
            },
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        ) { inner ->
            content(inner)
        }
    }
}
