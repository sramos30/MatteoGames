package com.example.matteogames.framework

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

/** Home: title + language button + a 2x4 grid of game cards that always fits the screen. */
fun homeView(services: GameServices, onSelect: (String) -> Unit): View = with(services.activity) {
    val version = runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull() ?: "?"
    sizedScreen(Pal.SkyTop, Pal.SkyBottom) { root, _, _ ->
        val col = vbox()
        col.setPadding(u(8), u(8), u(8), u(4))
        root.add(col, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val head = hbox(Gravity.CENTER_VERTICAL)
        head.add(emojiView("🌈", 56), 4)
        head.addView(fitLabel("Matteo Games", -1, 52), LinearLayout.LayoutParams(0, u(52), 1f))
        lateinit var lb: KidButton
        lb = langButton(services, 56, { "p.lang" }) { l -> services.voice.say(l to "p.lang") }
        head.add(lb, 4)
        col.add(head, 0, ViewGroup.LayoutParams.MATCH_PARENT, u(72))

        val grid = vbox()
        col.addView(grid, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        MiniGameRegistry.games.chunked(2).forEach { pair ->
            val row = hbox()
            grid.addView(row, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
            pair.forEach { g ->
                val card = KidButton(this).apply {
                    gradient = g.info.gradient
                    cornerDp = 24f
                    emoji = g.info.emoji
                    emojiFrac = 0.62f
                    caption = getString(g.info.titleRes)
                    setOnClickListener { onSelect(g.info.id) }
                }
                row.addView(card, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    setMargins(u(6), u(6), u(6), u(6))
                })
            }
        }
        col.add(fitLabel("v$version - Literki - Noto Emoji (Apache 2.0)", -1, 18, 0x88000000.toInt()))
    }
}

/** Friendly full-screen message (loading, Literki missing, permission denied). */
fun messageView(ctx: Context, emoji: String, title: String, msg: String, onRetry: (() -> Unit)?): View = with(ctx) {
    sizedScreen(Pal.SkyTop, Pal.SkyBottom) { root, _, _ ->
        val col = vbox(Gravity.CENTER)
        col.setPadding(u(20), u(20), u(20), u(20))
        root.add(col, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        col.add(emojiView(emoji, 150))
        col.add(fitLabel(title, -1, 64))
        // message may be long: split into lines that each auto-fit
        wrap(msg, 26).forEach { col.add(fitLabel(it, -1, 34, Pal.Ink)) }
        if (onRetry != null) {
            col.add(spacer(20))
            col.add(circleButton(96, arrow = Arrow.RIGHT, fill = Pal.Sunshine) { onRetry() })
        }
    }
}

private fun wrap(s: String, max: Int): List<String> {
    val out = ArrayList<String>(); var cur = ""
    s.split(' ').forEach { w -> if (cur.isNotEmpty() && cur.length + 1 + w.length > max) { out.add(cur); cur = w } else cur = if (cur.isEmpty()) w else "$cur $w" }
    if (cur.isNotEmpty()) out.add(cur)
    return out
}
