package com.example.matteogames.feature.findit

import android.view.View
import android.widget.LinearLayout
import com.example.matteogames.R
import com.example.matteogames.core.EN
import com.example.matteogames.core.PL
import com.example.matteogames.core.PT
import com.example.matteogames.feature.jigsaw.PuzzleLogic
import com.example.matteogames.feature.jigsaw.SamplePicture
import com.example.matteogames.framework.*
import java.util.Locale

private class FindRound(val options: List<SamplePicture>, val correctIndex: Int, val prompt: String, val locale: Locale)

private fun newRound(): FindRound {
    val options = PuzzleLogic.samples.shuffled().take(4)
    val target = options.indices.random()
    val pic = options[target]
    val (locale, text) = listOf(EN to pic.en, PL to pic.pl, PT to pic.pt).random()
    return FindRound(options, target, text, locale)
}

/** Hear a word, tap the matching picture (one of four). */
class FindItMiniGame : MiniGame {
    override val info = MiniGameInfo("findit", R.string.game_findit, "🔎", intArrayOf(Pal.Aqua, Pal.Grass))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val (root, content) = ctx.kidScreen(0xFFE6FBF3.toInt(), 0xFFEAF7EA.toInt(), onExit)
        var round = newRound()
        content.add(ctx.kidButton(100, { emoji = "🔊"; emojiFrac = 0.55f; cornerDp = 30f }) {
            services.speech.speak(round.prompt, round.locale)
        }, 12)
        val tiles = ArrayList<KidButton>()
        for (r in 0..1) {
            val row = ctx.hbox()
            for (c in 0..1) {
                val index = r * 2 + c
                val b = ctx.kidButton(145, { emojiFrac = 0.62f }) {
                    if (index == round.correctIndex) {
                        services.audio.playCorrect()
                        round = newRound()
                        tiles.forEachIndexed { i, t -> t.emoji = round.options[i].emoji }
                        services.speech.speak(round.prompt, round.locale)
                    } else {
                        services.speech.speak(round.prompt, round.locale)
                    }
                }
                tiles.add(b)
                row.add(b, 8)
            }
            content.add(row)
        }
        tiles.forEachIndexed { i, t -> t.emoji = round.options[i].emoji }
        services.speech.speak(round.prompt, round.locale)
        return root
    }
}
