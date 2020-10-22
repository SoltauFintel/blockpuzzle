package de.mwvb.blockpuzzle.view

import de.mwvb.blockpuzzle.logic.Action
import de.mwvb.blockpuzzle.logic.FilledRows
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece

/**
 * Alle Zugriffe vom Game (Controller) auf die View Schicht
 */
interface IGameView {

    // init phase
    fun getPlayingFieldView(): IPlayingFieldView

    // init phase (and internal use)
    fun getGamePieceView(index: Int): IGamePieceView

    /** Feature toggle. false: auto-gravity, true: player has to shake his phone to start gravity */
    fun getGravitySetting(): Boolean

    fun showScore(score: Int, delta: Int, gameOver: Boolean)

    fun showMoves(moves: Int)

    fun rotatingModeOff()
}