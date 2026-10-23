package com.example.matteogames.feature.numbers

import android.view.View
import com.example.matteogames.R
import com.example.matteogames.core.EN
import com.example.matteogames.core.PL
import com.example.matteogames.core.PT
import com.example.matteogames.core.Word
import com.example.matteogames.core.say
import com.example.matteogames.framework.*

private val NUMBERS = listOf(
    Word("one", "jeden", "um"),
    Word("two", "dwa", "dois"),
    Word("three", "trzy", "três"),
    Word("four", "cztery", "quatro"),
    Word("five", "pięć", "cinco"),
    Word("six", "sześć", "seis"),
    Word("seven", "siedem", "sete"),
    Word("eight", "osiem", "oito"),
    Word("nine", "dziewięć", "nove"),
    Word("ten", "dziesięć", "dez"),
)

/** Big number + that many objects; spoken EN → PL → PT. Arrows wait for the audio. */
class NumbersMiniGame : MiniGame {
    override val info = MiniGameInfo("numbers", R.string.game_numbers, "🔢", intArrayOf(Pal.Grape, Pal.SkyBlue))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val (root, content) = ctx.kidScreen(0xFFEDE7FF.toInt(), 0xFFEAF2FF.toInt(), onExit)
        val objects = listOf("⚽", "⚡", "🐶", "⭐", "🍎")
        var n = services.settings.numbersCurrent.coerceIn(1, 10)

        val big = ctx.kidButton(170, { labelSizeFrac = 0.62f; cornerDp = 32f }) { services.speech.say(NUMBERS[n - 1]) }
        content.add(big)
        content.add(ctx.spacer(14))
        val objs = ctx.vbox()
        content.add(objs)
        content.add(ctx.spacer(14))
        val flags = ctx.hbox()
        flags.add(ctx.circleButton(58, "🇬🇧") { services.speech.speak(NUMBERS[n - 1].en, EN) }, 7)
        flags.add(ctx.circleButton(58, "🇵🇱") { services.speech.speak(NUMBERS[n - 1].pl, PL) }, 7)
        flags.add(ctx.circleButton(58, "🇧🇷") { services.speech.speak(NUMBERS[n - 1].pt, PT) }, 7)
        content.add(flags)
        content.add(ctx.spacer(14))
        val arrows = ctx.hbox()
        content.add(arrows)

        fun render() {
            big.label = "$n"
            objs.removeAllViews()
            val e = objects[(n - 1) % objects.size]
            (1..n).toList().chunked(5).forEach { items ->
                val row = ctx.hbox()
                items.forEach { row.add(ctx.emojiView(e, 44), 4) }
                objs.add(row)
            }
        }
        fun go(delta: Int) {
            if (services.speech.speaking) return
            val m = n + delta
            if (m !in 1..10) return
            n = m
            services.settings.numbersCurrent = n
            render()
            services.speech.say(NUMBERS[n - 1])
        }
        val prev = ctx.circleButton(76, arrow = Arrow.LEFT) { go(-1) }
        val next = ctx.circleButton(76, arrow = Arrow.RIGHT) { go(+1) }
        arrows.add(prev, 12); arrows.add(next, 12)
        services.speech.onSpeakingChanged = { s ->
            val f = if (s) Pal.Disabled else 0xFFFFFFFF.toInt()
            prev.fill = f; next.fill = f
        }
        render()
        services.speech.say(NUMBERS[n - 1])
        return root
    }
}
