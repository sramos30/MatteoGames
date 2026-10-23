package com.example.matteogames.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * Thin wrapper over Android [TextToSpeech] using API-19 compatible calls. Speaks a short
 * sequence across languages by switching the engine's locale between utterances.
 * If a language's voice is missing on the device, that utterance is skipped quietly.
 */
class SpeechService(context: Context) {

    data class Utterance(val text: String, val locale: Locale, val rate: Float = 0.82f)

    private val main = Handler(Looper.getMainLooper())
    private var ready = false
    private val queue = java.util.ArrayDeque<Utterance>()
    private var pending: List<Utterance>? = null
    private var counter = 0

    /** True while a word/sequence is being spoken. */
    private var speakingFlag = false
    private var speakingSince = 0L
    /** Treated as finished after 10 s, so a silent/broken TTS engine never locks the UI. */
    val speaking: Boolean
        get() = speakingFlag && android.os.SystemClock.uptimeMillis() - speakingSince < 10_000
    /** Called on the main thread whenever [speaking] changes. */
    var onSpeakingChanged: ((Boolean) -> Unit)? = null

    private fun setSpeaking(v: Boolean) {
        if (v) speakingSince = android.os.SystemClock.uptimeMillis()
        if (speakingFlag == v) return
        speakingFlag = v
        onSpeakingChanged?.invoke(v)
    }

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        main.post { onInit(status) }
    }

    private fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (!ready) { setSpeaking(false); return }
        tts.setPitch(1.05f)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { main.post { playNext() } }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { main.post { playNext() } }
        })
        pending?.let { startSequence(it) }
        pending = null
    }

    fun speak(text: String, locale: Locale, rate: Float = 0.82f) =
        speakSequence(listOf(Utterance(text, locale, rate)))

    fun speakSequence(items: List<Utterance>) {
        if (items.isEmpty()) return
        setSpeaking(true)
        if (!ready) { pending = items; return }
        startSequence(items)
    }

    private fun startSequence(items: List<Utterance>) {
        queue.clear()
        queue.addAll(items)
        try { tts.stop() } catch (e: Exception) { }
        playNext()
    }

    private fun playNext() {
        val item = queue.poll()
        if (item == null) { setSpeaking(false); return }
        val res = try { tts.setLanguage(item.locale) } catch (e: Exception) { TextToSpeech.LANG_NOT_SUPPORTED }
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            playNext(); return
        }
        tts.setSpeechRate(item.rate)
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "u${counter++}"
        @Suppress("DEPRECATION")
        val r = tts.speak(item.text, TextToSpeech.QUEUE_FLUSH, params)
        if (r != TextToSpeech.SUCCESS) main.post { playNext() }
    }

    fun stop() {
        queue.clear()
        pending = null
        setSpeaking(false)
        if (ready) try { tts.stop() } catch (e: Exception) { }
    }

    fun shutdown() {
        try { tts.stop(); tts.shutdown() } catch (e: Exception) { }
    }
}
