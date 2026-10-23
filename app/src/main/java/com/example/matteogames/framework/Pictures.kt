package com.example.matteogames.framework

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.LruCache
import com.caverock.androidsvg.SVG
import com.example.matteogames.core.Literki
import java.util.concurrent.Executors

/**
 * Word pictures: the Noto Emoji SVG from Literki's xx.images package (same drawings Literki
 * shows), rendered to bitmaps off the UI thread and cached. While rendering, or when no images
 * package has the word, the bundled Noto PNG for the word's emoji is used.
 */
object Pictures {
    private val cache = object : LruCache<String, Bitmap>(24 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }
    private val pending = HashSet<String>()
    private val worker = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())
    var preferLang: String? = null

    private fun source(w: Literki.Word): String? = w.images[preferLang] ?: w.images.values.firstOrNull()

    /** Cached bitmap now (SVG or bundled PNG fallback); [onReady] fires when the SVG is ready. */
    fun get(ctx: Context, w: Literki.Word, size: Int, onReady: () -> Unit): Bitmap? {
        val src = source(w) ?: return loadEmoji(ctx, w.emoji)
        val key = "$src@$size"
        cache.get(key)?.let { return it }
        synchronized(pending) {
            if (pending.add(key)) worker.execute {
                render(ctx.applicationContext, src, size)?.let { cache.put(key, it) }
                synchronized(pending) { pending.remove(key) }
                main.post(onReady)
            }
        }
        return loadEmoji(ctx, w.emoji)
    }

    /** Blocking render (puzzle source image). */
    fun getNow(ctx: Context, w: Literki.Word, size: Int): Bitmap? {
        val src = source(w) ?: return loadEmoji(ctx, w.emoji)
        val key = "$src@$size"
        return cache.get(key) ?: render(ctx, src, size)?.also { cache.put(key, it) } ?: loadEmoji(ctx, w.emoji)
    }

    private fun render(ctx: Context, src: String, size: Int): Bitmap? = try {
        val pkg = src.substringBefore('/'); val path = src.substringAfter('/')
        val svg = ctx.contentResolver.openInputStream(Literki.uri(pkg, path))!!.use { SVG.getFromInputStream(it) }
        svg.setDocumentWidth("100%"); svg.setDocumentHeight("100%")
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        svg.renderToCanvas(c, android.graphics.RectF(0f, 0f, size.toFloat(), size.toFloat()))
        bmp
    } catch (e: Throwable) {
        Log.w(Literki.TAG, "svg $src: ${e.message}"); null
    }
}
