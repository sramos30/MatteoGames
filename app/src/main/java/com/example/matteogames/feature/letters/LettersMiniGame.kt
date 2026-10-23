package com.example.matteogames.feature.letters

import android.view.View
import com.example.matteogames.R
import com.example.matteogames.core.EN
import com.example.matteogames.core.PL
import com.example.matteogames.core.PT
import com.example.matteogames.core.SpeechService
import com.example.matteogames.core.Word
import com.example.matteogames.feature.jigsaw.VOCAB
import com.example.matteogames.framework.*
import java.util.Locale

private class Lang(val flag: String, val locale: Locale, val pick: (Word) -> String)

private val LANGS = listOf(
    Lang("🇬🇧", EN) { it.en },
    Lang("🇵🇱", PL) { it.pl },
    Lang("🇧🇷", PT) { it.pt },
)

private fun lettersFor(lang: Lang): List<Char> =
    VOCAB.mapNotNull { lang.pick(it).firstOrNull()?.uppercaseChar() }
        .filter { it in 'A'..'Z' }.distinct().sorted()

private fun wordsFor(lang: Lang, letter: Char): List<Word> =
    VOCAB.filter { lang.pick(it).firstOrNull()?.uppercaseChar() == letter }.distinctBy { it.emoji }

/** Big letter + a word starting with it, in the selected language. */
class LettersMiniGame : MiniGame {
    override val info = MiniGameInfo("letters", R.string.game_letters, "🔤", intArrayOf(Pal.Tangerine, Pal.Coral))

    override fun createView(services: GameServices, onExit: () -> Unit): View {
        val ctx = services.activity
        val (root, content) = ctx.kidScreen(0xFFFFF0E4.toInt(), 0xFFFFE7EC.toInt(), onExit)
        var langIdx = services.settings.lettersLang.coerceIn(0, LANGS.size - 1)
        var pos = services.settings.lettersPos.coerceIn(0, (lettersFor(LANGS[langIdx]).size - 1).coerceAtLeast(0))

        val flagRow = ctx.hbox()
        content.add(flagRow)
        content.add(ctx.spacer(10))
        lateinit var showWord: (Boolean) -> Unit
        val big = ctx.kidButton(160, { labelSizeFrac = 0.62f; cornerDp = 32f }) { showWord(true) }
        content.add(big)
        content.add(ctx.spacer(10))
        val pic = ctx.emojiView("⭐", 84)
        content.add(pic)
        val wordLabel = ctx.label("", 30f)
        content.add(wordLabel)
        val others = ctx.vbox()
        content.add(others)
        content.add(ctx.spacer(10))
        val arrows = ctx.hbox()
        content.add(arrows)

        val flagButtons = LANGS.mapIndexed { idx, l ->
            ctx.circleButton(60, l.flag) { langIdx = idx; pos = 0; update() }.also { flagRow.add(it, 8) }
        }

        showWord = { speakIt ->
            val lang = LANGS[langIdx]
            val letters = lettersFor(lang)
            val letter = letters[pos.coerceIn(0, letters.size - 1)]
            val w = wordsFor(lang, letter).random()
            big.label = letter.toString()
            pic.emoji = w.emoji
            wordLabel.text = lang.pick(w)
            others.removeAllViews()
            LANGS.filterIndexed { i, _ -> i != langIdx }.forEach { l ->
                others.add(ctx.wordChip(l.flag, l.pick(w)) { services.speech.speak(l.pick(w), l.locale) }, 3)
            }
            if (speakIt) services.speech.speakSequence(listOf(
                SpeechService.Utterance(letter.toString(), lang.locale),
                SpeechService.Utterance(lang.pick(w), lang.locale),
            ))
        }

        fun refresh() {
            flagButtons.forEachIndexed { i, b -> b.fill = if (i == langIdx) Pal.Sunshine else 0xFFFFFFFF.toInt() }
            services.settings.setLetters(langIdx, pos)
            showWord(true)
        }
        update = ::refresh

        arrows.add(ctx.circleButton(76, arrow = Arrow.LEFT) {
            val size = lettersFor(LANGS[langIdx]).size; pos = (pos - 1 + size) % size; refresh()
        }, 12)
        arrows.add(ctx.circleButton(76, arrow = Arrow.RIGHT) {
            val size = lettersFor(LANGS[langIdx]).size; pos = (pos + 1) % size; refresh()
        }, 12)
        refresh()
        return root
    }

    private var update: () -> Unit = {}
}
