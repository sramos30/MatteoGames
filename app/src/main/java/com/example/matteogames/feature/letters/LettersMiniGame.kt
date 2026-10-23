package com.example.matteogames.feature.letters

import android.view.View
import android.view.ViewGroup
import com.example.matteogames.R
import com.example.matteogames.core.Literki
import com.example.matteogames.framework.*

/** Big letter from the language's Literki alphabet + its example word (picture + text + voice). */
class LettersMiniGame : MiniGame {
    override val info = MiniGameInfo("letters", R.string.game_letters, "🔤", intArrayOf(Pal.Tangerine, Pal.Coral))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        fun letters() = services.lang.lang?.letters ?: emptyList()
        var pos = letters().indexOfFirst { it.id == services.settings.lettersPos }.coerceAtLeast(0)
        fun cur(): Literki.Letter? = letters().getOrNull(pos)
        lateinit var refresh: (Boolean) -> Unit
        return ctx.kidScreen(0xFFFFF0E4.toInt(), 0xFFFFE7EC.toInt(), onExit, right = { bar ->
            bar.add(ctx.langButton(services, 56, { cur()?.id }) { _ ->
                val id = services.settings.lettersPos
                pos = letters().indexOfFirst { it.id == id }.coerceAtLeast(0)
                refresh(true)
            })
        }) { content, _, _ ->
            val row = ctx.hbox()
            content.add(row, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val big = ctx.kidButton(150, { labelSizeFrac = 0.5f; cornerDp = 32f }) { refresh(true) }
            val ex = ctx.vbox(android.view.Gravity.CENTER)
            val pic = ctx.emojiView(null, 130)
            ex.add(pic)
            val word = ctx.fitLabel("", 240, 48)
            ex.add(word)
            refresh = { speak ->
                val lang = services.lang.lang
                val l = cur()
                if (lang != null && l != null) {
                    services.settings.lettersPos = l.id
                    big.label = if (l.lower.isNotEmpty() && l.lower != l.upper) "${l.upper}${l.lower}" else l.upper
                    val ex = lang.exampleFor(l)
                    pic.picture = Literki.words.firstOrNull { it.id == ex }
                    word.label = ex?.let { lang.text(it) } ?: ""
                    if (speak) services.voice.say(lang to l.id, lang to ex)
                }
            }
            row.add(ctx.circleButton(76, arrow = Arrow.LEFT) {
                val n = letters().size; if (n > 0) { pos = (pos - 1 + n) % n; refresh(true) }
            }, 10)
            row.add(big, 10)
            row.add(ex, 0, ctx.u(250), ViewGroup.LayoutParams.WRAP_CONTENT)
            row.add(ctx.circleButton(76, arrow = Arrow.RIGHT) {
                val n = letters().size; if (n > 0) { pos = (pos + 1) % n; refresh(true) }
            }, 10)
            refresh(true)
        }
    }
}
