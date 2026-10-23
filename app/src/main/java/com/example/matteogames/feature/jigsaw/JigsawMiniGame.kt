package com.example.matteogames.feature.jigsaw

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout
import com.example.matteogames.MainActivity
import com.example.matteogames.R
import com.example.matteogames.core.Literki
import android.widget.AbsListView
import com.example.matteogames.framework.*
import kotlin.math.hypot
import kotlin.random.Random

private class Piece(val row: Int, val col: Int, val bmp: Bitmap) {
    var x = 0f; var y = 0f
    var placed = false
}

/** Drag-and-drop board drawn on a Canvas. */
private class BoardView(context: Context) : View(context) {
    var grid = 2
    var source: Bitmap? = null
    var hint: Bitmap? = null
    var pieces: List<Piece> = emptyList()
    var restore: Set<String>? = null
    var onPlaced: (allDone: Boolean) -> Unit = {}

    private var boardLeft = 0f; private var boardTop = 0f; private var boardSize = 0f; private var cell = 0f
    private var laidOut = false
    private var drag: Piece? = null
    private var lastX = 0f; private var lastY = 0f
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bp = Paint(Paint.FILTER_BITMAP_FLAG)
    private val hp = Paint(Paint.FILTER_BITMAP_FLAG).apply { alpha = 46 }
    private val r = RectF()

    fun setPuzzle(src: Bitmap, g: Int, placed: Set<String>?) {
        source = src; grid = g
        hint = PuzzleLogic.square(src)
        pieces = PuzzleLogic.slice(src, g, g).map { Piece(it.row, it.col, it.bitmap) }
        restore = placed
        laidOut = false
        requestLayout(); invalidate()
    }

    private fun layoutPieces() {
        val w = width.toFloat(); val h = height.toFloat()
        if (w <= 0 || h <= 0 || pieces.isEmpty()) return
        boardSize = minOf(w * 0.92f, h * 0.55f)
        cell = boardSize / grid
        boardLeft = (w - boardSize) / 2f
        boardTop = h * 0.02f
        if (laidOut) return
        val rnd = Random(42)
        val trayTop = boardTop + boardSize + cell * 0.15f
        val trayBottom = (h - cell * 1.08f).coerceAtLeast(trayTop)
        val loose = ArrayList<Piece>()
        pieces.forEach { pc ->
            if (restore?.contains("${pc.row},${pc.col}") == true) {
                pc.x = boardLeft + pc.col * cell; pc.y = boardTop + pc.row * cell; pc.placed = true
            } else loose.add(pc)
        }
        // tray: shuffled tidy grid inside the view; rows overlap only when space is short
        loose.shuffle(rnd)
        val perRow = maxOf(1, ((w * 0.96f) / (cell * 1.04f)).toInt())
        val rows = (loose.size + perRow - 1) / perRow
        val stepX = if (perRow > 1) (w - cell * 1.1f) / (perRow - 1) else 0f
        val stepY = if (rows > 1) minOf(cell * 1.04f, (trayBottom - trayTop) / (rows - 1)) else 0f
        loose.forEachIndexed { i, pc ->
            val col = i % perRow; val row = i / perRow
            pc.x = (if (perRow > 1) cell * 0.05f + col * stepX else (w - cell) / 2f) + (row % 2) * cell * 0.08f
            pc.y = trayTop + row * stepY
            pc.x = pc.x.coerceIn(0f, (w - cell).coerceAtLeast(0f)); pc.y = pc.y.coerceIn(0f, (h - cell).coerceAtLeast(0f))
        }
        restore = null
        laidOut = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { laidOut = false; layoutPieces() }

    override fun onDraw(c: Canvas) {
        layoutPieces()
        if (pieces.isEmpty()) return
        p.color = 0xB3FFFFFF.toInt()
        r.set(boardLeft, boardTop, boardLeft + boardSize, boardTop + boardSize)
        c.drawRoundRect(r, 20f, 20f, p)
        hint?.let { c.drawBitmap(it, null, r, hp) }
        p.color = 0x33000000; p.strokeWidth = 2f
        for (i in 0..grid) {
            val gx = boardLeft + i * cell; val gy = boardTop + i * cell
            c.drawLine(gx, boardTop, gx, boardTop + boardSize, p)
            c.drawLine(boardLeft, gy, boardLeft + boardSize, gy, p)
        }
        // placed first, then loose pieces, dragged on top
        pieces.filter { it.placed }.forEach { draw(c, it) }
        pieces.filter { !it.placed && it !== drag }.forEach { draw(c, it) }
        drag?.let { draw(c, it) }
    }

    private fun draw(c: Canvas, pc: Piece) {
        r.set(pc.x, pc.y, pc.x + cell, pc.y + cell)
        if (!pc.placed) { p.color = 0x33000000; c.drawRect(r.left + 4, r.top + 6, r.right + 4, r.bottom + 6, p) }
        c.drawBitmap(pc.bmp, null, r, bp)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                drag = pieces.lastOrNull { !it.placed && e.x >= it.x && e.x <= it.x + cell && e.y >= it.y && e.y <= it.y + cell }
                    ?: pieces.lastOrNull { !it.placed && e.x >= it.x - cell * 0.2f && e.x <= it.x + cell * 1.2f && e.y >= it.y - cell * 0.2f && e.y <= it.y + cell * 1.2f }
                lastX = e.x; lastY = e.y
                if (drag != null) parent?.requestDisallowInterceptTouchEvent(true)
                return drag != null
            }
            MotionEvent.ACTION_MOVE -> {
                val d = drag ?: return false
                d.x += e.x - lastX; d.y += e.y - lastY
                lastX = e.x; lastY = e.y
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val d = drag ?: return false
                drag = null
                val slotX = boardLeft + d.col * cell; val slotY = boardTop + d.row * cell
                if (hypot(d.x - slotX, d.y - slotY) < cell * 0.55f) {
                    d.x = slotX; d.y = slotY; d.placed = true
                    onPlaced(pieces.all { it.placed })
                } else {
                    // keep the piece on screen
                    d.x = d.x.coerceIn(0f, (width - cell).coerceAtLeast(0f))
                    d.y = d.y.coerceIn(0f, (height - cell).coerceAtLeast(0f))
                }
                invalidate()
            }
        }
        return true
    }

    fun placedCells(): Set<String> = pieces.filter { it.placed }.map { "${it.row},${it.col}" }.toSet()
}

/** Drag the pieces onto the board. Every picture is a Literki word (picture + text + voice). */
class JigsawMiniGame : MiniGame {
    override val info = MiniGameInfo("jigsaw", R.string.game_jigsaw, "🧩", intArrayOf(Pal.SkyBlue, Pal.Aqua))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val settings = services.settings
        val pics = Literki.wordsWithPicture()
        var grid = settings.gridSize.coerceIn(2, 4)
        var pictureId: String? = null
        val gridButtons = ArrayList<Pair<Int, KidButton>>()
        lateinit var galleryBtn: KidButton
        lateinit var openGallery: () -> Unit
        lateinit var board: BoardView
        lateinit var refreshChip: () -> Unit
        fun currentPic() = pics.firstOrNull { it.id == pictureId }

        return ctx.kidScreen(Pal.SkyTop, Pal.SkyBottom, onExit, right = { bar ->
            listOf(2, 3, 4).forEach { n ->
                val b = ctx.kidButton(46, { label = "${n}x$n"; labelSizeFrac = 0.34f; cornerDp = 14f }) {
                    grid = n
                    settings.gridSize = n
                    settings.puzzlePlaced = emptySet()
                    board.source?.let { board.setPuzzle(it, n, null) }
                    refreshChip()
                }
                gridButtons.add(n to b)
                bar.add(b, 3)
            }
            bar.add(ctx.flex())
            galleryBtn = ctx.circleButton(50, "🖼️") { openGallery() }
            bar.add(galleryBtn, 3)
            bar.add(ctx.circleButton(50, "📷") {
                parentalGate(ctx) {
                    try {
                        val i = Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE)
                        @Suppress("DEPRECATION")
                        ctx.startActivityForResult(i, MainActivity.REQ_PICK_IMAGE)
                    } catch (e: Exception) { }
                }
            }, 3)
        }) { col, root, _ ->
            board = BoardView(ctx)
            // word chip: [language] WORD  (tap = hear it)
            val chipRow = ctx.hbox(Gravity.CENTER_VERTICAL)
            chipRow.setPadding(ctx.u(10), 0, ctx.u(10), 0)
            val word = ctx.kidButton(50, { cornerDp = 18f; labelColor = Pal.Ink; labelSizeFrac = 0.6f }) {
                services.sayWord(pictureId)
            }
            chipRow.add(ctx.langButton(services, 50, { pictureId }) { _ -> refreshChip(); services.sayWord(pictureId) }, 3)
            chipRow.addView(word, LinearLayout.LayoutParams(0, ctx.u(50), 1f).apply { setMargins(ctx.u(6), 0, 0, 0) })
            col.add(chipRow, 0, ViewGroup.LayoutParams.MATCH_PARENT, ctx.u(60))
            col.addView(board, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

            refreshChip = {
                val pic = currentPic()
                galleryBtn.picture = pic
                galleryBtn.emoji = if (pic == null) "🖼️" else null
                word.label = pic?.let { services.text(it.id) } ?: ""
                word.visibility = if (pic == null) View.INVISIBLE else View.VISIBLE
                gridButtons.forEach { (n, b) ->
                    b.fill = if (n == grid) Pal.Sunshine else 0xFFFFFFFF.toInt()
                    b.labelColor = if (n == grid) 0xFFFFFFFF.toInt() else Pal.Ink
                }
            }
            fun loadPicture(pic: Literki.Word, placed: Set<String>?) {
                pictureId = pic.id
                settings.jigsawPicture = pic.id
                if (placed == null) settings.puzzlePlaced = emptySet()
                board.setPuzzle(PuzzleLogic.wordPicture(ctx, pic, 600), grid, placed)
                refreshChip()
                services.sayWord(pic.id)
            }

            // gallery overlay: columns computed from the real width
            openGallery = {
                val overlay = FrameLayout(ctx)
                overlay.tag = "overlay"   // MainActivity.onBackPressed closes it first
                overlay.background = gradientBg(Pal.SkyTop, Pal.SkyBottom)
                overlay.isClickable = true
                val oc = ctx.vbox()
                overlay.add(oc, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val head = ctx.hbox(Gravity.CENTER_VERTICAL)
                head.setPadding(ctx.u(10), ctx.u(8), ctx.u(10), ctx.u(4))
                head.add(ctx.emojiView("🖼️", 48))
                head.add(ctx.flex())
                head.add(ctx.circleButton(56, arrow = Arrow.CLOSE) { root.removeView(overlay) })
                oc.add(head, 0, ViewGroup.LayoutParams.MATCH_PARENT, ctx.u(70))
                val completed = settings.completedPictures
                val gv = GridView(ctx)
                val cols = 3
                val cell = ctx.u(104)
                gv.numColumns = cols
                gv.stretchMode = GridView.STRETCH_SPACING_UNIFORM
                gv.columnWidth = cell
                gv.verticalSpacing = ctx.u(8)
                gv.setPadding(ctx.u(6), ctx.u(4), ctx.u(6), ctx.u(8))
                gv.adapter = object : BaseAdapter() {
                    override fun getCount() = pics.size
                    override fun getItem(position: Int) = pics[position]
                    override fun getItemId(position: Int) = position.toLong()
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        val v = (convertView as? KidButton) ?: KidButton(ctx).apply {
                            isClickable = false; isFocusable = false; emojiFrac = 0.7f
                            layoutParams = AbsListView.LayoutParams(cell, cell)
                        }
                        val pic = pics[position]
                        val done = completed.contains(pic.id)
                        val selected = pic.id == pictureId
                        v.picture = pic
                        v.fill = when { selected -> Pal.Sunshine; done -> 0xFFE4E4E8.toInt(); else -> 0xFFFFFFFF.toInt() }
                        v.emojiAlpha = if (done && !selected) 110 else 255
                        v.check = done
                        return v
                    }
                }
                gv.setOnItemClickListener { _, _, position, _ ->
                    root.removeView(overlay)
                    loadPicture(pics[position], null)
                }
                oc.addView(gv, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
                val idx = pics.indexOfFirst { it.id == pictureId }
                if (idx > 0) gv.setSelection(idx)
                root.addView(overlay, FrameLayout.LayoutParams(-1, -1))
            }

            // photo import behind a parental gate
            services.onImagePicked = { uri ->
                val file = services.imageStore.importFromUri(uri)
                val bmp = file?.let { services.imageStore.loadBitmap(it) }
                if (bmp != null) {
                    pictureId = null
                    settings.puzzlePlaced = emptySet()
                    board.setPuzzle(bmp, grid, null)
                    refreshChip()
                }
            }

            // win overlay: party picture + Literki's "p.ok" text and voice
            val win = ctx.vbox(Gravity.CENTER).apply {
                setBackgroundColor(0xE6FFFFFF.toInt()); visibility = View.GONE; isClickable = true
            }
            val party = ctx.emojiView("🎉", 150)
            win.add(party)
            val winText = ctx.fitLabel("", 320, 64, Pal.Secondary)
            win.add(winText)
            root.add(win, 0, -1, -1)
            ObjectAnimator.ofFloat(party, "scaleX", 0.85f, 1.15f).apply { duration = 600; repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; start() }
            ObjectAnimator.ofFloat(party, "scaleY", 0.85f, 1.15f).apply { duration = 600; repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; start() }

            board.onPlaced = { allDone ->
                services.audio.playCorrect()
                settings.puzzlePlaced = board.placedCells()
                if (allDone && pics.isNotEmpty()) {
                    services.celebrate()
                    winText.label = services.text("p.ok")
                    win.visibility = View.VISIBLE
                    val wasCustom = pictureId == null
                    currentPic()?.let { settings.addCompletedPicture(it.id) }
                    services.praise(pictureId)
                    root.postDelayed({
                        win.visibility = View.GONE
                        if (wasCustom) services.imageStore.clear()
                        settings.puzzlePlaced = emptySet()
                        val idx = pics.indexOfFirst { it.id == pictureId }
                        loadPicture(pics[if (idx < 0) 0 else (idx + 1) % pics.size], null)
                    }, 3000)
                }
            }

            // resume last picture with its placed pieces
            val pic = pics.firstOrNull { it.id == settings.jigsawPicture } ?: pics.firstOrNull()
            if (pic != null) loadPicture(pic, settings.puzzlePlaced) else refreshChip()
        }
    }
}

/** Grown-ups only: tap the number seven. */
private fun parentalGate(ctx: Context, onPass: () -> Unit) {
    val row = ctx.hbox()
    row.setPadding(ctx.u(6), ctx.u(14), ctx.u(6), ctx.u(14))
    val dlg = AlertDialog.Builder(ctx)
        .setTitle(R.string.parental_gate_prompt)
        .setView(row)
        .setNegativeButton(R.string.back, null)
        .create()
    listOf(7, 2, 5, 9).shuffled().forEach { d ->
        row.add(ctx.kidButton(56, { label = "$d"; labelSizeFrac = 0.5f; fill = Pal.SkyBlue; labelColor = 0xFFFFFFFF.toInt() }) {
            if (d == 7) { dlg.dismiss(); onPass() }
        }, 4)
    }
    dlg.show()
}
