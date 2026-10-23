package com.example.matteogames.feature.colors

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.example.matteogames.R
import com.example.matteogames.core.EN
import com.example.matteogames.core.PL
import com.example.matteogames.core.PT
import com.example.matteogames.core.Word
import com.example.matteogames.framework.*
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private enum class ShapeKind { CIRCLE, SQUARE, TRIANGLE, STAR }

private class ColorOpt(val color: Int, val word: Word)

private val COLORS = listOf(
    ColorOpt(0xFFE53E3E.toInt(), Word("red", "czerwony", "vermelho")),
    ColorOpt(0xFF5A8DEE.toInt(), Word("blue", "niebieski", "azul")),
    ColorOpt(0xFF57C785.toInt(), Word("green", "zielony", "verde")),
    ColorOpt(0xFFFFC23C.toInt(), Word("yellow", "żółty", "amarelo")),
    ColorOpt(0xFFEF5DA8.toInt(), Word("pink", "różowy", "rosa")),
    ColorOpt(0xFFFF8A47.toInt(), Word("orange", "pomarańczowy", "laranja")),
)

private val SHAPE_WORDS = mapOf(
    ShapeKind.CIRCLE to Word("circle", "koło", "círculo"),
    ShapeKind.SQUARE to Word("square", "kwadrat", "quadrado"),
    ShapeKind.TRIANGLE to Word("triangle", "trójkąt", "triângulo"),
    ShapeKind.STAR to Word("star", "gwiazda", "estrela"),
)

private class Tile(val shape: ShapeKind, val color: Int)
private class Round(val tiles: List<Tile>, val correctIndex: Int, val promptText: String, val promptLocale: Locale)

private fun roundFor(tiles: List<Tile>, target: Int, prompt: Word): Round {
    val (locale, text) = listOf(EN to prompt.en, PL to prompt.pl, PT to prompt.pt).random()
    return Round(tiles, target, text, locale)
}

private fun newRound(): Round {
    return if (Random.nextBoolean()) {
        val cols = COLORS.shuffled().take(4)
        val shapes = ShapeKind.values().toList().shuffled()
        val tiles = cols.mapIndexed { idx, c -> Tile(shapes[idx % shapes.size], c.color) }
        val target = (0..3).random()
        roundFor(tiles, target, cols[target].word)
    } else {
        val shapes = ShapeKind.values().toList().shuffled().take(4)
        val tiles = shapes.map { Tile(it, COLORS.random().color) }
        val target = (0..3).random()
        roundFor(tiles, target, SHAPE_WORDS.getValue(shapes[target]))
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

/** Hear a color or shape name, tap the matching tile. */
class ColorsShapesMiniGame : MiniGame {
    override val info = MiniGameInfo("colors", R.string.game_colors, "🎨", intArrayOf(Pal.Aqua, Pal.Grape))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val (root, content) = ctx.kidScreen(0xFFE6FBFA.toInt(), 0xFFEFE7FF.toInt(), onExit)
        var round = newRound()
        val tiles = ArrayList<KidButton>()
        fun bind() = tiles.forEachIndexed { i, b ->
            val tile = round.tiles[i]
            b.drawExtra = { c, r -> drawTileShape(c, r, tile) }
            b.invalidate()
        }
        content.add(ctx.kidButton(90, { emoji = "🔊"; emojiFrac = 0.55f; cornerDp = 28f }) {
            services.speech.speak(round.promptText, round.promptLocale)
        }, 10)
        for (r in 0..1) {
            val row = ctx.hbox()
            for (c in 0..1) {
                val index = r * 2 + c
                val b = ctx.kidButton(150) {
                    if (index == round.correctIndex) {
                        services.audio.playCorrect()
                        round = newRound(); bind()
                        services.speech.speak(round.promptText, round.promptLocale)
                    } else services.speech.speak(round.promptText, round.promptLocale)
                }
                tiles.add(b); row.add(b, 8)
            }
            content.add(row)
        }
        bind()
        services.speech.speak(round.promptText, round.promptLocale)
        return root
    }
}
