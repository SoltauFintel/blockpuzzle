package de.mwvb.blockpuzzle.view

import de.mwvb.blockpuzzle.logic.Action
import de.mwvb.blockpuzzle.logic.FilledRows
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece

/**
 * Alle Zugriffe vom Game (Controller) auf die View Schicht
 */
interface IGameView {

    fun getWithGravityOption(): Boolean

    fun updateScore(score: Int, delta: Int, gameOver: Boolean)

    fun showMoves(moves: Int)

    fun clearRows(filledRows: FilledRows, action: Action?)

    fun rotatingModeOff()

    fun drawPlayingField()

    fun doesNotWork()

    fun getGamePieceView(index: Int): IGamePieceView;
}