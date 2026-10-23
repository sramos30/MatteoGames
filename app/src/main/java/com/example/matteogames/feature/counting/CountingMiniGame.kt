package com.example.matteogames.feature.counting

import android.view.View
import com.example.matteogames.R
import com.example.matteogames.core.Word
import com.example.matteogames.core.sayOne
import com.example.matteogames.framework.*

private val NUMBER_WORDS = listOf(
    Word("one", "jeden", "um"),
    Word("two", "dwa", "dois"),
    Word("three", "trzy", "três"),
    Word("four", "cztery", "quatro"),
    Word("five", "pięć", "cinco"),
)

private class CountRound(val n: Int, val emoji: String, val choices: List<Int>)

private fun newRound(): CountRound {
    val n = (1..5).random()
    val emoji = listOf("⚽", "🍎", "⭐", "🐶", "🎈", "🐱", "🐟").random()
    val distractors = ((1..5).toList() - n).shuffled().take(2)
    return CountRound(n, emoji, (distractors + n).shuffled())
}

/** How many? Count the pictures and tap the number. */
class CountingMiniGame : MiniGame {
    override val info = MiniGameInfo("counting", R.string.game_counting, "🔟", intArrayOf(Pal.Sunshine, Pal.Grass))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val (root, content) = ctx.kidScreen(0xFFFFF6DE.toInt(), 0xFFE9F7E6.toInt(), onExit)
        var busy = false
        content.add(ctx.label("How many?", 32f))
        content.add(ctx.spacer(16))
        val objects = ctx.vbox()
        content.add(objects)
        content.add(ctx.spacer(28))
        val choicesRow = ctx.hbox()
        content.add(choicesRow)

        fun render(round: CountRound) {
            objects.removeAllViews()
            (1..round.n).toList().chunked(3).forEach { items ->
                val row = ctx.hbox()
                items.forEach { row.add(ctx.emojiView(round.emoji, 60), 5) }
                objects.add(row)
            }
            choicesRow.removeAllViews()
            round.choices.forEach { choice ->
                choicesRow.add(ctx.kidButton(96, { label = "$choice"; labelSizeFrac = 0.6f; cornerDp = 28f }) {
                    if (!busy && choice == round.n) {
                        busy = true
                        services.audio.playCorrect()
                        services.speech.sayOne(NUMBER_WORDS[round.n - 1])
                        root.postDelayed({ render(newRound()); busy = false }, 1400)
                    }
                }, 8)
            }
        }
        render(newRound())
        return root
    }
}
