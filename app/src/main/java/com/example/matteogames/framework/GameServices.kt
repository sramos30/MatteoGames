package com.example.matteogames.framework

import android.app.Activity
import com.example.matteogames.core.AudioService
import com.example.matteogames.core.ImageStore
import com.example.matteogames.core.Literki
import com.example.matteogames.core.SettingsRepository
import com.example.matteogames.core.Voice

/** Shared plumbing so individual minigames don't re-implement audio, storage, etc. */
class GameServices(val activity: Activity) {
    val audio = AudioService(activity)
    val imageStore = ImageStore(activity)
    val settings = SettingsRepository(activity)
    val voice = Voice(activity)
    val lang = LangState(this)

    /** Set by a game that wants the result of the system image picker. */
    var onImagePicked: ((android.net.Uri) -> Unit)? = null

    fun celebrate() = audio.playWin()

    /** Literki's prompts as praise / try again, in the current language. */
    fun praise(vararg before: String?) { val l = lang.lang; voice.say(*before.map { l to it }.toTypedArray(), l to "p.ok") }
    fun tryAgain() { voice.say(lang.lang to "p.try") }
    fun sayWord(id: String?) { voice.say(lang.lang to id) }
    fun text(id: String): String = lang.lang?.text(id) ?: ""
    val words: List<Literki.Word> get() = Literki.wordsWithPicture().filter { lang.lang?.has(it.id) != false }
}
