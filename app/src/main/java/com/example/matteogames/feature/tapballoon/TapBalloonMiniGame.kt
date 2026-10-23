package com.example.matteogames.feature.tapballoon

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.widget.FrameLayout
import com.example.matteogames.R
import com.example.matteogames.framework.*
import kotlin.random.Random

/** Tap the bobbing balloon; it glides to a new spot with a new color and a Literki word picture (spoken). */
class TapBalloonMiniGame : MiniGame {
    override val info = MiniGameInfo("tapballoon", R.string.game_tapballoon, "🎈", intArrayOf(Pal.Bubblegum, Pal.Tangerine))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val colors = intArrayOf(0xFFEF5DA8.toInt(), 0xFF5A8DEE.toInt(), 0xFF57C785.toInt(), 0xFFFFC23C.toInt())
        return ctx.sizedScreen(Pal.SkyTop, 0xFFFFE7F1.toInt()) { root, w, h ->
            val size = ctx.u(150)
            val top = ctx.u(80)
            val balloon = KidButton(ctx).apply { circle = true; fill = colors.random(); emojiFrac = 0.6f }
            fun pick() { val ws = services.words; if (ws.isNotEmpty()) balloon.picture = ws.random() else balloon.emoji = "🎈" }
            pick()
            root.addView(balloon, FrameLayout.LayoutParams(size, size))
            balloon.x = (w - size) * 0.5f; balloon.y = top + (h - top - size) * 0.4f
            balloon.setOnClickListener {
                services.audio.playCorrect()
                balloon.fill = colors.random()
                pick()
                services.sayWord(balloon.picture?.id)
                balloon.animate().x(Random.nextFloat() * (w - size).coerceAtLeast(0))
                    .y(top + Random.nextFloat() * (h - top - size).coerceAtLeast(0)).setDuration(450).start()
            }
            ObjectAnimator.ofFloat(balloon, "rotation", -6f, 6f).apply {
                duration = 900; repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; start()
            }
            root.addView(ctx.circleButton(56, arrow = Arrow.LEFT) { onExit() },
                FrameLayout.LayoutParams(ctx.u(56), ctx.u(56)).apply { setMargins(ctx.u(10), ctx.u(10), 0, 0) })
            root.addView(ctx.langButton(services, 56, { balloon.picture?.id }) { _ -> services.sayWord(balloon.picture?.id) },
                FrameLayout.LayoutParams(ctx.u(56), ctx.u(56)).apply {
                    gravity = android.view.Gravity.END; setMargins(0, ctx.u(10), ctx.u(10), 0) })
        }
    }
}
