package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoxingPreset
import com.example.model.SoundTheme
import com.example.model.TimerConfig
import com.example.ui.theme.BoxingAmber
import com.example.ui.theme.BoxingOrange
import com.example.ui.theme.BoxingRed
import com.example.ui.theme.RestCyan
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    config: TimerConfig,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSaveConfig: (TimerConfig) -> Unit,
    onPreviewSound: (SoundTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    var roundSec by remember(config) { mutableIntStateOf(config.roundDurationSeconds) }
    var restSec by remember(config) { mutableIntStateOf(config.restDurationSeconds) }
    var rounds by remember(config) { mutableIntStateOf(config.totalRounds) }
    var prepSec by remember(config) { mutableIntStateOf(config.prepDurationSeconds) }
    var selectedSoundTheme by remember(config) { mutableStateOf(config.soundThemeId) }
    var soundEnabled by remember(config) { mutableStateOf(config.soundEnabled) }
    var vibrationEnabled by remember(config) { mutableStateOf(config.vibrationEnabled) }
    var warningEnabled by remember(config) { mutableStateOf(config.warning10sEnabled) }
    var keepScreenOn by remember(config) { mutableStateOf(config.keepScreenOn) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        dragHandle = null,
        modifier = modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CONFIGURACIÓN",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            letterSpacing = 1.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "Rounds, tiempos y campana aguda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar ajustes",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // SECCIÓN: PANTALLA SIEMPRE ENCENDIDA (SWITCH)
            // ==========================================
            Text(
                text = "PANTALLA Y ENERGÍA",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = BoxingOrange,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceVariantDark,
                border = BorderStroke(1.dp, SurfaceBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (keepScreenOn) BoxingOrange else SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = if (keepScreenOn) Color.White else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pantalla siempre encendida",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = "Evita que el dispositivo entre en reposo",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = TextMuted
                            )
                        }
                    }

                    Switch(
                        checked = keepScreenOn,
                        onCheckedChange = { keepScreenOn = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BoxingOrange,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceDark
                        ),
                        modifier = Modifier.testTag("switch_keep_screen_on")
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ==========================================
            // SECCIÓN: MODIFICAR CANTIDAD DE ROUNDS
            // ==========================================
            Text(
                text = "MODIFICAR CANTIDAD DE ROUNDS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = BoxingOrange,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomRoundsPickerCard(
                rounds = rounds,
                accentColor = BoxingAmber,
                onAdjustRounds = { delta ->
                    rounds = (rounds + delta).coerceIn(1, 50)
                },
                quickOptions = listOf(1, 2, 3, 4, 5, 6, 8, 10, 12, 15),
                onSelectQuick = { rounds = it },
                testTagPrefix = "rounds_custom"
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ==========================================
            // SECCIÓN: SONIDOS CHIDOS INTERCAMBIABLES
            // ==========================================
            Text(
                text = "SONIDOS INTERCAMBIABLES (CAMPANA AGUDA Y MÁS)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = BoxingOrange,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Toca 'Probar' para escuchar la campana más aguda y los otros timbres:",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SoundTheme.entries.forEach { theme ->
                    val isSelected = selectedSoundTheme == theme.id

                    SoundThemeCard(
                        theme = theme,
                        isSelected = isSelected,
                        onSelect = { selectedSoundTheme = theme.id },
                        onPreview = { onPreviewSound(theme) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ==========================================
            // SECCIÓN: PREAJUSTES RÁPIDOS
            // ==========================================
            Text(
                text = "MODOS PREESTABLECIDOS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = BoxingOrange,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(BoxingPreset.PRESETS) { preset ->
                    val isSelected = roundSec == preset.roundDurationSeconds &&
                            restSec == preset.restDurationSeconds &&
                            rounds == preset.totalRounds

                    PresetChip(
                        preset = preset,
                        isSelected = isSelected,
                        onClick = {
                            roundSec = preset.roundDurationSeconds
                            restSec = preset.restDurationSeconds
                            rounds = preset.totalRounds
                            prepSec = preset.prepDurationSeconds
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ==========================================
            // SECCIÓN: TIEMPOS A GUSTO (PERSONALIZACIÓN TOTAL)
            // ==========================================
            Text(
                text = "DURACIÓN DE ROUNDS Y DESCANSO",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = BoxingOrange,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // DURACIÓN DEL ROUND
            CustomTimePickerCard(
                title = "DURACIÓN DEL ROUND",
                totalSeconds = roundSec,
                accentColor = BoxingRed,
                onAdjustMinutes = { delta ->
                    val m = (roundSec / 60) + delta
                    val s = roundSec % 60
                    val newTotal = (m * 60 + s).coerceIn(5, 3600)
                    roundSec = newTotal
                },
                onAdjustSeconds = { delta ->
                    val newTotal = (roundSec + delta).coerceIn(5, 3600)
                    roundSec = newTotal
                },
                quickOptions = listOf(30 to "30s", 60 to "1m", 120 to "2m", 180 to "3m", 300 to "5m"),
                onSelectQuick = { roundSec = it },
                testTagPrefix = "round_custom"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // DURACIÓN DEL DESCANSO
            CustomTimePickerCard(
                title = "DURACIÓN DEL DESCANSO",
                totalSeconds = restSec,
                accentColor = RestCyan,
                onAdjustMinutes = { delta ->
                    val m = (restSec / 60) + delta
                    val s = restSec % 60
                    val newTotal = (m * 60 + s).coerceIn(5, 1800)
                    restSec = newTotal
                },
                onAdjustSeconds = { delta ->
                    val newTotal = (restSec + delta).coerceIn(5, 1800)
                    restSec = newTotal
                },
                quickOptions = listOf(15 to "15s", 30 to "30s", 45 to "45s", 60 to "1m", 90 to "1.5m"),
                onSelectQuick = { restSec = it },
                testTagPrefix = "rest_custom"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // TIEMPO DE PREPARACIÓN
            CustomPrepPickerCard(
                prepSeconds = prepSec,
                accentColor = BoxingOrange,
                onAdjustSeconds = { delta ->
                    prepSec = (prepSec + delta).coerceIn(0, 120)
                },
                quickOptions = listOf(0 to "0s", 5 to "5s", 10 to "10s", 15 to "15s", 30 to "30s"),
                onSelectQuick = { prepSec = it },
                testTagPrefix = "prep_custom"
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ==========================================
            // SECCIÓN: ALERTAS
            // ==========================================
            Text(
                text = "OPCIONES DE SONIDO Y VIBRACIÓN",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = BoxingOrange,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceVariantDark,
                border = BorderStroke(1.dp, SurfaceBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "Sonido Habilitado",
                        subtitle = "Reproduce la campana en cada inicio y fin de round",
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingToggleRow(
                        title = "Vibración Háptica",
                        subtitle = "Siente el timbrazo en tu dispositivo",
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingToggleRow(
                        title = "Aviso a los 10 segundos",
                        subtitle = "Golpes de madera y pantalla pulsante antes de terminar",
                        checked = warningEnabled,
                        onCheckedChange = { warningEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Guardar y Aplicar Button
            Button(
                onClick = {
                    onSaveConfig(
                        TimerConfig(
                            roundDurationSeconds = roundSec,
                            restDurationSeconds = restSec,
                            totalRounds = rounds,
                            prepDurationSeconds = prepSec,
                            soundEnabled = soundEnabled,
                            vibrationEnabled = vibrationEnabled,
                            warning10sEnabled = warningEnabled,
                            keepScreenOn = keepScreenOn,
                            soundThemeId = selectedSoundTheme
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .testTag("save_config_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BoxingRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "GUARDAR Y APLICAR CAMBIOS",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 1.2.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SoundThemeCard(
    theme: SoundTheme,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    val themeIcon = when (theme) {
        SoundTheme.BELL_HIGH -> Icons.Default.Notifications
        SoundTheme.BELL -> Icons.Default.Notifications
        SoundTheme.HORN -> Icons.Default.Campaign
        SoundTheme.GONG -> Icons.Default.Sports
        SoundTheme.BUZZER -> Icons.Default.GraphicEq
        SoundTheme.WHISTLE -> Icons.Default.VolumeUp
    }

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) BoxingRed.copy(alpha = 0.18f) else SurfaceVariantDark,
        border = BorderStroke(1.5.dp, if (isSelected) BoxingRed else SurfaceBorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sound_theme_card_${theme.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) BoxingRed else SurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = themeIcon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else BoxingOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = theme.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextPrimary
                        )
                        if (theme == SoundTheme.BELL_HIGH) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BoxingOrange)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "MÁS AGUDA",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                            }
                        }
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BoxingRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVO",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Text(
                        text = theme.subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = TextSecondary
                    )
                }
            }

            Button(
                onClick = onPreview,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) BoxingRed else SurfaceDark,
                    contentColor = Color.White
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("preview_sound_${theme.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Probar",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTimePickerCard(
    title: String,
    totalSeconds: Int,
    accentColor: Color,
    onAdjustMinutes: (Int) -> Unit,
    onAdjustSeconds: (Int) -> Unit,
    quickOptions: List<Pair<Int, String>>,
    onSelectQuick: (Int) -> Unit,
    testTagPrefix: String
) {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceVariantDark,
        border = BorderStroke(1.dp, SurfaceBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp
                    ),
                    color = accentColor,
                    modifier = Modifier.testTag("${testTagPrefix}_display")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Minutos Stepper
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MINUTOS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { onAdjustMinutes(-1) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "-1 min", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "${minutes}m",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        IconButton(
                            onClick = { onAdjustMinutes(1) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "+1 min", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Segundos Stepper (+/- 5s)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SEGUNDOS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { onAdjustSeconds(-5) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "-5 seg", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "${seconds}s",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        IconButton(
                            onClick = { onAdjustSeconds(5) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "+5 seg", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Pill Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickOptions.forEach { (value, label) ->
                    val isSelected = totalSeconds == value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accentColor else SurfaceDark)
                            .clickable { onSelectQuick(value) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomRoundsPickerCard(
    rounds: Int,
    accentColor: Color,
    onAdjustRounds: (Int) -> Unit,
    quickOptions: List<Int>,
    onSelectQuick: (Int) -> Unit,
    testTagPrefix: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceVariantDark,
        border = BorderStroke(1.dp, SurfaceBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL DE ROUNDS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Modifica la cantidad de asaltos",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = TextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { onAdjustRounds(-1) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .testTag("${testTagPrefix}_decrease")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "-1 round", tint = TextPrimary)
                    }

                    Text(
                        text = "$rounds",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        ),
                        color = accentColor,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .testTag("${testTagPrefix}_display")
                    )

                    IconButton(
                        onClick = { onAdjustRounds(1) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .testTag("${testTagPrefix}_increase")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+1 round", tint = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick round chips (1 a 15)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickOptions) { r ->
                    val isSelected = rounds == r
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) accentColor else SurfaceDark)
                            .clickable { onSelectQuick(r) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$r",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomPrepPickerCard(
    prepSeconds: Int,
    accentColor: Color,
    onAdjustSeconds: (Int) -> Unit,
    quickOptions: List<Pair<Int, String>>,
    onSelectQuick: (Int) -> Unit,
    testTagPrefix: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceVariantDark,
        border = BorderStroke(1.dp, SurfaceBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TIEMPO DE PREPARACIÓN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Espera inicial para colocarte guantes",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = TextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onAdjustSeconds(-5) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "-5 seg", tint = TextPrimary)
                    }

                    Text(
                        text = if (prepSeconds == 0) "0s" else "${prepSeconds}s",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        ),
                        color = accentColor,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .testTag("${testTagPrefix}_display")
                    )

                    IconButton(
                        onClick = { onAdjustSeconds(5) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+5 seg", tint = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickOptions.forEach { (value, label) ->
                    val isSelected = prepSeconds == value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accentColor else SurfaceDark)
                            .clickable { onSelectQuick(value) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    preset: BoxingPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) BoxingRed.copy(alpha = 0.25f) else SurfaceVariantDark,
        border = BorderStroke(1.5.dp, if (isSelected) BoxingRed else SurfaceBorderDark),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = if (isSelected) BoxingRed else BoxingOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = preset.name,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    color = if (isSelected) TextPrimary else TextSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${preset.roundDurationSeconds / 60}m round • ${preset.restDurationSeconds}s descanso",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextMuted
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BoxingRed,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceDark
            )
        )
    }
}
