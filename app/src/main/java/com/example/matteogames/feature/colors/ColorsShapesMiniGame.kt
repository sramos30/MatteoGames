package com.example.matteogames.feature.colors

import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.example.matteogames.R
import com.example.matteogames.framework.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private enum class ShapeKind(val id: String) { CIRCLE(Ids.CIRCLE), SQUARE(Ids.SQUARE), TRIANGLE(Ids.TRIANGLE), STAR(Ids.STAR) }

/** Color -> Literki word (c.red, c.blue, c.green, c.yellow, c.pink, c.orange). */
private val COLORS = listOf(0xFFE53E3E.toInt() to Ids.RED, 0xFF5A8DEE.toInt() to Ids.BLUE, 0xFF57C785.toInt() to Ids.GREEN,
    0xFFFFC23C.toInt() to Ids.YELLOW, 0xFFEF5DA8.toInt() to Ids.PINK, 0xFFFF8A47.toInt() to Ids.ORANGE)

private class Tile(val shape: ShapeKind, val color: Int)
private class Round(val tiles: List<Tile>, val correctIndex: Int, val nameId: String)

/** Same shape in 4 colors (name a color) or 4 shapes in one color (name a shape). */
private fun newRound(): Round {
    val k = (0..3).random()
    return if (Random.nextBoolean()) {
        val shape = ShapeKind.values().random()
        val cs = COLORS.shuffled().take(4)
        Round(cs.map { Tile(shape, it.first) }, k, cs[k].second)
    } else {
        val color = COLORS.random().first
        val ss = ShapeKind.values().toList().shuffled().take(4)
        Round(ss.map { Tile(it, color) }, k, ss[k].id)
    }
}

private val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG)

private fun drawTileShape(c: Canvas, box: RectF, tile: Tile) {
    val pad = box.width() * 0.16f
    val l = box.left + pad; val t = box.top + pad
    val w = box.width() - 2 * pad; val h = box.height() - 2 * pad
    shapePaint.color = tile.color
    when (tile.shape) {
        ShapeKind.CIRCLE -> c.drawCircle(l + w / 2, t + h / 2, min(w, h) / 2, shapePaint)
        ShapeKind.SQUARE -> c.drawRoundRect(RectF(l, t, l + w, t + h), w * 0.12f, w * 0.12f, shapePaint)
        ShapeKind.TRIANGLE -> c.drawPath(Path().apply {
            moveTo(l + w / 2f, t); lineTo(l + w, t + h); lineTo(l, t + h); close()
        }, shapePaint)
        ShapeKind.STAR -> {
            val cx = l + w / 2f; val cy = t + h / 2f
            val outer = min(w, h) / 2f; val inner = outer * 0.5f
            val p = Path()
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) outer else inner
                val a = Math.toRadians(-90.0 + i * 36.0)
                val x = cx + r * cos(a).toFloat(); val y = cy + r * sin(a).toFloat()
                if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close(); c.drawPath(p, shapePaint)
        }
    }
}

/**
 * Colors & shapes: Literki says "Find" + a color or shape name (MATTEO-IDS.json) and the child
 * taps it. The speaker button shows the name and repeats it; praise names it again.
 */
class ColorsShapesMiniGame : MiniGame {
    override val info = MiniGameInfo("colors", R.string.game_colors, "🎨", intArrayOf(Pal.Aqua, Pal.Grape))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        return ctx.kidScreen(0xFFE6FBFA.toInt(), 0xFFEFE7FF.toInt(), onExit, right = { bar ->
            bar.add(ctx.langButton(services, 56, { "p.find" }) { l -> services.voice.say(l to "p.find") })
        }) { content, root, _ ->
            var round = newRound()
            var busy = false
            val tiles = ArrayList<KidButton>()
            fun ask() = services.voice.say(services.lang.lang to "p.find", services.lang.lang to round.nameId)
            val sample = ctx.kidButton(120, { fill = 0xFFFFF3C4.toInt(); captionColor = Pal.Secondary; emoji = "🔊"; emojiFrac = 0.5f }) { ask() }
            fun bind() {
                sample.caption = services.text(round.nameId); sample.invalidate()
                tiles.forEachIndexed { i, b ->
                    val tile = round.tiles[i]
                    b.drawExtra = { c, r -> drawTileShape(c, r, tile) }
                    b.invalidate()
                }
            }
            val main = ctx.hbox()
            content.add(main)
            main.add(sample, 16)
            val grid = ctx.vbox()
            main.add(grid)
            for (r in 0..1) {
                val row = ctx.hbox()
                for (c in 0..1) {
                    val index = r * 2 + c
                    lateinit var b: KidButton
                    b = ctx.kidButton(128) {
                        if (busy) return@kidButton
                        if (index == round.correctIndex) {
                            busy = true
                            services.audio.playCorrect()
                            services.praise(round.nameId) { round = newRound(); bind(); busy = false; services.roundStart("colors"); ask() }
                        } else {
                            ObjectAnimator.ofFloat(b, "translationX", 0f, -14f, 14f, -8f, 8f, 0f).setDuration(350).start()
                            services.voice.say(services.lang.lang to "p.try", services.lang.lang to round.nameId)
                        }
                    }
                    tiles.add(b); row.add(b, 6)
                }
                grid.add(row)
            }
            bind()
            root.postDelayed({ ask() }, 1200)
        }
    }
}
