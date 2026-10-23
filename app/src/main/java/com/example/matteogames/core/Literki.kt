package com.example.matteogames.core

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Content comes from Literki's installed language packages, through its read-only provider
 * (literki-app/docs/PROVEDOR-PACOTES.md). Languages, letters, digits, words, prompts, pictures
 * (xx.images, Noto SVG) and recorded/automatic voices (xx.rec over xx.tts). Same item ids
 * ("simbolo") in every language: cA, d3, w003, p.ok.
 */
object Literki {
    const val TAG = "MatteoLiterki"
    const val PKG = "com.literki.app"
    const val PERM = "com.literki.app.permission.LER_PACOTES"
    private val BASE: Uri = Uri.parse("content://com.literki.app.pacotes")

    enum class Status { LOADING, OK, NO_LITERKI, NO_PERMISSION, EMPTY }

    class Letter(val upper: String, val lower: String, val id: String, val example: String)
    class Digit(val value: Int, val text: String, val name: String, val id: String)
    class Sound(val pkg: String, val path: String)

    class Lang(
        val id: String,
        val label: String,
        val locale: Locale,
        val prompts: Map<String, String>,
        val letters: List<Letter>,
        val digits: Map<Int, Digit>,
        val words: Map<String, String>,
        val sounds: Map<String, Sound>,
    ) {
        /** Text of an item in this language, or null when the language doesn't have it. */
        fun text(id: String): String? = when {
            id.startsWith("p.") -> prompts[id]
            id.startsWith("d") -> digits.values.firstOrNull { it.id == id }?.let { it.name.ifEmpty { it.text } }
            id.startsWith("c") -> letters.firstOrNull { it.id == id }?.upper
            else -> words[id]
        }
        fun has(id: String) = text(id) != null
        /** Example word for a letter: the one chosen in the studio, else the first word starting with it. */
        fun exampleFor(l: Letter): String? {
            if (l.example.isNotEmpty() && words.containsKey(l.example)) return l.example
            return words.entries.sortedBy { it.key }.firstOrNull { it.value.startsWith(l.upper) }?.key
        }
    }

    class Word(val id: String, val emoji: String, val fx: Int) {
        /** language id -> "pkg/path" of the Noto SVG drawing from xx.images */
        val images = LinkedHashMap<String, String>()
    }

    @Volatile var status = Status.LOADING; private set
    @Volatile var langs: List<Lang> = emptyList(); private set
    @Volatile var words: List<Word> = emptyList(); private set
    @Volatile var packages: Map<String, Int> = emptyMap(); private set

    fun uri(pkg: String, path: String): Uri = BASE.buildUpon().appendPath("arquivo").appendPath(pkg).also { b ->
        path.split('/').forEach { b.appendPath(it) }
    }.build()

    private fun read(ctx: Context, pkg: String, path: String): String? = try {
        ctx.contentResolver.openInputStream(uri(pkg, path))?.use { it.readBytes().toString(Charsets.UTF_8) }
    } catch (e: Exception) {
        Log.w(TAG, "read $pkg/$path: ${e.message}"); null
    }

    fun localeFor(id: String): Locale = when (id) {
        "en" -> Locale.UK
        "br" -> Locale("pt", "BR")
        "pt" -> Locale("pt", "PT")
        else -> Locale(id)
    }

    /** Blocking: call off the UI thread. */
    @Synchronized
    fun load(ctx: Context): Status {
        status = try { doLoad(ctx) } catch (e: Exception) { Log.e(TAG, "load failed", e); Status.EMPTY }
        return status
    }

    private fun doLoad(ctx: Context): Status {
        try { ctx.packageManager.getPackageInfo(PKG, 0) } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "Literki not installed"); return Status.NO_LITERKI
        }
        if (ctx.checkCallingOrSelfPermission(PERM) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "permission $PERM not granted (reinstall MatteoGames after Literki)"); return Status.NO_PERMISSION
        }
        val pk = LinkedHashMap<String, Int>()
        val order = ArrayList<Pair<String, String>>()
        try {
            ctx.contentResolver.query(Uri.withAppendedPath(BASE, "lista"), null, null, null, null)?.use { c ->
                while (c.moveToNext()) pk[c.getString(0)] = c.getInt(1)
            }
            ctx.contentResolver.query(Uri.withAppendedPath(BASE, "linguas"), null, null, null, null)?.use { c ->
                val i = c.getColumnIndex("id"); val r = c.getColumnIndex("rotulo")
                while (c.moveToNext()) order.add(c.getString(i) to (if (r >= 0) c.getString(r) else c.getString(i).uppercase()))
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "provider denied: ${e.message}"); return Status.NO_PERMISSION
        }
        packages = pk
        Log.i(TAG, "/lista: ${pk.size} pacotes ${pk.keys}")
        Log.i(TAG, "/linguas: ${order.map { it.first }}")

        val ls = ArrayList<Lang>()
        val ws = LinkedHashMap<String, Word>()
        for ((id, label) in order) {
            val base = read(ctx, "$id.base", "base.json")?.let { JSONObject(it) } ?: continue
            val prompts = HashMap<String, String>()
            base.optJSONObject("prompts")?.let { p -> p.keys().forEach { k -> prompts[k] = p.getString(k) } }
            val letters = ArrayList<Letter>()
            base.optJSONArray("alfabeto")?.let { a ->
                for (i in 0 until a.length()) { val c = a.getJSONObject(i)
                    letters.add(Letter(c.getString("maiuscula"), c.optString("minuscula"), c.getString("simbolo"), c.optString("exemplo"))) }
            }
            val digits = LinkedHashMap<Int, Digit>()
            base.optJSONArray("digitos")?.let { a ->
                for (i in 0 until a.length()) { val c = a.getJSONObject(i)
                    val t = c.getString("texto"); val v = t.toIntOrNull() ?: continue
                    digits[v] = Digit(v, t, c.optString("nome"), c.getString("simbolo")) }
            }
            val words = LinkedHashMap<String, String>()
            if (pk.containsKey("$id.words")) read(ctx, "$id.words", "words.json")?.let { s ->
                val a = JSONArray(s)
                for (i in 0 until a.length()) { val c = a.getJSONObject(i)
                    val sid = c.getString("simbolo"); words[sid] = c.getString("texto")
                    val w = ws.getOrPut(sid) { Word(sid, c.optString("emoji"), c.optInt("fx")) }
                    if (w.emoji.isEmpty()) ws[sid] = Word(sid, c.optString("emoji"), c.optInt("fx")).also { it.images.putAll(w.images) }
                }
            }
            if (pk.containsKey("$id.images")) read(ctx, "$id.images", "imagens.json")?.let { s ->
                val o = JSONObject(s)
                o.keys().forEach { k -> ws[k]?.images?.put(id, "$id.images/" + o.getString(k)) }
            }
            val sounds = HashMap<String, Sound>()
            for (t in listOf("tts", "rec")) {   // rec over tts: the approved recording wins
                val p = "$id.$t"
                if (!pk.containsKey(p)) continue
                read(ctx, p, "sons.json")?.let { s -> val o = JSONObject(s); o.keys().forEach { k -> sounds[k] = Sound(p, o.getString(k)) } }
            }
            Log.i(TAG, "$id: ${letters.size} letras, ${digits.size} digitos, ${words.size} palavras, ${prompts.size} prompts, ${sounds.size} sons")
            val rec = if (pk.containsKey("$id.rec")) sounds.values.count { it.pkg == "$id.rec" } else 0
            Log.i(TAG, "$id som: palavras ${words.keys.count { it in sounds }}/${words.size}, letras ${letters.count { it.id in sounds }}/${letters.size}, " +
                "digitos ${digits.values.count { it.id in sounds }}/${digits.size}, prompts ${prompts.keys.count { it in sounds }}/${prompts.size}, rec $rec" +
                " | sem som: " + (words.keys.filter { it !in sounds } + prompts.keys.filter { it !in sounds }).take(40))
            ls.add(Lang(id, label, localeFor(id), prompts, letters, digits, words, sounds))
        }
        langs = ls
        words = ws.values.sortedBy { it.fx }
        Log.i(TAG, "catalogo: ${ls.size} linguas, ${words.size} palavras, ${words.count { it.images.isNotEmpty() }} com desenho")
        return if (ls.isEmpty()) Status.EMPTY else Status.OK
    }

    fun lang(id: String?): Lang? = langs.firstOrNull { it.id == id }

    /** The language after [cur] (in /linguas order) that has [item]; null when no other has it. */
    fun next(cur: Lang?, item: String?): Lang? {
        if (langs.isEmpty()) return null
        val start = langs.indexOf(cur)
        for (k in 1..langs.size) {
            val l = langs[(start + k + langs.size) % langs.size]
            if (l === cur) continue
            if (item == null || l.has(item)) return l
        }
        return null
    }

    fun wordsWithPicture(): List<Word> = words.filter { it.images.isNotEmpty() || it.emoji.isNotEmpty() }
}
