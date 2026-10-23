package com.example.matteogames.feature.jigsaw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.matteogames.core.Literki
import com.example.matteogames.framework.Pictures

data class Tile(val row: Int, val col: Int, val bitmap: Bitmap)

object PuzzleLogic {

    /** Center-crops to a square so the grid cells look even. */
    fun square(src: Bitmap): Bitmap {
        if (src.width == src.height) return src
        val size = minOf(src.width, src.height)
        val x = (src.width - size) / 2
        val y = (src.height - size) / 2
        return Bitmap.createBitmap(src, x, y, size, size)
    }

    /** Cuts a bitmap into rows x cols tiles. */
    fun slice(src: Bitmap, rows: Int, cols: Int): List<Tile> {
        val sq = square(src)
        val w = sq.width / cols
        val h = sq.height / rows
        val tiles = ArrayList<Tile>(rows * cols)
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                tiles.add(Tile(r, c, Bitmap.createBitmap(sq, c * w, r * h, w, h)))
            }
        }
        return tiles
    }

    // Soft background colors cycled behind emoji puzzle pictures.
    private val EMOJI_BG = listOf(
        Color.rgb(0x9B, 0xD7, 0xFF),
        Color.rgb(0xFF, 0xD6, 0x8A),
        Color.rgb(0xB8, 0xE9, 0xC4),
        Color.rgb(0xF8, 0xC1, 0xDA),
        Color.rgb(0xCF, 0xC4, 0xF5),
        Color.rgb(0xFF, 0xC1, 0xA6),
        Color.rgb(0xA6, 0xE6, 0xE6),
    )

    fun bgFor(i: Int) = EMOJI_BG[(i % EMOJI_BG.size + EMOJI_BG.size) % EMOJI_BG.size]

    /**
     * Renders a puzzle picture: a colored background with the word's picture on top —
     * the Literki drawing (Noto SVG from xx.images) or the bundled Noto PNG.
     */
    fun wordPicture(context: Context, word: Literki.Word, size: Int = 600): Bitmap {
        val bg = bgFor(word.fx ?: word.id.hashCode())
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val s = size.toFloat()
        p.color = bg
        canvas.drawRect(0f, 0f, s, s, p)
        p.color = Color.argb(70, 255, 255, 255)
        canvas.drawCircle(s / 2f, s / 2f, s * 0.42f, p)

        val tw = Pictures.getNow(context, word, (s * 0.66f).toInt())
        if (tw != null) {
            val target = s * 0.66f
            val left = (s - target) / 2f
            val dst = RectF(left, left, left + target, left + target)
            canvas.drawBitmap(tw, null, dst, Paint(Paint.FILTER_BITMAP_FLAG))
        }
        return bmp
    }
}
