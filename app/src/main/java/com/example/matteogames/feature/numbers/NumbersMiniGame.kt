package com.example.matteogames.feature.numbers

import android.view.View
import android.view.ViewGroup
import com.example.matteogames.R
import com.example.matteogames.framework.*

/**
 * Big digit + that many objects, spoken from Literki's digits (d0..d9, "digitos.nome").
 * Literki has no 10, so the game goes 0..9. Arrows wait for the audio.
 */
class NumbersMiniGame : MiniGame {
    override val info = MiniGameInfo("numbers", R.string.game_numbers, "🔢", intArrayOf(Pal.Grape, Pal.SkyBlue))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val objects = listOf("⚽", "⚡", "🐶", "⭐", "🍎")
        val digits = (services.lang.lang?.digits?.keys ?: (0..9).toSet()).filter { it in 0..9 }.sorted().ifEmpty { listOf(0) }
        var n = services.settings.numbersCurrent.let { if (it in digits) it else digits.first() }
        fun id() = "d$n"
        lateinit var name: KidButton; lateinit var big: KidButton
        fun refreshName() { name.label = services.text(id()) }
        return ctx.kidScreen(0xFFEDE7FF.toInt(), 0xFFEAF2FF.toInt(), onExit, right = { bar ->
            bar.add(ctx.langButton(services, 56, { id() }) { l -> refreshName(); services.voice.say(l to id()) })
        }) { content, _, _ ->
            val row = ctx.hbox()
            content.add(row, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val left = ctx.vbox(android.view.Gravity.CENTER)
            row.add(left, 0, ctx.u(230), ViewGroup.LayoutParams.MATCH_PARENT)
            big = ctx.kidButton(150, { labelSizeFrac = 0.62f; cornerDp = 32f }) { services.sayWord(id()) }
            left.add(big)
            name = ctx.fitLabel("", 220, 48)
            left.add(name)
            val right = ctx.vbox(android.view.Gravity.CENTER)
            row.add(right, 0, ctx.u(300), ViewGroup.LayoutParams.MATCH_PARENT)
            val objs = ctx.vbox(android.view.Gravity.CENTER)
            right.add(objs, 0, ViewGroup.LayoutParams.MATCH_PARENT, ctx.u(150))
            val arrows = ctx.hbox()
            right.add(arrows, 6)

            fun render() {
                big.label = "$n"
                refreshName()
                objs.removeAllViews()
                val e = objects[(n + objects.size - 1) % objects.size]
                (1..n).toList().chunked(5).forEach { items ->
                    val r = ctx.hbox()
                    items.forEach { r.add(ctx.emojiView(e, 46), 4) }
                    objs.add(r)
                }
            }
            fun go(delta: Int) {
                if (services.voice.speaking) return
                val i = digits.indexOf(n) + delta
                if (i !in digits.indices) return
                n = digits[i]
                services.settings.numbersCurrent = n
                render()
                services.sayWord(id())
            }
            val prev = ctx.circleButton(80, arrow = Arrow.LEFT) { go(-1) }
            val next = ctx.circleButton(80, arrow = Arrow.RIGHT) { go(+1) }
            arrows.add(prev, 14); arrows.add(next, 14)
            services.voice.onSpeakingChanged = { s ->
                val f = if (s) Pal.Disabled else 0xFFFFFFFF.toInt()
                prev.fill = f; next.fill = f
            }
            render()
            services.sayWord(id())
        }
    }
}
