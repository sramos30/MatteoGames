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
import android.widget.TextView

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

fun Context.dp(v: Number): Int = (v.toFloat() * resources.displayMetrics.density + 0.5f).toInt()
fun Context.dpf(v: Number): Float = v.toFloat() * resources.displayMetrics.density

fun gradientBg(top: Int, bottom: Int) =
    GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(top, bottom))

/** Arrow glyphs are drawn as shapes (no font dependency). */
enum class Arrow { LEFT, RIGHT, CLOSE }

/**
 * Big, rounded, chunky button drawn on a Canvas, sized for small fingers. Shows a Twemoji
 * picture, a text label, an arrow, or a custom drawing. Springs when pressed.
 */
open class KidButton(context: Context) : View(context) {
    var fill: Int = Color.WHITE; set(v) { field = v; invalidate() }
    var gradient: IntArray? = null; set(v) { field = v; invalidate() }
    var circle = false
    var cornerDp = 22f
    var emoji: String? = null; set(v) { field = v; invalidate() }
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
    private val shadow = context.dpf(4)

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
        val rad = if (circle) minOf(w, h) / 2f else context.dpf(cornerDp)
        // shadow
        p.shader = null
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
        emoji?.let { e ->
            val bmp = loadTwemoji(context, e)
            val es = side * emojiFrac
            if (bmp != null) {
                bp.alpha = emojiAlpha
                c.drawBitmap(bmp, null, RectF(cx - es / 2, emojiCy - es / 2, cx + es / 2, emojiCy + es / 2), bp)
            } else {
                p.color = Pal.Ink; p.textAlign = Paint.Align.CENTER; p.textSize = es * 0.9f
                c.drawText(e, cx, emojiCy - (p.descent() + p.ascent()) / 2, p)
            }
        }
        label?.let { t ->
            p.color = labelColor
            p.typeface = Typeface.DEFAULT_BOLD
            p.textAlign = Paint.Align.CENTER
            p.textSize = if (labelSizePx > 0) labelSizePx else side * labelSizeFrac
            // shrink to fit
            val maxW = r.width() * 0.88f
            while (p.measureText(t) > maxW && p.textSize > 8f) p.textSize *= 0.92f
            c.drawText(t, cx, cy - (p.descent() + p.ascent()) / 2, p)
        }
        if (cap != null) {
            p.color = Color.WHITE
            p.typeface = Typeface.DEFAULT_BOLD
            p.textAlign = Paint.Align.CENTER
            p.textSize = context.dpf(20)
            val maxW = r.width() * 0.9f
            while (p.measureText(cap) > maxW && p.textSize > 8f) p.textSize *= 0.92f
            c.drawText(cap, cx, r.top + r.height() * 0.86f, p)
        }
        badge?.let { b ->
            p.color = 0xFF2E7D46.toInt(); p.typeface = Typeface.DEFAULT_BOLD
            p.textAlign = Paint.Align.RIGHT; p.textSize = side * 0.24f
            c.drawText(b, r.right - side * 0.08f, r.top + side * 0.26f, p)
        }
    }
}

// --- tiny layout helpers ---

fun Context.kidButton(sizeDp: Int, block: KidButton.() -> Unit = {}, onTap: () -> Unit): KidButton =
    KidButton(this).apply {
        layoutParams = LinearLayout.LayoutParams(dp(sizeDp), dp(sizeDp))
        block()
        setOnClickListener { onTap() }
    }

fun Context.circleButton(sizeDp: Int, emoji: String? = null, arrow: Arrow? = null, fill: Int = Color.WHITE, onTap: () -> Unit) =
    kidButton(sizeDp, {
        circle = true; this.emoji = emoji; this.arrow = arrow; this.fill = fill; emojiFrac = 0.55f
    }, onTap)

fun Context.vbox(gravity: Int = Gravity.CENTER_HORIZONTAL): LinearLayout = LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL; this.gravity = gravity
}

fun Context.hbox(gravity: Int = Gravity.CENTER): LinearLayout = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL; this.gravity = gravity
}

/** Adds [child] with an optional margin (dp) around it. */
fun ViewGroup.add(child: View, marginDp: Int = 0, w: Int = -3, h: Int = -3): View {
    val ctx = context
    val m = ctx.dp(marginDp)
    val old = child.layoutParams
    val lw = if (w != -3) w else old?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT
    val lh = if (h != -3) h else old?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
    val lp: ViewGroup.LayoutParams = when (this) {
        is LinearLayout -> LinearLayout.LayoutParams(lw, lh).apply { setMargins(m, m, m, m) }
        is FrameLayout -> FrameLayout.LayoutParams(lw, lh).apply { setMargins(m, m, m, m) }
        else -> ViewGroup.MarginLayoutParams(lw, lh).apply { setMargins(m, m, m, m) }
    }
    addView(child, lp)
    return child
}

fun Context.spacer(hDp: Int): View = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(hDp)) }

fun Context.label(text: String, sizeSp: Float, color: Int = Pal.Ink, bold: Boolean = true): TextView =
    TextView(this).apply {
        this.text = text; textSize = sizeSp; setTextColor(color); gravity = Gravity.CENTER
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

/** Simple Twemoji image view. */
class EmojiView(context: Context, emoji: String) : KidButton(context) {
    init {
        this.emoji = emoji; emojiFrac = 1f; isClickable = false
    }
    override fun onDraw(c: Canvas) {
        val e = emoji ?: return
        val bmp = loadTwemoji(context, e) ?: return
        c.drawBitmap(bmp, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), Paint(Paint.FILTER_BITMAP_FLAG))
    }
    override fun onTouchEvent(event: MotionEvent) = false
}

fun Context.emojiView(emoji: String, sizeDp: Int) = EmojiView(this, emoji).apply {
    layoutParams = LinearLayout.LayoutParams(dp(sizeDp), dp(sizeDp))
}

/** Flag + word chip that speaks when tapped. */
fun Context.wordChip(flag: String, word: String, onTap: () -> Unit): View {
    val row = hbox().apply {
        background = GradientDrawable().apply { setColor(Color.WHITE); cornerRadius = dpf(18) }
        setPadding(dp(14), dp(6), dp(14), dp(6))
        isClickable = true
        setOnClickListener { onTap() }
    }
    row.add(emojiView(flag, 26))
    row.add(label("  $word", 20f), 0)
    return row
}

/**
 * Standard page scaffold: gradient background + round back button top-left.
 * Returns (root, contentColumn). The content column fills the rest and centers children.
 */
fun Context.kidScreen(top: Int, bottom: Int, onExit: () -> Unit): Pair<FrameLayout, LinearLayout> {
    val root = FrameLayout(this)
    root.background = gradientBg(top, bottom)
    val col = vbox()
    root.add(col, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    val bar = hbox(Gravity.START or Gravity.CENTER_VERTICAL)
    bar.add(circleButton(60, arrow = Arrow.LEFT) { onExit() }, 12)
    col.add(bar, 0, ViewGroup.LayoutParams.MATCH_PARENT)
    val content = vbox(Gravity.CENTER)
    col.addView(content, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
    return root to content
}
