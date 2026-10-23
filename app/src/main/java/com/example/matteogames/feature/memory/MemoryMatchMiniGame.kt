package com.example.matteogames.feature.memory

import android.view.View
import com.example.matteogames.R
import com.example.matteogames.core.Word
import com.example.matteogames.core.sayOne
import com.example.matteogames.feature.jigsaw.PuzzleLogic
import com.example.matteogames.framework.*

private class MCard(val pairId: Int, val emoji: String, val word: Word) {
    var faceUp = false
    var matched = false
    lateinit var view: KidButton
    fun refresh() {
        val revealed = faceUp || matched
        view.emoji = if (revealed) emoji else "❓"
        view.fill = if (matched) 0xFFCDEFD6.toInt() else 0xFFFFFFFF.toInt()
    }
}

private fun newDeck(pairs: Int): List<MCard> {
    val picks = PuzzleLogic.samples.shuffled().take(pairs)
    val cards = ArrayList<MCard>(pairs * 2)
    picks.forEachIndexed { i, s ->
        val w = Word(s.en, s.pl, s.pt)
        cards.add(MCard(i, s.emoji, w)); cards.add(MCard(i, s.emoji, w))
    }
    return cards.shuffled()
}

/** Flip two cards to find matching pairs. 3, 4 or 6 pairs. */
class MemoryMatchMiniGame : MiniGame {
    override val info = MiniGameInfo("memory", R.string.game_memory, "🧠", intArrayOf(Pal.Grape, Pal.Bubblegum))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val (root, content) = ctx.kidScreen(0xFFEFE7FF.toInt(), 0xFFFFE7F3.toInt(), onExit)
        var pairs = 3
        var deck: List<MCard> = emptyList()
        var first: MCard? = null
        var busy = false
        val pairRow = ctx.hbox()
        content.add(pairRow)
        content.add(ctx.spacer(12))
        val grid = ctx.vbox()
        content.add(grid)
        val pairButtons = ArrayList<Pair<Int, KidButton>>()

        lateinit var deal: () -> Unit
        fun onTap(card: MCard) {
            if (busy || card.faceUp || card.matched) return
            card.faceUp = true; card.refresh()
            services.speech.sayOne(card.word, rate = 1.1f)
            val a = first
            if (a == null) { first = card; return }
            first = null
            if (a.pairId == card.pairId) {
                a.matched = true; card.matched = true; a.refresh(); card.refresh()
                services.audio.playCorrect()
                if (deck.all { it.matched }) { busy = true; root.postDelayed({ deal() }, 1000) }
            } else {
                busy = true
                root.postDelayed({
                    a.faceUp = false; card.faceUp = false; a.refresh(); card.refresh(); busy = false
                }, 800)
            }
        }
        deal = {
            busy = false; first = null
            deck = newDeck(pairs)
            pairButtons.forEach { (p, b) -> b.fill = if (p == pairs) Pal.Sunshine else 0xFFFFFFFF.toInt()
                b.labelColor = if (p == pairs) 0xFFFFFFFF.toInt() else Pal.Ink }
            grid.removeAllViews()
            val columns = if (deck.size <= 6) 3 else 4
            val size = if (deck.size <= 6) 96 else 76
            deck.chunked(columns).forEach { rc ->
                val row = ctx.hbox()
                rc.forEach { card ->
                    card.view = ctx.kidButton(size, { emojiFrac = 0.6f }) { onTap(card) }
                    card.refresh()
                    row.add(card.view, 6)
                }
                grid.add(row)
            }
        }
        listOf(3, 4, 6).forEach { p ->
            val b = ctx.kidButton(64, { label = "$p"; labelSizeFrac = 0.5f }) { pairs = p; deal() }
            pairButtons.add(p to b)
            pairRow.add(b, 6)
        }
        deal()
        return root
    }
}
