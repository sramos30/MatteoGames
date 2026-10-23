package com.example.matteogames.core

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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

    private var onDone: (() -> Unit)? = null
    /** A protected sequence (praise) can't be cut by an ordinary say(); only by another protected one or stop(). */
    private var protectedSeq = false
    private var step = -1   // item currently playing; each item advances only once
    private val timeout = Runnable { val g = gen; val i = step; Log.w(Literki.TAG, "voice timeout item $i"); advance(g, i) }

    /**
     * Play items in order (each: language + item id such as cA, d3, w003, p.ok). Interrupts, except a
     * running protected sequence. [onDone] runs on the main thread once the whole sequence has played
     * (or failed / timed out), never when it was cut by stop() or another say().
     */
    fun say(vararg items: Pair<Literki.Lang?, String?>, protect: Boolean = false, onDone: (() -> Unit)? = null) {
        if (protectedSeq && !protect && speaking) { Log.i(Literki.TAG, "voice: kept praise, dropped ${items.map { it.second }}"); return }
        stop()
        queue = items.mapNotNull { (l, id) -> if (l != null && id != null) l to id else null }
        val g = ++gen
        this.onDone = onDone
        protectedSeq = protect
        startedAt = System.currentTimeMillis()
        speaking = queue.isNotEmpty()
        next(g, 0)
    }

    private fun advance(g: Int, i: Int) {
        if (g != gen || i != step) return
        step = -1
        main.removeCallbacks(timeout)
        next(g, i + 1)
    }

    private fun armTimeout(ms: Long) { main.removeCallbacks(timeout); main.postDelayed(timeout, ms) }

    private fun next(g: Int, i: Int) {
        if (g != gen) return
        if (i >= queue.size) {
            speaking = false; protectedSeq = false
            val d = onDone; onDone = null
            if (d != null) { Log.i(Literki.TAG, "voice done ${queue.map { it.second }}"); main.post(d) }
            return
        }
        val (lang, id) = queue[i]
        step = i
        startedAt = System.currentTimeMillis()
        armTimeout(4000)   // until we know the real length
        val snd = lang.sounds[id]
        if (snd != null && playMp3(g, i, snd, lang.id, id)) return
        // last resort: TTS (done via UtteranceProgressListener, or the timeout)
        val text = lang.text(id)
        val t = tts
        if (text != null && t != null && ttsReady) {
            try {
                t.language = lang.locale
                t.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(u: String?) {}
                    override fun onDone(u: String?) { main.post { if (u == "$g:$i") advance(g, i) } }
                    @Deprecated("Deprecated in Java") override fun onError(u: String?) { main.post { if (u == "$g:$i") advance(g, i) } }
                })
                @Suppress("DEPRECATION") t.speak(text, TextToSpeech.QUEUE_FLUSH, hashMapOf(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID to "$g:$i"))
                Log.i(Literki.TAG, "TTS fallback ${lang.id}/$id")
                armTimeout(minOf(4000L, 1000L + text.length * 120L))
            } catch (e: Exception) { advance(g, i) }
        } else {
            ensureTts()
            advance(g, i)
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
                if (g == gen && step == i) armTimeout((if (it.duration > 0) it.duration.toLong() else 3000L) + 1000L)
                Log.i(Literki.TAG, "MediaPlayer start $langId/$id <- ${snd.pkg}/${snd.path}")
            }
            mp.setOnCompletionListener { m -> main.post { m.release(); if (player === m) player = null; advance(g, i) } }
            mp.setOnErrorListener { m, _, _ -> main.post { m.release(); if (player === m) player = null; advance(g, i) }; true }
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
        main.removeCallbacks(timeout); step = -1; onDone = null; protectedSeq = false
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
