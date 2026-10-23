package com.example.matteogames.framework

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

/** Home: title + language button + a 4x2 grid (landscape) of game cards that always fits the screen. */
fun homeView(services: GameServices, onSelect: (String) -> Unit): View = with(services.activity) {
    val version = runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull() ?: "?"
    sizedScreen(Pal.SkyTop, Pal.SkyBottom) { root, _, _ ->
        val col = vbox()
        col.setPadding(u(Ui.SIDE), u(4), u(Ui.SIDE), u(2))
        root.add(col, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val head = hbox(Gravity.CENTER_VERTICAL)
        head.add(emojiView("🌈", 50), 4)
        head.addView(fitLabel("Matteo Games", -1, 44), LinearLayout.LayoutParams(0, u(44), 1f))
        val cards = ArrayList<KidButton>()
        lateinit var lb: KidButton
        lb = langButton(services, 52, { "p.lang" }) { l ->
            services.voice.say(l to "p.lang")
            // titles follow the language
            cards.forEach { it.caption = services.text(titlePrompt(it.tag as String)); it.invalidate() }
        }
        // house: hold 1.5 s to go back to Literki (a short tap only wiggles)
        val home = HoldButton(this, 1500L) { exitToLiterki(services.activity) }
        home.arrow = Arrow.HOME
        head.addView(home, LinearLayout.LayoutParams(u(56), u(56)).apply { setMargins(u(4), 0, u(14), 0) })
        head.add(lb, 4)
        col.add(head, 0, ViewGroup.LayoutParams.MATCH_PARENT, u(60))

        val grid = vbox()
        col.addView(grid, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        MiniGameRegistry.games.chunked(4).forEach { pair ->
            val row = hbox()
            grid.addView(row, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
            pair.forEach { g ->
                val card = KidButton(this).apply {
                    tag = g.info.id
                    gradient = g.info.gradient
                    cornerDp = 24f
                    emoji = g.info.emoji
                    emojiFrac = 0.58f
                    caption = services.text(titlePrompt(g.info.id)).ifEmpty { getString(g.info.titleRes) }
                    setOnClickListener { onSelect(g.info.id); services.sayTitle(g.info.id) }
                }
                cards.add(card)
                row.addView(card, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    setMargins(u(5), u(5), u(5), u(5))
                })
            }
        }
        col.add(fitLabel("v$version - Literki - Noto Emoji (Apache 2.0)", -1, 16, 0x88000000.toInt()))
    }
}

/** Friendly full-screen message (loading, Literki missing, permission denied). */
fun messageView(ctx: Context, emoji: String, title: String, msg: String, onRetry: (() -> Unit)?): View = with(ctx) {
    sizedScreen(Pal.SkyTop, Pal.SkyBottom) { root, _, _ ->
        // landscape: picture on the left, text + retry on the right
        val row = hbox(Gravity.CENTER)
        row.setPadding(u(Ui.SIDE + 12), u(16), u(Ui.SIDE + 12), u(16))
        root.add(row, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        row.add(emojiView(emoji, 150), 10)
        val col = vbox(Gravity.CENTER)
        row.addView(col, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
        col.add(fitLabel(title, -1, 60))
        // message may be long: split into lines that each auto-fit
        wrap(msg, 30).forEach { col.add(fitLabel(it, -1, 34, Pal.Ink)) }
        if (onRetry != null) {
            col.add(spacer(14))
            col.add(circleButton(84, arrow = Arrow.RIGHT, fill = Pal.Sunshine) { onRetry() })
        }
    }
}

private fun wrap(s: String, max: Int): List<String> {
    val out = ArrayList<String>(); var cur = ""
    s.split(' ').forEach { w -> if (cur.isNotEmpty() && cur.length + 1 + w.length > max) { out.add(cur); cur = w } else cur = if (cur.isEmpty()) w else "$cur $w" }
    if (cur.isNotEmpty()) out.add(cur)
    return out
}

/** Brings Literki back to the front (launching it if it is not running) and closes MatteoGames. */
fun exitToLiterki(a: android.app.Activity) {
    val i = a.packageManager.getLaunchIntentForPackage("com.literki.app")
    if (i != null) {
        // launcher intent + NEW_TASK resumes Literki's existing task instead of recreating it
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        try { a.startActivity(i) } catch (_: Exception) {}
    }
    a.finishAffinity()
}
