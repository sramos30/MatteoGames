package com.example.matteogames.core

import android.content.Context

/** Persisted settings / resume state (SharedPreferences; works on Android 4.4). */
class SettingsRepository(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /** Number of rows/columns in the jigsaw grid. Default 2 (very easy for toddlers). */
    var gridSize: Int
        get() = prefs.getInt("grid_size", DEFAULT_GRID)
        set(v) { prefs.edit().putInt("grid_size", v).apply() }

    /** Ids of puzzle pictures the child has already completed. */
    val completedPictures: Set<String>
        get() = HashSet(prefs.getStringSet("completed_pictures", emptySet()) ?: emptySet())

    fun addCompletedPicture(id: String) {
        prefs.edit().putStringSet("completed_pictures", completedPictures + id).apply()
    }

    var jigsawPicture: String
        get() = prefs.getString("jigsaw_picture", "") ?: ""
        set(v) { prefs.edit().putString("jigsaw_picture", v).apply() }

    /** Which pieces ("row,col") are already placed on the current puzzle. */
    var puzzlePlaced: Set<String>
        get() = HashSet(prefs.getStringSet("puzzle_placed", emptySet()) ?: emptySet())
        set(v) { prefs.edit().putStringSet("puzzle_placed", HashSet(v)).apply() }

    var numbersCurrent: Int
        get() = prefs.getInt("numbers_current", 1)
        set(v) { prefs.edit().putInt("numbers_current", v).apply() }

    /** Literki language id ("br", "pl"...) chosen with the language button. */
    var lang: String?
        get() = prefs.getString("lang", null)
        set(v) { prefs.edit().putString("lang", v).apply() }

    /** Letter id (cA...) the Letters game is on. */
    var lettersPos: String
        get() = prefs.getString("letters_id", "") ?: ""
        set(v) { prefs.edit().putString("letters_id", v).apply() }

    companion object {
        const val DEFAULT_GRID = 2
    }
}
