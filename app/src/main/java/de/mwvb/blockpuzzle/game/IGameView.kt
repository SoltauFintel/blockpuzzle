package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.gamepiece.IGamePieceView
import de.mwvb.blockpuzzle.playingfield.IPlayingFieldView

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