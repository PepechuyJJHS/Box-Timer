package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SoundTheme
import com.example.model.TimerPhase
import com.example.model.TimerStatus
import com.example.ui.theme.BoxingAmber
import com.example.ui.theme.BoxingOrange
import com.example.ui.theme.BoxingRed
import com.example.ui.theme.BoxingRedDark
import com.example.ui.theme.RestCyan
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow
import com.example.viewmodel.TimerUiState

@Composable
fun TimerDisplay(
    state: TimerUiState,
    onOpenSettings: () -> Unit,
    onAdjustQuickSeconds: (Int) -> Unit = {},
    onAdjustRounds: (Int) -> Unit = {},
    onSetRounds: (Int) -> Unit = {},
    onToggleKeepScreenOn: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val phaseColor by animateColorAsState(
        targetValue = when (state.phase) {
            TimerPhase.IDLE -> BoxingOrange
            TimerPhase.PREPARATION -> BoxingAmber
            TimerPhase.ROUND -> if (state.isWarningActive) WarningYellow else BoxingRed
            TimerPhase.REST -> RestCyan
            TimerPhase.FINISHED -> Color(0xFFFFD166)
        },
        animationSpec = tween(300),
        label = "phaseColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state.isWarningActive) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("timer_display_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phase Banner
        PhaseHeaderBadge(
            phase = state.phase,
            status = state.status,
            phaseColor = phaseColor,
            isWarning = state.isWarningActive
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Large Round Dial
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            val dialSize = minOf(maxWidth * 0.9f, 320.dp)

            Box(
                modifier = Modifier
                    .size(dialSize)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 18.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = this.center

                    drawCircle(
                        color = Color(0xFF1E1E28),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    if (animatedProgress > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    phaseColor.copy(alpha = 0.7f),
                                    phaseColor
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                // Dial Inner Content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Interactive Round Counter / Modifier
                    if (state.status != TimerStatus.RUNNING && state.phase != TimerPhase.FINISHED) {
                        RoundModifierInline(
                            currentRound = state.currentRound,
                            totalRounds = state.totalRounds,
                            onAdjustRounds = onAdjustRounds,
                            onOpenSettings = onOpenSettings
                        )
                    } else {
                        RoundCounterBadge(
                            currentRound = state.currentRound,
                            totalRounds = state.totalRounds,
                            phase = state.phase
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = state.formattedTime,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = if (dialSize < 280.dp) 64.sp else 76.sp
                        ),
                        color = if (state.isWarningActive) WarningYellow else TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable(enabled = state.status != TimerStatus.RUNNING) {
                                onOpenSettings()
                            }
                            .testTag("timer_countdown_text")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            state.status == TimerStatus.PAUSED -> "EN PAUSA"
                            state.phase == TimerPhase.ROUND && state.isWarningActive -> "¡ÚLTIMOS 10s!"
                            state.phase == TimerPhase.ROUND -> "PELEA"
                            state.phase == TimerPhase.REST -> "RESPIRA Y RECUPERA"
                            state.phase == TimerPhase.PREPARATION -> "COLÓCATE LOS GUANTES"
                            state.phase == TimerPhase.FINISHED -> "¡GRAN ENTRENAMIENTO!"
                            else -> "PRESIONA INICIAR"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.status == TimerStatus.PAUSED) BoxingOrange else TextSecondary,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Visual Rounds Dots Tracker
        RoundsDotsTracker(
            currentRound = state.currentRound,
            totalRounds = state.totalRounds,
            phase = state.phase
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Round Modification Bar when STOPPED / PAUSED
        if (state.status != TimerStatus.RUNNING && state.phase != TimerPhase.FINISHED) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rounds:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted,
                    modifier = Modifier.padding(end = 6.dp)
                )
                listOf(1, 3, 5, 8, 10, 12).forEach { r ->
                    val isSelected = state.totalRounds == r
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BoxingAmber else SurfaceDark)
                            .clickable { onSetRounds(r) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$r",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                            ),
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Status Row: Total Workout Time, Sound Theme Badge, and Keep Screen On Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tiempo Total
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariantDark)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = state.formattedTotalElapsed,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sound Theme Badge
            Surface(
                onClick = onOpenSettings,
                shape = RoundedCornerShape(12.dp),
                color = SurfaceVariantDark,
                border = BorderStroke(1.dp, BoxingOrange.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    val soundIcon = when (state.activeSoundTheme) {
                        SoundTheme.BELL_HIGH -> Icons.Default.Notifications
                        SoundTheme.BELL -> Icons.Default.Notifications
                        SoundTheme.HORN -> Icons.Default.Campaign
                        SoundTheme.GONG -> Icons.Default.Sports
                        SoundTheme.BUZZER -> Icons.Default.GraphicEq
                        SoundTheme.WHISTLE -> Icons.Default.VolumeUp
                    }
                    Icon(
                        imageVector = soundIcon,
                        contentDescription = null,
                        tint = BoxingOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = state.activeSoundTheme.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BoxingOrange
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Keep Screen On Quick Indicator
            Surface(
                onClick = onToggleKeepScreenOn,
                shape = RoundedCornerShape(12.dp),
                color = if (state.config.keepScreenOn) BoxingOrange.copy(alpha = 0.2f) else SurfaceVariantDark,
                border = BorderStroke(1.dp, if (state.config.keepScreenOn) BoxingOrange else SurfaceBorderDark),
                modifier = Modifier.testTag("toggle_keep_screen_on_badge")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Pantalla siempre encendida",
                        tint = if (state.config.keepScreenOn) BoxingOrange else TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state.config.keepScreenOn) "Pantalla ON" else "Pantalla OFF",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        color = if (state.config.keepScreenOn) BoxingOrange else TextMuted
                    )
                }
            }
        }

        // Quick seconds adjustment controls when STOPPED or PAUSED
        if (state.status != TimerStatus.RUNNING && state.phase != TimerPhase.FINISHED) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                QuickTweakButton(label = "-30s", onClick = { onAdjustQuickSeconds(-30) })
                QuickTweakButton(label = "-15s", onClick = { onAdjustQuickSeconds(-15) })
                QuickTweakButton(label = "+15s", onClick = { onAdjustQuickSeconds(15) })
                QuickTweakButton(label = "+30s", onClick = { onAdjustQuickSeconds(30) })
            }
        }
    }
}

@Composable
private fun RoundModifierInline(
    currentRound: Int,
    totalRounds: Int,
    onAdjustRounds: (Int) -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { onAdjustRounds(-1) },
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(SurfaceVariantDark)
                .testTag("decrease_rounds_dial_btn")
        ) {
            Icon(Icons.Default.Remove, contentDescription = "-1 round", tint = TextPrimary, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "ROUND $currentRound DE $totalRounds",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            ),
            color = BoxingRed,
            modifier = Modifier
                .clickable { onOpenSettings() }
                .testTag("round_counter_text")
        )

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(
            onClick = { onAdjustRounds(1) },
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(SurfaceVariantDark)
                .testTag("increase_rounds_dial_btn")
        ) {
            Icon(Icons.Default.Add, contentDescription = "+1 round", tint = TextPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun QuickTweakButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, SurfaceBorderDark)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun PhaseHeaderBadge(
    phase: TimerPhase,
    status: TimerStatus,
    phaseColor: Color,
    isWarning: Boolean
) {
    val title = when (phase) {
        TimerPhase.IDLE -> "BOX TIMER"
        TimerPhase.PREPARATION -> "PREPARACIÓN"
        TimerPhase.ROUND -> if (isWarning) "¡AVISO DE ROUND!" else "ROUND EN CURSO"
        TimerPhase.REST -> "DESCANSO"
        TimerPhase.FINISHED -> "ENTRENAMIENTO COMPLETADO"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(phaseColor.copy(alpha = 0.2f))
            .border(1.5.dp, phaseColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("phase_header_badge")
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(phaseColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = phaseColor
        )
    }
}

@Composable
private fun RoundCounterBadge(
    currentRound: Int,
    totalRounds: Int,
    phase: TimerPhase
) {
    val roundText = when (phase) {
        TimerPhase.FINISHED -> "COMPLETO"
        TimerPhase.PREPARATION -> "INICIO"
        else -> "ROUND $currentRound DE $totalRounds"
    }

    Text(
        text = roundText,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            letterSpacing = 1.2.sp
        ),
        color = BoxingRed,
        modifier = Modifier.testTag("round_counter_text")
    )
}

@Composable
private fun RoundsDotsTracker(
    currentRound: Int,
    totalRounds: Int,
    phase: TimerPhase,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val displayLimit = minOf(totalRounds, 12)
        for (i in 1..displayLimit) {
            val isCurrent = (i == currentRound && phase != TimerPhase.FINISHED)
            val isCompleted = (i < currentRound || phase == TimerPhase.FINISHED)

            val dotColor = when {
                isCurrent -> if (phase == TimerPhase.ROUND) BoxingRed else RestCyan
                isCompleted -> BoxingRedDark
                else -> SurfaceVariantDark
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (isCurrent) 10.dp else 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(dotColor)
            )
        }
    }
}
