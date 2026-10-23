package com.example.matteogames.core

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/** Lightweight feedback using the built-in ToneGenerator and haptics (API 19 safe). */
class AudioService(context: Context) {

    private val vibrator: Vibrator? =
        context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    private val tone: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 90)
    } catch (e: RuntimeException) {
        null
    }

    fun playTap() { safeTone(ToneGenerator.TONE_PROP_BEEP, 80); buzz(20) }
    fun playCorrect() { safeTone(ToneGenerator.TONE_PROP_ACK, 150); buzz(35) }
    fun playWin() { safeTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400); buzz(90) }

    private fun safeTone(type: Int, durationMs: Int) {
        try { tone?.startTone(type, durationMs) } catch (e: RuntimeException) { }
    }

    @Suppress("DEPRECATION")
    private fun buzz(ms: Long) {
        try {
            val v = vibrator ?: return
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(ms)
            }
        } catch (e: Exception) { }
    }
}
