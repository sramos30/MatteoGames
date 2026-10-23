package com.example.matteogames.framework

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Bundled Twemoji images (CC-BY 4.0), downloaded at build time into assets/twemoji/.
 * Android 4.4 has no color emoji font, so every picture in the app goes through here.
 */
fun twemojiKey(emoji: String): String {
    val sb = StringBuilder()
    var i = 0
    while (i < emoji.length) {
        val cp = emoji.codePointAt(i)
        i += Character.charCount(cp)
        if (cp == 0xFE0F) continue
        if (sb.isNotEmpty()) sb.append('-')
        sb.append(Integer.toHexString(cp))
    }
    return sb.toString()
}

private class Opt(val bitmap: Bitmap?)
private val twCache = ConcurrentHashMap<String, Opt>()

/** Loads the bundled Twemoji bitmap for [emoji], or null if it isn't bundled. */
fun loadTwemoji(context: Context, emoji: String): Bitmap? {
    if (emoji.isEmpty()) return null
    val key = twemojiKey(emoji)
    twCache[key]?.let { return it.bitmap }
    val bmp = try {
        context.assets.open("twemoji/$key.png").use { BitmapFactory.decodeStream(it) }
    } catch (e: Exception) {
        null
    }
    twCache[key] = Opt(bmp)
    return bmp
}
