package com.example.matteogames.framework

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

// Core playful palette (ARGB)
object Pal {
    const val Sunshine = 0xFFFFC23C.toInt()
    const val SkyBlue = 0xFF5A8DEE.toInt()
    const val Grass = 0xFF57C785.toInt()
    const val Bubblegum = 0xFFEF5DA8.toInt()
    const val Ink = 0xFF3A3A55.toInt()
    const val Tangerine = 0xFFFF8A47.toInt()
    const val Grape = 0xFF9B6DFF.toInt()
    const val Aqua = 0xFF3ED0D6.toInt()
    const val Coral = 0xFFFF6B6B.toInt()
    const val SkyTop = 0xFFEAF2FF.toInt()
    const val SkyBottom = 0xFFFFF0F6.toInt()
    const val Secondary = 0xFF9B6DFF.toInt()
    const val Disabled = 0xFFE3E0EC.toInt()
}

/**
 * Responsive sizing. Every screen is laid out on a virtual 360 x 640 unit board; the real
 * pixels per unit come from the size measured in onSizeChanged (see [sizedScreen]), so the
 * layout always fits whatever the window gives us (status bar, nav bar, Fire Phone...).
 */
object Ui { @JvmStatic var u = 2f }
fun Context.u(v: Number): Int = (v.toFloat() * Ui.u + 0.5f).toInt()
fun Context.uf(v: Number): Float = v.toFloat() * Ui.u

/** Frame that builds its content only once its real size is known, and rebuilds on change. */
class SizedScreen(context: Context, private val build: (FrameLayout, Int, Int) -> Unit) : FrameLayout(context) {
    private var builtW = 0; private var builtH = 0
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w <= 0 || h <= 0 || (w == builtW && h == builtH)) return
        builtW = w; builtH = h
        post {
            removeAllViews()
            Ui.u = minOf(w / 360f, h / 640f)
            build(this, w, h)
        }
    }
}

fun Context.sizedScreen(top: Int, bottom: Int, build: (FrameLayout, Int, Int) -> Unit): FrameLayout =
    SizedScreen(this, build).apply { background = gradientBg(top, bottom) }

fun gradientBg(top: Int, bottom: Int) =
    GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(top, bottom))

/** Arrow glyphs are drawn as shapes (no font dependency). */
enum class Arrow { LEFT, RIGHT, CLOSE }

/**
 * Big, rounded, chunky button drawn on a Canvas, sized for small fingers. Shows a Noto
 * picture, a text label, an arrow, or a custom drawing. Springs when pressed.
 */
open class KidButton(context: Context) : View(context) {
    var fill: Int = Color.WHITE; set(v) { field = v; invalidate() }
    var gradient: IntArray? = null; set(v) { field = v; invalidate() }
    var circle = false
    var cornerDp = 22f
    var emoji: String? = null; set(v) { field = v; invalidate() }
    /** Literki word drawn from its Noto SVG (xx.images); falls back to the bundled Noto PNG. */
    var picture: com.example.matteogames.core.Literki.Word? = null; set(v) { field = v; invalidate() }
    /** Plain = no background/shadow (used for auto-fitting text labels). */
    var plain = false
    var check = false; set(v) { field = v; invalidate() }
    var emojiFrac = 0.6f
    var emojiAlpha = 255; set(v) { field = v; invalidate() }
    var label: String? = null; set(v) { field = v; invalidate() }
    var labelColor: Int = Pal.Secondary; set(v) { field = v; invalidate() }
    var labelSizeFrac = 0.6f
    var labelSizePx = 0f
    /** Optional caption drawn under the emoji (home cards). */
    var caption: String? = null
    var arrow: Arrow? = null
    var badge: String? = null
    var drawExtra: ((Canvas, RectF) -> Unit)? = null

    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bp = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    private val r = RectF()
    private val shadow get() = if (plain) 0f else minOf(width, height) * 0.04f

    init { isClickable = true }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> animate().scaleX(0.9f).scaleY(0.9f).setDuration(80).start()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                animate().scaleX(1f).scaleY(1f).setDuration(120).start()
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(c: Canvas) {
        val w = width.toFloat(); val h = height.toFloat()
        val rad = if (circle) minOf(w, h) / 2f else minOf(context.uf(cornerDp), minOf(w, h) / 2f)
        p.shader = null
        r.set(0f, 0f, w, h - shadow)
        if (!plain) {
            p.color = 0x22000000
            r.set(shadow * 0.3f, shadow, w - shadow * 0.3f, h)
            c.drawRoundRect(r, rad, rad, p)
            r.set(0f, 0f, w - 0f, h - shadow)
            val g = gradient
            if (g != null) {
                p.shader = LinearGradient(0f, 0f, 0f, r.bottom, g[0], g[1], Shader.TileMode.CLAMP)
                p.color = Color.WHITE
            } else p.color = fill
            c.drawRoundRect(r, rad, rad, p)
            p.shader = null
        }
        val cx = r.centerX(); val cy = r.centerY()
        val side = minOf(r.width(), r.height())

        drawExtra?.invoke(c, r)

        arrow?.let { a ->
            p.color = Pal.Ink
            p.style = Paint.Style.FILL
            val s = side * 0.22f
            val path = Path()
            when (a) {
                Arrow.LEFT -> { path.moveTo(cx - s, cy); path.lineTo(cx + s * 0.8f, cy - s); path.lineTo(cx + s * 0.8f, cy + s); path.close(); c.drawPath(path, p) }
                Arrow.RIGHT -> { path.moveTo(cx + s, cy); path.lineTo(cx - s * 0.8f, cy - s); path.lineTo(cx - s * 0.8f, cy + s); path.close(); c.drawPath(path, p) }
                Arrow.CLOSE -> {
                    p.style = Paint.Style.STROKE; p.strokeWidth = side * 0.09f; p.strokeCap = Paint.Cap.ROUND
                    c.drawLine(cx - s, cy - s, cx + s, cy + s, p); c.drawLine(cx + s, cy - s, cx - s, cy + s, p)
                    p.style = Paint.Style.FILL
                }
            }
        }

        val cap = caption
        val emojiCy = if (cap != null) r.top + r.height() * 0.42f else cy
        val es = (if (cap != null) minOf(r.width(), r.height() * 0.62f) else side) * emojiFrac
        val pic = picture
        val bmp = when {
            pic != null -> Pictures.get(context, pic, 256) { postInvalidate() }
            emoji != null -> loadEmoji(context, emoji!!)
            else -> null
        }
        if (bmp != null) {   // never fall back to a font glyph: FireOS draws emoji blank
            bp.alpha = emojiAlpha
            c.drawBitmap(bmp, null, RectF(cx - es / 2, emojiCy - es / 2, cx + es / 2, emojiCy + es / 2), bp)
        }
        label?.let { t ->
            p.color = labelColor
            p.typeface = Typeface.DEFAULT_BOLD
            p.textAlign = Paint.Align.CENTER
            p.textSize = if (labelSizePx > 0) labelSizePx else (if (plain) r.height() else side) * labelSizeFrac
            // shrink to fit
            val maxW = r.width() * 0.88f
            while (p.measureText(t) > maxW && p.textSize > 8f) p.textSize *= 0.92f
            c.drawText(t, cx, cy - (p.descent() + p.ascent()) / 2, p)
        }
        if (cap != null) {
            p.color = Color.WHITE
            p.typeface = Typeface.DEFAULT_BOLD
            p.textAlign = Paint.Align.CENTER
            p.textSize = r.height() * 0.15f
            val maxW = r.width() * 0.9f
            while (p.measureText(cap) > maxW && p.textSize > 8f) p.textSize *= 0.92f
            c.drawText(cap, cx, r.top + r.height() * 0.86f, p)
        }
        if (check) {   // drawn tick (no font glyph)
            p.color = 0xFF2E7D46.toInt(); p.style = Paint.Style.STROKE; p.strokeWidth = side * 0.07f
            p.strokeCap = Paint.Cap.ROUND; p.strokeJoin = Paint.Join.ROUND
            val bx = r.right - side * 0.24f; val by = r.top + side * 0.1f; val s = side * 0.16f
            val path = Path(); path.moveTo(bx, by + s * 0.55f); path.lineTo(bx + s * 0.38f, by + s * 0.9f); path.lineTo(bx + s, by)
            c.drawPath(path, p); p.style = Paint.Style.FILL
        }
        badge?.let { b ->
            p.color = 0xFF2E7D46.toInt(); p.typeface = Typeface.DEFAULT_BOLD
            p.textAlign = Paint.Align.RIGHT; p.textSize = side * 0.24f
            c.drawText(b, r.right - side * 0.08f, r.top + side * 0.26f, p)
        }
    }
}

// --- tiny layout helpers (sizes in units, see [Ui]) ---

fun Context.kidButton(sizeU: Int, block: KidButton.() -> Unit = {}, onTap: () -> Unit): KidButton =
    KidButton(this).apply {
        layoutParams = LinearLayout.LayoutParams(u(sizeU), u(sizeU))
        block()
        setOnClickListener { onTap() }
    }

fun Context.circleButton(sizeU: Int, emoji: String? = null, arrow: Arrow? = null, fill: Int = Color.WHITE, onTap: () -> Unit) =
    kidButton(sizeU, {
        circle = true; this.emoji = emoji; this.arrow = arrow; this.fill = fill; emojiFrac = 0.55f
    }, onTap)

fun Context.vbox(gravity: Int = Gravity.CENTER_HORIZONTAL): LinearLayout = LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL; this.gravity = gravity
}

fun Context.hbox(gravity: Int = Gravity.CENTER): LinearLayout = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL; this.gravity = gravity
}

/** Adds [child] with an optional margin (units) around it. */
fun ViewGroup.add(child: View, marginU: Int = 0, w: Int = -3, h: Int = -3): View {
    val m = context.u(marginU)
    val old = child.layoutParams
    val lw = if (w != -3) w else old?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT
    val lh = if (h != -3) h else old?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
    val lp: ViewGroup.LayoutParams = when (this) {
        is LinearLayout -> LinearLayout.LayoutParams(lw, lh).apply { setMargins(m, m, m, m); (old as? LinearLayout.LayoutParams)?.let { weight = it.weight; gravity = it.gravity } }
        is FrameLayout -> FrameLayout.LayoutParams(lw, lh).apply { setMargins(m, m, m, m) }
        else -> ViewGroup.MarginLayoutParams(lw, lh).apply { setMargins(m, m, m, m) }
    }
    addView(child, lp)
    return child
}

fun Context.spacer(hU: Int): View = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, u(hU)) }

/** Flexible empty space inside a LinearLayout. */
fun Context.flex(): View = View(this).apply { layoutParams = LinearLayout.LayoutParams(0, 0, 1f) }

/** Auto-fitting one-line text in a fixed box (w x h units; w = -1 means match parent). */
fun Context.fitLabel(text: String, wU: Int, hU: Int, color: Int = Pal.Ink): KidButton = KidButton(this).apply {
    plain = true; isClickable = false; label = text; labelColor = color; labelSizeFrac = 0.8f
    layoutParams = LinearLayout.LayoutParams(if (wU < 0) ViewGroup.LayoutParams.MATCH_PARENT else u(wU), u(hU))
}

/** Picture-only view (bundled Noto PNG or a Literki word drawing). */
class EmojiView(context: Context, emoji: String?) : KidButton(context) {
    init { this.emoji = emoji; emojiFrac = 1f; plain = true; isClickable = false }
    override fun onTouchEvent(event: MotionEvent) = false
}

fun Context.emojiView(emoji: String?, sizeU: Int) = EmojiView(this, emoji).apply {
    layoutParams = LinearLayout.LayoutParams(u(sizeU), u(sizeU))
}

/**
 * Standard page scaffold, built once the real size is known: gradient background, a top bar
 * (back button left, optional [right] views) and a content column that fills the rest.
 * [build] receives the content column and its height in units.
 */
fun Context.kidScreen(top: Int, bottom: Int, onExit: () -> Unit, right: (LinearLayout) -> Unit = {},
                      build: (content: LinearLayout, root: FrameLayout, hU: Float) -> Unit): FrameLayout =
    sizedScreen(top, bottom) { root, _, h ->
        val col = vbox()
        root.add(col, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val bar = hbox(Gravity.CENTER_VERTICAL)
        bar.setPadding(u(10), u(8), u(10), u(4))
        bar.add(circleButton(56, arrow = Arrow.LEFT) { onExit() })
        bar.add(flex())
        right(bar)
        col.add(bar, 0, ViewGroup.LayoutParams.MATCH_PARENT, u(70))
        val content = vbox(Gravity.CENTER)
        col.addView(content, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        build(content, root, h / Ui.u - 70f)
    }
