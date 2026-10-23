package com.example.matteogames.framework

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Bundled Noto Emoji pictures (Apache 2.0), downloaded at build time into assets/noto/ by
 * scripts/fetch_noto.py. Same strategy as Literki: FireOS 4.4 has no color emoji font (glyphs
 * show up blank), so NOTHING in the UI is drawn as an emoji character; every symbol is a bitmap.
 */
fun notoKey(emoji: String): String {
    val sb = StringBuilder()
    var i = 0
    while (i < emoji.length) {
        val cp = emoji.codePointAt(i)
        i += Character.charCount(cp)
        if (cp == 0xFE0F) continue
        if (sb.isNotEmpty()) sb.append('_')
        sb.append(String.format("%04x", cp))
    }
    return sb.toString()
}

private class Opt(val bitmap: Bitmap?)
private val cache = ConcurrentHashMap<String, Opt>()

/** The bundled Noto bitmap for [emoji], or null if it isn't bundled (then nothing is drawn). */
fun loadEmoji(context: Context, emoji: String): Bitmap? {
    if (emoji.isEmpty()) return null
    val key = notoKey(emoji)
    cache[key]?.let { return it.bitmap }
    val bmp = try {
        context.assets.open("noto/emoji_u$key.png").use { BitmapFactory.decodeStream(it) }
    } catch (e: Exception) {
        null
    }
    cache[key] = Opt(bmp)
    return bmp
}
