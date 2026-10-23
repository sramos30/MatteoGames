package com.example.matteogames.feature.memory

import android.view.View
import com.example.matteogames.R
import com.example.matteogames.core.Literki
import com.example.matteogames.framework.*

private class MCard(val pairId: Int, val word: Literki.Word) {
    var faceUp = false
    var matched = false
    lateinit var view: KidButton
    fun refresh() {
        val revealed = faceUp || matched
        view.picture = if (revealed) word else null
        view.emoji = if (revealed) null else "❓"
        view.emojiFrac = if (revealed) 0.75f else 0.45f
        view.fill = if (matched) 0xFFCDEFD6.toInt() else if (revealed) 0xFFFFFFFF.toInt() else 0xFFE9DDFF.toInt()
    }
}

/** Flip two cards to find matching pairs of Literki words. 3, 4 or 6 pairs. */
class MemoryMatchMiniGame : MiniGame {
    override val info = MiniGameInfo("memory", R.string.game_memory, "🧠", intArrayOf(Pal.Grape, Pal.Bubblegum))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        var pairs = 3
        val pairButtons = ArrayList<Pair<Int, KidButton>>()
        lateinit var deal: () -> Unit
        return ctx.kidScreen(0xFFEFE7FF.toInt(), 0xFFFFE7F3.toInt(), onExit, right = { bar ->
            listOf(3, 4, 6).forEach { p ->
                val b = ctx.kidButton(50, { label = "$p"; labelSizeFrac = 0.5f; cornerDp = 16f }) { pairs = p; deal() }
                pairButtons.add(p to b)
                bar.add(b, 4)
            }
            bar.add(ctx.flex())
            bar.add(ctx.langButton(services, 56, { null }) { l -> services.voice.say(l to "p.lang") })
        }) { content, root, hU ->
            var deck: List<MCard> = emptyList()
            var first: MCard? = null
            var busy = false
            val grid = ctx.vbox()
            content.add(grid)

            fun onTap(card: MCard) {
                if (busy || card.faceUp || card.matched) return
                card.faceUp = true; card.refresh()
                services.sayWord(card.word.id)
                val a = first
                if (a == null) { first = card; return }
                first = null
                if (a.pairId == card.pairId) {
                    a.matched = true; card.matched = true; a.refresh(); card.refresh()
                    services.audio.playCorrect()
                    if (deck.all { it.matched }) {
                        busy = true
                        root.postDelayed({ services.praise() }, 900)
                        root.postDelayed({ deal() }, 2600)
                    }
                } else {
                    busy = true
                    root.postDelayed({
                        a.faceUp = false; card.faceUp = false; a.refresh(); card.refresh(); busy = false
                    }, 1000)
                }
            }
            deal = {
                busy = false; first = null
                val picks = services.words.shuffled().take(pairs)
                deck = picks.flatMapIndexed { i, w -> listOf(MCard(i, w), MCard(i, w)) }.shuffled()
                pairButtons.forEach { (p, b) -> b.fill = if (p == pairs) Pal.Sunshine else 0xFFFFFFFF.toInt()
                    b.labelColor = if (p == pairs) 0xFFFFFFFF.toInt() else Pal.Ink }
                grid.removeAllViews()
                // pick the column count that gives the biggest cards in the measured area
                val wU = root.width / Ui.u - 2 * Ui.SIDE
                val n = deck.size
                var cols = 2; var size = 0f
                for (c in 2..n) {
                    val rows = (n + c - 1) / c
                    val s = minOf(wU / c, hU / rows) - 12f
                    if (s > size) { size = s; cols = c }
                }
                val cardU = size.coerceIn(60f, 130f).toInt()
                deck.chunked(cols).forEach { rc ->
                    val row = ctx.hbox()
                    rc.forEach { card ->
                        card.view = ctx.kidButton(cardU, { cornerDp = 18f }) { onTap(card) }
                        card.refresh()
                        row.add(card.view, 6)
                    }
                    grid.add(row)
                }
            }
            deal()
        }
    }
}
