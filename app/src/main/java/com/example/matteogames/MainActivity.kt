package com.example.matteogames

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.matteogames.core.Literki
import com.example.matteogames.framework.GameServices
import com.example.matteogames.framework.MiniGameRegistry
import com.example.matteogames.framework.Pictures
import com.example.matteogames.framework.homeView
import com.example.matteogames.framework.messageView

/** Single plain Activity (no AndroidX) — swaps full-screen Views for Home and each game. */
class MainActivity : Activity() {

    private lateinit var services: GameServices
    private var inGame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        services = GameServices(this)
        loadCatalog()
        immersive()
    }

    override fun onResume() {
        super.onResume()
        immersive()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) immersive()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        immersive()
    }

    /** Same full screen as Literki: no status bar, no Android navigation buttons (sticky immersive). */
    @Suppress("DEPRECATION")
    private fun immersive() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    /** Literki's provider is read off the UI thread; meanwhile a friendly "loading" page. */
    private fun loadCatalog() {
        inGame = false
        setContentView(messageView(this, "🌈", "Matteo Games", "...", null))
        Thread {
            val st = Literki.load(applicationContext)
            runOnUiThread {
                if (isFinishing) return@runOnUiThread
                if (st == Literki.Status.OK) {
                    Pictures.preferLang = services.lang.lang?.id
                    showHome()
                } else showProblem(st)
            }
        }.start()
    }

    private fun showProblem(st: Literki.Status) {
        val (pic, msg) = when (st) {
            Literki.Status.NO_LITERKI -> "📦" to "Instale o Literki e os pacotes de língua para jogar."
            Literki.Status.NO_PERMISSION -> "🔑" to "Reinstale o Matteo Games depois do Literki para ele poder ler os pacotes."
            else -> "📦" to "Nenhum pacote de língua instalado no Literki."
        }
        setContentView(messageView(this, pic, "Ops!", msg) { loadCatalog() })
    }

    private fun showHome() {
        services.voice.stop()
        services.voice.onSpeakingChanged = null
        services.onImagePicked = null
        inGame = false
        setContentView(homeView(services) { id -> showGame(id) })
    }

    private fun showGame(id: String) {
        services.voice.stop()
        services.voice.onSpeakingChanged = null
        inGame = true
        val view: View = MiniGameRegistry.byId(id).createView(services) { showHome() }
        setContentView(view)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // an open overlay (e.g. the puzzle gallery) closes before the game does
        val overlay = window.decorView.findViewWithTag<View>("overlay")
        if (overlay != null) (overlay.parent as? android.view.ViewGroup)?.removeView(overlay)
        else if (inGame) showHome() else super.onBackPressed()
    }

    /**
     * Back from Literki (house button, kiosk Apps screen): re-read the packages and rebuild Home so the
     * language button shows the saved choice, never a stale label or a fallback from a reload in progress.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)   // singleTask: keep extras (test hook mg_count) for the next game opened
    }

    override fun onRestart() {
        super.onRestart()
        if (!inGame) loadCatalog()
    }

    override fun onStop() {
        super.onStop()
        services.voice.stop()
    }

    override fun onDestroy() {
        services.voice.shutdown()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { uri -> services.onImagePicked?.invoke(uri) }
        }
    }

    companion object {
        const val REQ_PICK_IMAGE = 41
    }
}
