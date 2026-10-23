package com.example.matteogames.core

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log

/**
 * Plays Literki's voices: the mp3 of an item from xx.rec (approved recording) or xx.tts,
 * through the provider with MediaPlayer + ParcelFileDescriptor. Android TTS is only a last
 * resort when the language has no audio for the item, and nothing ever waits on it: if TTS
 * isn't ready, the item is just skipped.
 */
class Voice(context: Context) {
    private val ctx = context.applicationContext
    private val main = Handler(Looper.getMainLooper())
    private var player: MediaPlayer? = null
    private var queue: List<Pair<Literki.Lang, String>> = emptyList()
    private var gen = 0
    private var startedAt = 0L
    var onSpeakingChanged: ((Boolean) -> Unit)? = null

    var speaking = false
        private set(v) { if (field != v) { field = v; onSpeakingChanged?.invoke(v) } }
        get() { if (field && System.currentTimeMillis() - startedAt > 10_000) field = false; return field }

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private fun ensureTts() {
        if (tts != null) return
        tts = try { TextToSpeech(ctx) { s -> ttsReady = s == TextToSpeech.SUCCESS } } catch (e: Exception) { null }
    }

    /** Play items in order (each: language + item id such as cA, d3, w003, p.ok). Interrupts. */
    fun say(vararg items: Pair<Literki.Lang?, String?>) {
        stop()
        queue = items.mapNotNull { (l, id) -> if (l != null && id != null) l to id else null }
        val g = ++gen
        startedAt = System.currentTimeMillis()
        speaking = queue.isNotEmpty()
        next(g, 0)
    }

    private fun next(g: Int, i: Int) {
        if (g != gen) return
        if (i >= queue.size) { speaking = false; return }
        val (lang, id) = queue[i]
        startedAt = System.currentTimeMillis()
        val snd = lang.sounds[id]
        if (snd != null && playMp3(g, i, snd, lang.id, id)) return
        // last resort: TTS, never blocking
        val text = lang.text(id)
        val t = tts
        if (text != null && t != null && ttsReady) {
            try {
                t.language = lang.locale
                @Suppress("DEPRECATION") t.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                Log.i(Literki.TAG, "TTS fallback ${lang.id}/$id")
            } catch (e: Exception) { }
            main.postDelayed({ next(g, i + 1) }, 250L + text.length * 90L)
        } else {
            ensureTts()
            next(g, i + 1)
        }
    }

    private fun playMp3(g: Int, i: Int, snd: Literki.Sound, langId: String, id: String): Boolean {
        return try {
            val pfd = ctx.contentResolver.openFileDescriptor(Literki.uri(snd.pkg, snd.path), "r") ?: return false
            val mp = MediaPlayer()
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mp.setDataSource(pfd.fileDescriptor)
            mp.setOnPreparedListener {
                try { pfd.close() } catch (e: Exception) { }
                if (g != gen) { it.release(); return@setOnPreparedListener }
                it.start()
                Log.i(Literki.TAG, "MediaPlayer start $langId/$id <- ${snd.pkg}/${snd.path}")
            }
            mp.setOnCompletionListener { it.release(); if (player === it) player = null; next(g, i + 1) }
            mp.setOnErrorListener { m, _, _ -> m.release(); if (player === m) player = null; next(g, i + 1); true }
            player = mp
            mp.prepareAsync()
            true
        } catch (e: Exception) {
            Log.w(Literki.TAG, "mp3 ${snd.pkg}/${snd.path}: ${e.message}")
            false
        }
    }

    fun stop() {
        gen++
        player?.let { try { it.stop() } catch (e: Exception) { }; it.release() }
        player = null
        try { tts?.stop() } catch (e: Exception) { }
        speaking = false
    }

    fun shutdown() {
        stop()
        try { tts?.shutdown() } catch (e: Exception) { }
        tts = null
    }
}
