package de.mwvb.blockpuzzle.gamepiece

import de.mwvb.blockpuzzle.game.IGameView
import de.mwvb.blockpuzzle.gamestate.Spielstand

/**
 * 4 GamePieceHolder objects
 */
class Holders(view: IGameView) {
    private val holders = mapOf(
                 1 to GamePieceHolder(1),
                 2 to GamePieceHolder(2),
                 3 to GamePieceHolder(3),
                -1 to GamePieceHolder(-1)
    )

    fun get(index: Int): GamePieceHolder {
        return holders[index] ?: error("Illegal holder index")
    }

    init {
        holders.values.forEach { it.setView(view.getGamePieceView(it.index)) }
    }

    fun load(ss: Spielstand) {
        holders.values.forEach { it.load(ss) }
    }

    fun save(ss: Spielstand) {
        holders.values.forEach { it.save(ss) }
    }

    fun is123Empty(): Boolean {
        return get(1).gamePiece == null && get(2).gamePiece == null && get(3).gamePiece == null
    }

    fun clearParking() {
        get(-1).gamePiece = null
    }

    fun clearAll() {
        get(1).gamePiece = null
        get(2).gamePiece = null
        get(3).gamePiece = null
        clearParking()
    }

    fun isParkingFree(): Boolean {
        return get(-1).gamePiece == null
    }

    /** Drop Aktion für Parking Area */
    fun park(sourceIndex: Int): Boolean {
        if (sourceIndex != -1 && isParkingFree()) {
            val source = get(sourceIndex)
            get(-1).gamePiece = source.gamePiece // Parking belegen
            source.gamePiece = null // Source leeren
            return true
        }
        return false
    }
}
