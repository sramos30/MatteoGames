package com.example.matteogames.framework

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

/** Home: title + a 2-column grid of big game cards (vertical scroll). */
fun homeView(context: Context, onSelect: (String) -> Unit): View = with(context) {
    val version = runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull() ?: "?"
    val scroll = ScrollView(this)
    scroll.background = gradientBg(Pal.SkyTop, Pal.SkyBottom)
    val col = vbox()
    col.setPadding(dp(12), dp(16), dp(12), dp(16))
    scroll.add(col, 0, ViewGroup.LayoutParams.MATCH_PARENT)

    col.add(emojiView("🌈", 64))
    col.add(label("Matteo Games", 34f, Pal.Ink))
    col.add(spacer(12))

    val games = MiniGameRegistry.games
    val screenW = resources.displayMetrics.widthPixels / resources.displayMetrics.density
    val cardW = ((screenW - 24 - 4 * 8) / 2).toInt().coerceAtMost(220)
    games.chunked(2).forEach { pair ->
        val row = hbox()
        pair.forEach { g ->
            val card = KidButton(this).apply {
                gradient = g.info.gradient
                cornerDp = 28f
                emoji = g.info.emoji
                emojiFrac = 0.5f
                caption = getString(g.info.titleRes)
                setOnClickListener { onSelect(g.info.id) }
            }
            row.addView(card, LinearLayout.LayoutParams(dp(cardW), dp(cardW * 1.15f)).apply {
                setMargins(dp(8), dp(8), dp(8), dp(8))
            })
        }
        col.add(row)
    }
    col.add(spacer(16))
    col.add(label("Matteo Games  v$version  ·  Emoji: Twemoji CC-BY 4.0", 12f, 0x66000000, bold = false).apply {
        gravity = Gravity.CENTER
    })
    scroll
}
