package com.example.matteogames.framework

import android.animation.ObjectAnimator
import android.content.Context
import com.example.matteogames.core.Literki

/** Current language (persisted in SharedPreferences) and the cycle button, like Literki's cycleLang(). */
class LangState(private val services: GameServices) {
    val lang: Literki.Lang?
        // the saved choice wins; the first language is only shown (never saved) while it is missing
        get() = Literki.lang(services.settings.lang) ?: Literki.langs.firstOrNull()

    /**
     * Go to the next installed language that has [item]. Returns the new language, or null
     * when no other language has it (the button shakes and beeps, nothing changes).
     */
    fun cycle(item: String?, button: KidButton?): Literki.Lang? {
        val n = Literki.next(lang, item)
        if (n == null) {
            services.audio.playTap()
            button?.let { ObjectAnimator.ofFloat(it, "translationX", 0f, -14f, 14f, -10f, 10f, 0f).setDuration(350).start() }
            return null
        }
        android.util.Log.i("MatteoLang", "cycle ${lang?.id} -> ${n.id} (item $item)")
        services.settings.lang = n.id
        Pictures.preferLang = n.id
        button?.label = n.label
        return n
    }
}

/** Pill button showing the language label (text, ASCII-safe: "BR", "PL"...), never a flag emoji. */
fun Context.langButton(services: GameServices, sizeU: Int = 56, item: () -> String?, onChanged: (Literki.Lang) -> Unit): KidButton {
    lateinit var b: KidButton
    b = kidButton(sizeU, {
        circle = true; fill = Pal.SkyBlue; labelColor = 0xFFFFFFFF.toInt(); labelSizeFrac = 0.36f
        label = services.lang.lang?.label ?: "?"
    }) { services.lang.cycle(item(), b)?.let(onChanged) }
    return b
}
