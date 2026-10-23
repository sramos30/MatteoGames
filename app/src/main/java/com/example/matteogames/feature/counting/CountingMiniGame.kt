package com.example.matteogames.feature.counting

import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.example.matteogames.R
import com.example.matteogames.framework.*

private class CountRound(val n: Int, val emoji: String, val choices: List<Int>)

private fun newRound(max: Int): CountRound {
    val n = (1..max).random()
    val emoji = listOf("⚽", "🍎", "⭐", "🐶", "🎈", "🐱", "🐟").random()
    val distractors = ((1..max).toList() - n).shuffled().take(2)
    return CountRound(n, emoji, (distractors + n).shuffled())
}

/** Count the pictures and tap the number (1..9: Literki has digits only up to 9). */
class CountingMiniGame : MiniGame {
    override val info = MiniGameInfo("counting", R.string.game_counting, "🔟", intArrayOf(Pal.Sunshine, Pal.Grass))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val max = (services.lang.lang?.digits?.keys?.filter { it in 1..9 }?.maxOrNull() ?: 9)
        return ctx.kidScreen(0xFFFFF6DE.toInt(), 0xFFE9F7E6.toInt(), onExit, right = { bar ->
            bar.add(ctx.langButton(services, 56, { "p.ok" }) { l -> services.voice.say(l to "p.lang") })
        }) { content, root, _ ->
            var busy = false
            val row = ctx.hbox()
            content.add(row, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val objects = ctx.vbox(Gravity.CENTER)
            row.add(objects, 0, ctx.u(300), ViewGroup.LayoutParams.MATCH_PARENT)
            row.add(android.view.View(ctx), 0, ctx.u(40), 1)
            val choicesRow = ctx.vbox(Gravity.CENTER)
            row.add(choicesRow, 0, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)

            fun render(round: CountRound) {
                objects.removeAllViews()
                (1..round.n).toList().chunked(3).forEach { items ->
                    val r = ctx.hbox()
                    items.forEach { r.add(ctx.emojiView(round.emoji, 74), 4) }
                    objects.add(r)
                }
                choicesRow.removeAllViews()
                round.choices.forEach { choice ->
                    lateinit var b: KidButton
                    b = ctx.kidButton(82, { label = "$choice"; labelSizeFrac = 0.6f; cornerDp = 28f }) {
                        if (busy) return@kidButton
                        if (choice == round.n) {
                            busy = true
                            services.audio.playCorrect()
                            services.praise("d${round.n}")
                            root.postDelayed({ render(newRound(max)); busy = false }, 2600)
                        } else {
                            ObjectAnimator.ofFloat(b, "translationX", 0f, -14f, 14f, -8f, 8f, 0f).setDuration(350).start()
                            services.tryAgain()
                        }
                    }
                    choicesRow.add(b, 6)
                }
            }
            render(newRound(max))
        }
    }
}
