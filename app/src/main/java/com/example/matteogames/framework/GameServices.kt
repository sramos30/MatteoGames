package com.example.matteogames.framework

import android.app.Activity
import com.example.matteogames.core.AudioService
import com.example.matteogames.core.ImageStore
import com.example.matteogames.core.SettingsRepository
import com.example.matteogames.core.SpeechService

/** Shared plumbing so individual minigames don't re-implement audio, storage, etc. */
class GameServices(val activity: Activity) {
    val audio = AudioService(activity)
    val imageStore = ImageStore(activity)
    val settings = SettingsRepository(activity)
    val speech = SpeechService(activity)

    /** Set by a game that wants the result of the system image picker. */
    var onImagePicked: ((android.net.Uri) -> Unit)? = null

    fun celebrate() = audio.playWin()
}
