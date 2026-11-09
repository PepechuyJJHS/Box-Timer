package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.BoxingSoundManager
import com.example.model.BoxingPreset
import com.example.model.SoundTheme
import com.example.model.TimerConfig
import com.example.model.TimerPhase
import com.example.model.TimerStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val phase: TimerPhase = TimerPhase.IDLE,
    val status: TimerStatus = TimerStatus.STOPPED,
    val currentRound: Int = 1,
    val totalRounds: Int = 3,
    val timeRemainingSeconds: Int = 180,
    val totalPhaseDurationSeconds: Int = 180,
    val totalElapsedWorkoutSeconds: Int = 0,
    val config: TimerConfig = TimerConfig(),
    val isWarningActive: Boolean = false
) {
    val progress: Float
        get() = if (totalPhaseDurationSeconds > 0) {
            (timeRemainingSeconds.toFloat() / totalPhaseDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val formattedTime: String
        get() {
            val minutes = timeRemainingSeconds / 60
            val seconds = timeRemainingSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }

    val formattedTotalElapsed: String
        get() {
            val minutes = totalElapsedWorkoutSeconds / 60
            val seconds = totalElapsedWorkoutSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }

    val activeSoundTheme: SoundTheme
        get() = SoundTheme.fromId(config.soundThemeId)
}

class BoxTimerViewModel(application: Application) : AndroidViewModel(application) {

    private val soundManager = BoxingSoundManager(application.applicationContext)

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onStartClicked() {
        when (_uiState.value.status) {
            TimerStatus.STOPPED -> {
                startFromBeginning()
            }
            TimerStatus.PAUSED -> {
                resumeTimer()
            }
            TimerStatus.RUNNING -> {
                // Already running
            }
        }
    }

    fun onPauseClicked() {
        if (_uiState.value.status == TimerStatus.RUNNING) {
            timerJob?.cancel()
            _uiState.update { it.copy(status = TimerStatus.PAUSED) }
        }
    }

    fun onResetClicked() {
        timerJob?.cancel()
        val currentConfig = _uiState.value.config
        _uiState.update {
            it.copy(
                phase = TimerPhase.IDLE,
                status = TimerStatus.STOPPED,
                currentRound = 1,
                timeRemainingSeconds = currentConfig.roundDurationSeconds,
                totalPhaseDurationSeconds = currentConfig.roundDurationSeconds,
                totalElapsedWorkoutSeconds = 0,
                isWarningActive = false
            )
        }
    }

    fun onSkipToNext() {
        val current = _uiState.value
        val theme = SoundTheme.fromId(current.config.soundThemeId)
        if (current.phase == TimerPhase.ROUND) {
            soundManager.playRoundEnd(theme, current.config.soundEnabled, current.config.vibrationEnabled)
            if (current.currentRound < current.totalRounds) {
                transitionToRest()
            } else {
                finishWorkout()
            }
        } else if (current.phase == TimerPhase.REST || current.phase == TimerPhase.PREPARATION) {
            val nextRound = if (current.phase == TimerPhase.REST) current.currentRound + 1 else 1
            transitionToRound(nextRound)
        }
    }

    fun onSkipToPrevious() {
        val current = _uiState.value
        if (current.currentRound > 1) {
            val targetRound = current.currentRound - 1
            transitionToRound(targetRound)
        } else {
            onResetClicked()
        }
    }

    fun previewSoundTheme(theme: SoundTheme) {
        soundManager.previewSoundTheme(theme)
    }

    fun setSoundTheme(themeId: String) {
        val newConfig = _uiState.value.config.copy(soundThemeId = themeId)
        updateConfig(newConfig)
    }

    fun applyPreset(preset: BoxingPreset) {
        val newConfig = _uiState.value.config.copy(
            roundDurationSeconds = preset.roundDurationSeconds,
            restDurationSeconds = preset.restDurationSeconds,
            totalRounds = preset.totalRounds,
            prepDurationSeconds = preset.prepDurationSeconds
        )
        updateConfig(newConfig)
    }

    fun updateConfig(newConfig: TimerConfig) {
        val isStopped = _uiState.value.status == TimerStatus.STOPPED
        _uiState.update {
            it.copy(
                config = newConfig,
                totalRounds = newConfig.totalRounds,
                timeRemainingSeconds = if (isStopped) newConfig.roundDurationSeconds else it.timeRemainingSeconds,
                totalPhaseDurationSeconds = if (isStopped) newConfig.roundDurationSeconds else it.totalPhaseDurationSeconds
            )
        }
    }

    fun adjustRoundDuration(deltaSeconds: Int) {
        val current = _uiState.value.config.roundDurationSeconds
        val updated = (current + deltaSeconds).coerceIn(5, 3600) // 5s to 60m
        updateConfig(_uiState.value.config.copy(roundDurationSeconds = updated))
    }

    fun setRoundMinutesAndSeconds(minutes: Int, seconds: Int) {
        val total = (minutes * 60 + seconds).coerceIn(5, 3600)
        updateConfig(_uiState.value.config.copy(roundDurationSeconds = total))
    }

    fun adjustRestDuration(deltaSeconds: Int) {
        val current = _uiState.value.config.restDurationSeconds
        val updated = (current + deltaSeconds).coerceIn(5, 1800) // 5s to 30m
        updateConfig(_uiState.value.config.copy(restDurationSeconds = updated))
    }

    fun setRestMinutesAndSeconds(minutes: Int, seconds: Int) {
        val total = (minutes * 60 + seconds).coerceIn(5, 1800)
        updateConfig(_uiState.value.config.copy(restDurationSeconds = total))
    }

    fun adjustTotalRounds(delta: Int) {
        val current = _uiState.value.config.totalRounds
        val updated = (current + delta).coerceIn(1, 50)
        updateConfig(_uiState.value.config.copy(totalRounds = updated))
    }

    fun adjustPrepDuration(deltaSeconds: Int) {
        val current = _uiState.value.config.prepDurationSeconds
        val updated = (current + deltaSeconds).coerceIn(0, 120)
        updateConfig(_uiState.value.config.copy(prepDurationSeconds = updated))
    }

    fun setTotalRounds(rounds: Int) {
        val updated = rounds.coerceIn(1, 50)
        updateConfig(_uiState.value.config.copy(totalRounds = updated))
    }

    fun toggleKeepScreenOn() {
        val newConfig = _uiState.value.config.copy(keepScreenOn = !_uiState.value.config.keepScreenOn)
        updateConfig(newConfig)
    }

    fun toggleSound() {
        val newConfig = _uiState.value.config.copy(soundEnabled = !_uiState.value.config.soundEnabled)
        updateConfig(newConfig)
    }

    fun toggleVibration() {
        val newConfig = _uiState.value.config.copy(vibrationEnabled = !_uiState.value.config.vibrationEnabled)
        updateConfig(newConfig)
    }

    fun toggle10sWarning() {
        val newConfig = _uiState.value.config.copy(warning10sEnabled = !_uiState.value.config.warning10sEnabled)
        updateConfig(newConfig)
    }

    private fun startFromBeginning() {
        val config = _uiState.value.config
        val theme = SoundTheme.fromId(config.soundThemeId)
        if (config.prepDurationSeconds > 0) {
            _uiState.update {
                it.copy(
                    phase = TimerPhase.PREPARATION,
                    status = TimerStatus.RUNNING,
                    currentRound = 1,
                    timeRemainingSeconds = config.prepDurationSeconds,
                    totalPhaseDurationSeconds = config.prepDurationSeconds,
                    totalElapsedWorkoutSeconds = 0,
                    isWarningActive = false
                )
            }
            soundManager.playTick(config.soundEnabled)
        } else {
            _uiState.update {
                it.copy(
                    phase = TimerPhase.ROUND,
                    status = TimerStatus.RUNNING,
                    currentRound = 1,
                    timeRemainingSeconds = config.roundDurationSeconds,
                    totalPhaseDurationSeconds = config.roundDurationSeconds,
                    totalElapsedWorkoutSeconds = 0,
                    isWarningActive = false
                )
            }
            soundManager.playRoundStart(theme, config.soundEnabled, config.vibrationEnabled)
        }
        startTicker()
    }

    private fun resumeTimer() {
        _uiState.update { it.copy(status = TimerStatus.RUNNING) }
        startTicker()
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                handleTick()
            }
        }
    }

    private fun handleTick() {
        val current = _uiState.value
        if (current.status != TimerStatus.RUNNING) return

        val theme = SoundTheme.fromId(current.config.soundThemeId)

        if (current.timeRemainingSeconds > 1) {
            val newRemaining = current.timeRemainingSeconds - 1
            val newElapsed = current.totalElapsedWorkoutSeconds + 1
            val isWarning = current.phase == TimerPhase.ROUND && newRemaining <= 10 && current.config.warning10sEnabled

            if (current.phase == TimerPhase.ROUND && newRemaining == 10 && current.config.warning10sEnabled) {
                soundManager.playWarning10s(current.config.soundEnabled, current.config.vibrationEnabled)
            } else if (current.phase == TimerPhase.PREPARATION && newRemaining in 1..3) {
                soundManager.playTick(current.config.soundEnabled)
            }

            _uiState.update {
                it.copy(
                    timeRemainingSeconds = newRemaining,
                    totalElapsedWorkoutSeconds = newElapsed,
                    isWarningActive = isWarning
                )
            }
        } else {
            val newElapsed = current.totalElapsedWorkoutSeconds + 1
            _uiState.update { it.copy(totalElapsedWorkoutSeconds = newElapsed) }

            when (current.phase) {
                TimerPhase.PREPARATION -> {
                    transitionToRound(1)
                }
                TimerPhase.ROUND -> {
                    soundManager.playRoundEnd(theme, current.config.soundEnabled, current.config.vibrationEnabled)
                    if (current.currentRound < current.totalRounds) {
                        transitionToRest()
                    } else {
                        finishWorkout()
                    }
                }
                TimerPhase.REST -> {
                    transitionToRound(current.currentRound + 1)
                }
                TimerPhase.IDLE, TimerPhase.FINISHED -> {}
            }
        }
    }

    private fun transitionToRound(roundNum: Int) {
        val config = _uiState.value.config
        val theme = SoundTheme.fromId(config.soundThemeId)
        soundManager.playRoundStart(theme, config.soundEnabled, config.vibrationEnabled)
        _uiState.update {
            it.copy(
                phase = TimerPhase.ROUND,
                status = TimerStatus.RUNNING,
                currentRound = roundNum,
                timeRemainingSeconds = config.roundDurationSeconds,
                totalPhaseDurationSeconds = config.roundDurationSeconds,
                isWarningActive = false
            )
        }
    }

    private fun transitionToRest() {
        val config = _uiState.value.config
        _uiState.update {
            it.copy(
                phase = TimerPhase.REST,
                status = TimerStatus.RUNNING,
                timeRemainingSeconds = config.restDurationSeconds,
                totalPhaseDurationSeconds = config.restDurationSeconds,
                isWarningActive = false
            )
        }
    }

    private fun finishWorkout() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                phase = TimerPhase.FINISHED,
                status = TimerStatus.STOPPED,
                timeRemainingSeconds = 0,
                isWarningActive = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundManager.release()
    }
}
