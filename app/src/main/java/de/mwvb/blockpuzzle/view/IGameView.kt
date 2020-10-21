package de.mwvb.blockpuzzle.view

import de.mwvb.blockpuzzle.logic.Action
import de.mwvb.blockpuzzle.logic.FilledRows
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece

/**
 * Alle Zugriffe vom Game (Controller) auf die View Schicht
 */
interface IGameView {

    fun setGamePiece(index: Int, gamePiece: GamePiece?, write: Boolean)

    fun getGamePiece(index: Int): GamePiece?

    fun rotatingModeOff()


    fun drawPlayingField()

    fun restoreGamePieceViews()

    fun updateScore(score: Int)
    fun showMoves(moves: Int)

    fun doesNotWork()

    fun getWithGravityOption(): Boolean

    fun clearRows(filledRows: FilledRows, action: Action?)

    fun grey(index: Int, grey: Boolean)
}