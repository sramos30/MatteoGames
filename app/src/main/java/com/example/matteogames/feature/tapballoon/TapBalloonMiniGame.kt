package com.example.matteogames.feature.tapballoon

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.widget.FrameLayout
import com.example.matteogames.R
import com.example.matteogames.framework.Arrow
import com.example.matteogames.framework.GameServices
import com.example.matteogames.framework.KidButton
import com.example.matteogames.framework.MiniGame
import com.example.matteogames.framework.MiniGameInfo
import com.example.matteogames.framework.Pal
import com.example.matteogames.framework.circleButton
import com.example.matteogames.framework.dp
import com.example.matteogames.framework.gradientBg
import kotlin.random.Random

/** Tap the bobbing balloon; it glides to a new spot with a new color and picture. */
class TapBalloonMiniGame : MiniGame {
    override val info = MiniGameInfo("tapballoon", R.string.game_tapballoon, "🎈", intArrayOf(Pal.Bubblegum, Pal.Tangerine))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val colors = intArrayOf(0xFFEF5DA8.toInt(), 0xFF5A8DEE.toInt(), 0xFF57C785.toInt(), 0xFFFFC23C.toInt())
        val glyphs = listOf("🎈", "⚽", "⭐", "🐲", "🦸")
        val root = FrameLayout(ctx)
        root.background = gradientBg(Pal.SkyTop, 0xFFFFE7F1.toInt())
        val size = ctx.dp(140)
        val balloon = KidButton(ctx).apply {
            circle = true; fill = colors.random(); emoji = glyphs.random(); emojiFrac = 0.52f
        }
        root.addView(balloon, FrameLayout.LayoutParams(size, size))
        fun move() {
            val w = (root.width - size).coerceAtLeast(0)
            val h = (root.height - size).coerceAtLeast(0)
            balloon.animate().x(Random.nextFloat() * w).y(ctx.dp(80) + Random.nextFloat() * (h - ctx.dp(80)).coerceAtLeast(0))
                .setDuration(450).start()
        }
        balloon.setOnClickListener {
            services.audio.playCorrect()
            balloon.fill = colors.random()
            balloon.emoji = glyphs.random()
            move()
        }
        root.post { balloon.x = (root.width - size) * 0.3f; balloon.y = (root.height - size) * 0.3f }
        // Gentle bob so the balloon feels alive.
        ObjectAnimator.ofFloat(balloon, "rotation", -6f, 6f).apply {
            duration = 900; repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; start()
        }
        root.addView(ctx.circleButton(60, arrow = Arrow.LEFT) { onExit() },
            FrameLayout.LayoutParams(ctx.dp(60), ctx.dp(60)).apply { setMargins(ctx.dp(12), ctx.dp(12), 0, 0) })
        return root
    }
}
