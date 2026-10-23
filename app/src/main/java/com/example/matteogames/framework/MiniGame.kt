package com.example.matteogames.framework

import android.view.View

/** Static metadata shown on the home screen. */
data class MiniGameInfo(
    val id: String,
    val titleRes: Int,
    val emoji: String,
    /** Two-stop gradient (ARGB ints) that gives each game card its own color. */
    val gradient: IntArray,
)

/**
 * A minigame is a self-contained feature built from classic Views.
 * To add a new game: implement this interface and register it in [MiniGameRegistry].
 */
interface MiniGame {
    val info: MiniGameInfo

    /** Builds the full-screen view for this game. */
    fun createView(services: GameServices, onExit: () -> Unit): View
}
