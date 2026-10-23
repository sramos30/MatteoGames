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
    /** End of a whole game (puzzle done, all pairs found): optional item, then "p.win". */
    fun win(vararg before: String?) { val l = lang.lang; voice.say(*before.map { l to it }.toTypedArray(), l to "p.win") }
    /** Say Literki's spoken title of a game (p.game.*). */
    fun sayTitle(gameId: String) { voice.say(lang.lang to titlePrompt(gameId)) }
    fun tryAgain() { voice.say(lang.lang to "p.try") }
    fun sayWord(id: String?) { voice.say(lang.lang to id) }
    fun text(id: String): String = lang.lang?.text(id) ?: ""
    /** Picture words for the games (color/shape/10 vocabulary excluded). */
    val words: List<Literki.Word> get() = Literki.wordsWithPicture().filter { it.id !in VOCAB && lang.lang?.has(it.id) != false }
}

/** Literki ids of the MatteoGames vocabulary (literki-estudio/docs/MATTEO-IDS.json). */
object Ids {
    const val RED = "w134"; const val BLUE = "w135"; const val GREEN = "w136"
    const val YELLOW = "w137"; const val PINK = "w138"; const val ORANGE = "w139"
    const val CIRCLE = "w140"; const val SQUARE = "w141"; const val TRIANGLE = "w142"; const val STAR = "w104"
    const val TEN = "w143"
}
/** Colors/shapes/10 are game vocabulary; the star (w104) stays a normal picture too. */
private val VOCAB = setOf(Ids.RED, Ids.BLUE, Ids.GREEN, Ids.YELLOW, Ids.PINK, Ids.ORANGE, Ids.CIRCLE, Ids.SQUARE, Ids.TRIANGLE, Ids.TEN)

fun titlePrompt(gameId: String) = "p.game." + (if (gameId == "tapballoon") "balloons" else gameId)
