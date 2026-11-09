package com.example.model

import com.example.R

enum class TimerPhase(val displayName: String) {
    IDLE("LISTO"),
    PREPARATION("PREPARACIÓN"),
    ROUND("¡ROUND!"),
    REST("DESCANSO"),
    FINISHED("COMPLETADO")
}

enum class TimerStatus {
    STOPPED,
    RUNNING,
    PAUSED
}

enum class SoundTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val startResId: Int,
    val endResId: Int
) {
    BELL_HIGH(
        id = "bell_high",
        title = "Campana Aguda",
        subtitle = "Tono agudo y penetrante para ring de boxeo",
        startResId = R.raw.sound_bell_high_start,
        endResId = R.raw.sound_bell_high_end
    ),
    BELL(
        id = "bell",
        title = "Campana Tradicional",
        subtitle = "Campana clásica de boxeo",
        startResId = R.raw.sound_bell_start,
        endResId = R.raw.sound_bell_end
    ),
    HORN(
        id = "horn",
        title = "Bocina Air Horn",
        subtitle = "Potente bocina de arena y combate",
        startResId = R.raw.sound_horn_start,
        endResId = R.raw.sound_horn_end
    ),
    GONG(
        id = "gong",
        title = "Gong de Dojo",
        subtitle = "Profundo y resonante para MMA y artes marciales",
        startResId = R.raw.sound_gong_start,
        endResId = R.raw.sound_gong_end
    ),
    BUZZER(
        id = "buzzer",
        title = "Chicharra Digital",
        subtitle = "Buzzer electrónico de alta intensidad",
        startResId = R.raw.sound_buzzer_start,
        endResId = R.raw.sound_buzzer_end
    ),
    WHISTLE(
        id = "whistle",
        title = "Silbato de Entrenador",
        subtitle = "Silbato tradicional de referee",
        startResId = R.raw.sound_whistle_start,
        endResId = R.raw.sound_whistle_end
    );

    companion object {
        fun fromId(id: String): SoundTheme = entries.find { it.id == id } ?: BELL_HIGH
    }
}

data class TimerConfig(
    val roundDurationSeconds: Int = 180, // 3 min por defecto
    val restDurationSeconds: Int = 60,   // 1 min por defecto
    val totalRounds: Int = 3,            // 3 rounds por defecto
    val prepDurationSeconds: Int = 10,   // 10 seg
    val warning10sEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val keepScreenOn: Boolean = true,    // Pantalla siempre encendida
    val soundThemeId: String = "bell_high" // Campana aguda por defecto
)

data class BoxingPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val roundDurationSeconds: Int,
    val restDurationSeconds: Int,
    val totalRounds: Int,
    val prepDurationSeconds: Int = 10
) {
    companion object {
        val PRESETS = listOf(
            BoxingPreset(
                id = "pro",
                name = "Boxeo Profesional",
                subtitle = "3 min pelea / 1 min descanso (12 rounds)",
                roundDurationSeconds = 180,
                restDurationSeconds = 60,
                totalRounds = 12,
                prepDurationSeconds = 10
            ),
            BoxingPreset(
                id = "amateur",
                name = "Amateur / Olímpico",
                subtitle = "3 min pelea / 1 min descanso (3 rounds)",
                roundDurationSeconds = 180,
                restDurationSeconds = 60,
                totalRounds = 3,
                prepDurationSeconds = 10
            ),
            BoxingPreset(
                id = "sparring",
                name = "Sparring Intenso",
                subtitle = "2 min pelea / 30s descanso (6 rounds)",
                roundDurationSeconds = 120,
                restDurationSeconds = 30,
                totalRounds = 6,
                prepDurationSeconds = 10
            ),
            BoxingPreset(
                id = "bag_hiit",
                name = "Saco / HIIT",
                subtitle = "1 min explosivo / 30s descanso (8 rounds)",
                roundDurationSeconds = 60,
                restDurationSeconds = 30,
                totalRounds = 8,
                prepDurationSeconds = 5
            ),
            BoxingPreset(
                id = "mma",
                name = "Championship MMA",
                subtitle = "5 min combate / 1 min descanso (5 rounds)",
                roundDurationSeconds = 300,
                restDurationSeconds = 60,
                totalRounds = 5,
                prepDurationSeconds = 10
            )
        )
    }
}
