package com.example.matteogames.feature.findit

import android.animation.ObjectAnimator
import android.view.View
import com.example.matteogames.R
import com.example.matteogames.core.Literki
import com.example.matteogames.framework.*

private class FindRound(val options: List<Literki.Word>, val correctIndex: Int) {
    val target get() = options[correctIndex]
}

/** Hear a Literki word (current language), tap the matching picture (one of four). */
class FindItMiniGame : MiniGame {
    override val info = MiniGameInfo("findit", R.string.game_findit, "🔎", intArrayOf(Pal.Aqua, Pal.Grass))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        fun newRound(): FindRound? {
            val ws = services.words
            if (ws.size < 4) return null
            val o = ws.shuffled().take(4)
            return FindRound(o, o.indices.random())
        }
        var round = newRound()
        return ctx.kidScreen(0xFFE6FBF3.toInt(), 0xFFEAF7EA.toInt(), onExit, right = { bar ->
            bar.add(ctx.langButton(services, 56, { round?.target?.id }) { _ -> services.voice.say(services.lang.lang to "p.find", services.lang.lang to round?.target?.id) })
        }) { content, root, _ ->
            val main = ctx.hbox()
            content.add(main)
            main.add(ctx.kidButton(100, { emoji = "🔊"; emojiFrac = 0.55f; cornerDp = 30f }) {
                services.voice.say(services.lang.lang to "p.find", services.lang.lang to round?.target?.id)
            }, 16)
            val grid = ctx.vbox()
            main.add(grid)
            val tiles = ArrayList<KidButton>()
            var busy = false
            fun show() { round?.let { r -> tiles.forEachIndexed { i, t -> t.picture = r.options[i] } } }
            for (rr in 0..1) {
                val row = ctx.hbox()
                for (c in 0..1) {
                    val index = rr * 2 + c
                    lateinit var b: KidButton
                    b = ctx.kidButton(128, { emojiFrac = 0.72f }) {
                        val r = round ?: return@kidButton
                        if (busy) return@kidButton
                        if (index == r.correctIndex) {
                            busy = true
                            services.audio.playCorrect()
                            services.praise()
                            root.postDelayed({
                                round = newRound(); show(); busy = false
                                services.voice.say(services.lang.lang to "p.find", services.lang.lang to round?.target?.id)
                            }, 1800)
                        } else {
                            ObjectAnimator.ofFloat(b, "translationX", 0f, -14f, 14f, -8f, 8f, 0f).setDuration(350).start()
                            services.voice.say(services.lang.lang to "p.try", services.lang.lang to r.target.id)
                        }
                    }
                    tiles.add(b)
                    row.add(b, 6)
                }
                grid.add(row)
            }
            show()
            services.voice.say(services.lang.lang to "p.find", services.lang.lang to round?.target?.id)
        }
    }
}
