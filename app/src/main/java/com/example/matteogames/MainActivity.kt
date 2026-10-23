package com.example.matteogames

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.matteogames.framework.GameServices
import com.example.matteogames.framework.MiniGameRegistry
import com.example.matteogames.framework.homeView

/** Single plain Activity (no AndroidX) — swaps full-screen Views for Home and each game. */
class MainActivity : Activity() {

    private lateinit var services: GameServices
    private var inGame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        services = GameServices(this)
        showHome()
    }

    private fun showHome() {
        services.speech.stop()
        services.speech.onSpeakingChanged = null
        services.onImagePicked = null
        inGame = false
        setContentView(homeView(this) { id -> showGame(id) })
    }

    private fun showGame(id: String) {
        services.speech.stop()
        inGame = true
        val view: View = MiniGameRegistry.byId(id).createView(services) { showHome() }
        setContentView(view)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (inGame) showHome() else super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        services.speech.stop()
    }

    override fun onDestroy() {
        services.speech.shutdown()
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
