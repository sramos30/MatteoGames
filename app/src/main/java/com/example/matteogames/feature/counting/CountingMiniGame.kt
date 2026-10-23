package com.example.matteogames.feature.counting

import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.example.matteogames.R
import com.example.matteogames.framework.*

private class CountRound(val n: Int, val emoji: String, val choices: List<Int>)

/** Each number 1..max once per bag, so 10 comes up at least once every 10 rounds. */
private val bag = ArrayDeque<Int>()
/** Test hook: `am start ... --ei mg_count 10` forces the first round. */
var forcedCount = 0

private fun newRound(max: Int): CountRound {
    if (bag.isEmpty() || bag.any { it > max }) { bag.clear(); bag.addAll((1..max).shuffled()) }
    val n = if (forcedCount in 1..max) forcedCount.also { forcedCount = 0 } else bag.removeFirst()
    val emoji = listOf("⚽", "🍎", "⭐", "🐶", "🎈", "🐱", "🐟").random()
    val distractors = ((1..max).toList() - n).shuffled().take(2)
    return CountRound(n, emoji, (distractors + n).shuffled())
}

/** Count the pictures and tap the number (1..10; 10 is Literki word n.10 = w143). */
class CountingMiniGame : MiniGame {
    override val info = MiniGameInfo("counting", R.string.game_counting, "🔟", intArrayOf(Pal.Sunshine, Pal.Grass))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val max = 10
        // 10 has no digit: its word w143 (shown as "10" even where the language lacks the word text)
        fun numId(n: Int) = if (n == 10) Ids.TEN else services.lang.lang?.digits?.get(n)?.id ?: "d$n"
        forcedCount = services.activity.intent?.getIntExtra("mg_count", 0) ?: 0
        services.activity.intent?.removeExtra("mg_count")
        fun ask() = services.voice.say(services.lang.lang to "p.howmany")
        return ctx.kidScreen(0xFFFFF6DE.toInt(), 0xFFE9F7E6.toInt(), onExit, right = { bar ->
            bar.add(ctx.langButton(services, 56, { "p.howmany" }) { l -> services.voice.say(l to "p.howmany") })
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
                android.util.Log.i("MatteoFlow", "counting round n=${round.n} choices=${round.choices}")
                objects.removeAllViews()
                (1..round.n).toList().chunked(if (round.n > 9) 4 else 3).forEach { items ->
                    val r = ctx.hbox()
                    items.forEach { r.add(ctx.emojiView(round.emoji, if (round.n > 9) 60 else 74), 4) }
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
                            services.praise(numId(round.n)) { render(newRound(max)); busy = false; services.roundStart("counting"); ask() }
                        } else {
                            ObjectAnimator.ofFloat(b, "translationX", 0f, -14f, 14f, -8f, 8f, 0f).setDuration(350).start()
                            services.tryAgain()
                        }
                    }
                    choicesRow.add(b, 6)
                }
            }
            render(newRound(max))
            root.postDelayed({ ask() }, 1200)
        }
    }
}
