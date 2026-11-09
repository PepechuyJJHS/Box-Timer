package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TimerPhase
import com.example.model.TimerStatus
import com.example.ui.theme.BoxingAmber
import com.example.ui.theme.BoxingOrange
import com.example.ui.theme.BoxingRed
import com.example.ui.theme.ButtonSurface
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TimerUiState

@Composable
fun ControlButtons(
    state: TimerUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = state.status == TimerStatus.RUNNING

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("controls_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quick Toggles & Skip Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sound & Vibration Toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sound Toggle
                QuickIconButton(
                    icon = if (state.config.soundEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                    contentDescription = "Alternar sonido de timbre",
                    active = state.config.soundEnabled,
                    activeColor = BoxingAmber,
                    testTag = "toggle_sound_button",
                    onClick = onToggleSound
                )

                // Vibration Toggle
                QuickIconButton(
                    icon = Icons.Default.Vibration,
                    contentDescription = "Alternar vibración",
                    active = state.config.vibrationEnabled,
                    activeColor = BoxingOrange,
                    testTag = "toggle_vibration_button",
                    onClick = onToggleVibration
                )
            }

            // Skip Round Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickIconButton(
                    icon = Icons.Default.SkipPrevious,
                    contentDescription = "Round anterior",
                    active = state.currentRound > 1,
                    activeColor = TextPrimary,
                    testTag = "skip_prev_button",
                    onClick = onSkipPrevious
                )

                QuickIconButton(
                    icon = Icons.Default.SkipNext,
                    contentDescription = "Siguiente round",
                    active = state.phase != TimerPhase.FINISHED,
                    activeColor = TextPrimary,
                    testTag = "skip_next_button",
                    onClick = onSkipNext
                )

                QuickIconButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "Configurar rounds y tiempos",
                    active = true,
                    activeColor = BoxingOrange,
                    testTag = "settings_button",
                    onClick = onOpenSettings
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PRIMARY ACTION BUTTON (INICIAR / PAUSAR) - Extra large for sweaty hands & boxing gloves
        val primaryBtnColor by animateColorAsState(
            targetValue = if (isRunning) BoxingOrange else BoxingRed,
            label = "primaryBtnColor"
        )

        Button(
            onClick = {
                if (isRunning) onPause() else onStart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .testTag("primary_play_pause_button"),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryBtnColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = when {
                        isRunning -> "PAUSAR"
                        state.status == TimerStatus.PAUSED -> "CONTINUAR"
                        else -> "INICIAR"
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        letterSpacing = 1.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SECONDARY ACTION BUTTONS: REINICIAR and CONFIGURAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // REINICIAR (Reset) Button
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("reset_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ButtonSurface,
                    contentColor = TextPrimary
                ),
                border = BorderStroke(1.5.dp, SurfaceBorderDark)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REINICIAR",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 1.sp
                    ),
                    color = TextPrimary
                )
            }

            // AJUSTES (Quick Configure) Button
            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("open_config_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ButtonSurface,
                    contentColor = BoxingOrange
                ),
                border = BorderStroke(1.5.dp, SurfaceBorderDark)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = BoxingOrange,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AJUSTES",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 1.sp
                    ),
                    color = BoxingOrange
                )
            }
        }
    }
}

@Composable
private fun QuickIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    active: Boolean,
    activeColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (active) SurfaceVariantDark else ButtonSurface.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, if (active) activeColor.copy(alpha = 0.5f) else SurfaceBorderDark),
        modifier = Modifier
            .size(50.dp)
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (active) activeColor else TextMuted,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
