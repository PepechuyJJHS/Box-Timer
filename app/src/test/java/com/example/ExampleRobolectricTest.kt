package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BoxingPreset
import com.example.model.SoundTheme
import com.example.model.TimerConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Box Timer", appName)
    }

    @Test
    fun `verify timer presets and default config`() {
        val config = TimerConfig()
        assertEquals(180, config.roundDurationSeconds) // 3 min
        assertEquals(60, config.restDurationSeconds)   // 1 min
        assertEquals("bell_high", config.soundThemeId)
        assertTrue(config.soundEnabled)
        assertTrue(config.vibrationEnabled)
        assertTrue(config.keepScreenOn)

        val proPreset = BoxingPreset.PRESETS.first { it.id == "pro" }
        assertEquals(180, proPreset.roundDurationSeconds)
        assertEquals(60, proPreset.restDurationSeconds)
        assertEquals(12, proPreset.totalRounds)
    }

    @Test
    fun `verify sound themes and acute bell`() {
        assertEquals(6, SoundTheme.entries.size)
        assertNotNull(SoundTheme.fromId("bell_high"))
        assertNotNull(SoundTheme.fromId("bell"))
        assertNotNull(SoundTheme.fromId("horn"))
        assertNotNull(SoundTheme.fromId("gong"))
        assertNotNull(SoundTheme.fromId("buzzer"))
        assertNotNull(SoundTheme.fromId("whistle"))
    }

    @Test
    fun `verify modifying quantity of rounds`() {
        val config = TimerConfig(totalRounds = 5)
        assertEquals(5, config.totalRounds)
        val modified = config.copy(totalRounds = 12)
        assertEquals(12, modified.totalRounds)
    }
}
