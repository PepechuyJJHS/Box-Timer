package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.R
import com.example.model.SoundTheme

class BoxingSoundManager(private val context: Context) {

    private val soundPool: SoundPool
    private val loadedSounds = mutableMapOf<Int, Int>() // resId -> soundPoolId
    private val readySoundPoolIds = mutableSetOf<Int>()

    private var warningClapId: Int = 0
    private var isWarningLoaded = false

    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator?
    private val vibratorManager: VibratorManager?

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                readySoundPoolIds.add(sampleId)
                if (sampleId == warningClapId) {
                    isWarningLoaded = true
                }
            }
        }

        // Preload all sound themes
        preloadAllThemes()

        try {
            warningClapId = soundPool.load(context, R.raw.warning_clap, 1)
        } catch (e: Exception) {
            Log.e("BoxingSoundManager", "Error loading warning clap", e)
        }

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            Log.e("BoxingSoundManager", "Error creating ToneGenerator", e)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibrator = vibratorManager?.defaultVibrator
        } else {
            vibratorManager = null
            @Suppress("DEPRECATION")
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private fun preloadAllThemes() {
        for (theme in SoundTheme.entries) {
            loadResIfNeeded(theme.startResId)
            loadResIfNeeded(theme.endResId)
        }
    }

    private fun loadResIfNeeded(resId: Int): Int {
        return loadedSounds.getOrPut(resId) {
            try {
                soundPool.load(context, resId, 1)
            } catch (e: Exception) {
                Log.e("BoxingSoundManager", "Failed to load res $resId", e)
                0
            }
        }
    }

    fun playRoundStart(theme: SoundTheme, soundEnabled: Boolean, vibrateEnabled: Boolean) {
        if (soundEnabled) {
            playRawRes(theme.startResId, fallbackTone = ToneGenerator.TONE_PROP_BEEP2)
        }
        if (vibrateEnabled) {
            vibratePattern(longArrayOf(0, 300, 150, 400), intArrayOf(0, 255, 0, 255))
        }
    }

    fun playRoundEnd(theme: SoundTheme, soundEnabled: Boolean, vibrateEnabled: Boolean) {
        if (soundEnabled) {
            playRawRes(theme.endResId, fallbackTone = ToneGenerator.TONE_PROP_PROMPT)
        }
        if (vibrateEnabled) {
            vibratePattern(longArrayOf(0, 300, 150, 300, 150, 500), intArrayOf(0, 255, 0, 255, 0, 255))
        }
    }

    fun previewSoundTheme(theme: SoundTheme) {
        playRawRes(theme.startResId, fallbackTone = ToneGenerator.TONE_PROP_BEEP2)
        vibratePattern(longArrayOf(0, 150, 100, 200), intArrayOf(0, 200, 0, 255))
    }

    private fun playRawRes(resId: Int, fallbackTone: Int) {
        val soundId = loadedSounds[resId] ?: loadResIfNeeded(resId)
        if (soundId != 0 && readySoundPoolIds.contains(soundId)) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        } else {
            toneGenerator?.startTone(fallbackTone, 500)
        }
    }

    fun playWarning10s(soundEnabled: Boolean, vibrateEnabled: Boolean) {
        if (soundEnabled) {
            if (isWarningLoaded && warningClapId != 0) {
                soundPool.play(warningClapId, 1f, 1f, 1, 0, 1f)
            } else {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 300)
            }
        }
        if (vibrateEnabled) {
            vibratePattern(longArrayOf(0, 80, 80, 80, 80, 100), intArrayOf(0, 180, 0, 180, 0, 220))
        }
    }

    fun playTick(soundEnabled: Boolean) {
        if (soundEnabled) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        }
    }

    private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (e: Exception) {
            Log.e("BoxingSoundManager", "Vibration failed", e)
        }
    }

    fun release() {
        try {
            soundPool.release()
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("BoxingSoundManager", "Error releasing resources", e)
        }
    }
}
