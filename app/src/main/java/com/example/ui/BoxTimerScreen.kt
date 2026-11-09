package com.example.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.TimerPhase
import com.example.model.TimerStatus
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BoxingOrange
import com.example.ui.theme.BoxingRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.BoxTimerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxTimerScreen(
    viewModel: BoxTimerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSettingsOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Keep screen ON switch handler during boxing workout session
    val context = LocalContext.current
    DisposableEffect(uiState.config.keepScreenOn, uiState.status) {
        val window = (context as? Activity)?.window
        val shouldKeepOn = uiState.config.keepScreenOn && uiState.status == TimerStatus.RUNNING
        if (shouldKeepOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        containerColor = BackgroundDark
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            val isWideScreen = maxWidth > 600.dp

            if (isWideScreen) {
                // LANDSCAPE / TABLET LAYOUT: Side-by-side
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        TimerDisplay(
                            state = uiState,
                            onOpenSettings = { isSettingsOpen = true },
                            onAdjustQuickSeconds = { delta ->
                                if (uiState.phase == TimerPhase.REST) {
                                    viewModel.adjustRestDuration(delta)
                                } else {
                                    viewModel.adjustRoundDuration(delta)
                                }
                            },
                            onAdjustRounds = { delta -> viewModel.adjustTotalRounds(delta) },
                            onSetRounds = { r -> viewModel.setTotalRounds(r) },
                            onToggleKeepScreenOn = { viewModel.toggleKeepScreenOn() },
                            modifier = Modifier.widthIn(max = 420.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HeaderBar(
                            onOpenSettings = { isSettingsOpen = true },
                            onToggleKeepScreenOn = { viewModel.toggleKeepScreenOn() },
                            keepScreenOn = uiState.config.keepScreenOn
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ControlButtons(
                            state = uiState,
                            onStart = { viewModel.onStartClicked() },
                            onPause = { viewModel.onPauseClicked() },
                            onReset = { viewModel.onResetClicked() },
                            onSkipNext = { viewModel.onSkipToNext() },
                            onSkipPrevious = { viewModel.onSkipToPrevious() },
                            onOpenSettings = { isSettingsOpen = true },
                            onToggleSound = { viewModel.toggleSound() },
                            onToggleVibration = { viewModel.toggleVibration() },
                            modifier = Modifier.widthIn(max = 440.dp)
                        )
                    }
                }
            } else {
                // PORTRAIT MOBILE LAYOUT: Vertical Stack
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    HeaderBar(
                        onOpenSettings = { isSettingsOpen = true },
                        onToggleKeepScreenOn = { viewModel.toggleKeepScreenOn() },
                        keepScreenOn = uiState.config.keepScreenOn
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TimerDisplay(
                        state = uiState,
                        onOpenSettings = { isSettingsOpen = true },
                        onAdjustQuickSeconds = { delta ->
                            if (uiState.phase == TimerPhase.REST) {
                                viewModel.adjustRestDuration(delta)
                            } else {
                                viewModel.adjustRoundDuration(delta)
                            }
                        },
                        onAdjustRounds = { delta -> viewModel.adjustTotalRounds(delta) },
                        onSetRounds = { r -> viewModel.setTotalRounds(r) },
                        onToggleKeepScreenOn = { viewModel.toggleKeepScreenOn() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ControlButtons(
                        state = uiState,
                        onStart = { viewModel.onStartClicked() },
                        onPause = { viewModel.onPauseClicked() },
                        onReset = { viewModel.onResetClicked() },
                        onSkipNext = { viewModel.onSkipToNext() },
                        onSkipPrevious = { viewModel.onSkipToPrevious() },
                        onOpenSettings = { isSettingsOpen = true },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleVibration = { viewModel.toggleVibration() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    }

    // Modal Settings Sheet
    if (isSettingsOpen) {
        SettingsSheet(
            config = uiState.config,
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    isSettingsOpen = false
                }
            },
            onSaveConfig = { newConfig ->
                viewModel.updateConfig(newConfig)
            },
            onPreviewSound = { theme ->
                viewModel.previewSoundTheme(theme)
            }
        )
    }
}

@Composable
private fun HeaderBar(
    onOpenSettings: () -> Unit,
    onToggleKeepScreenOn: () -> Unit,
    keepScreenOn: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title & Gym Badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BoxingRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "BOX TIMER",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Campana Aguda & Rounds Pro",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Keep Screen On Quick Header Toggle
            IconButton(
                onClick = onToggleKeepScreenOn,
                modifier = Modifier.testTag("header_keep_screen_on_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Pantalla siempre encendida",
                    tint = if (keepScreenOn) BoxingOrange else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Settings button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("header_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = BoxingOrange,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
