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

    fun showScore(score: Int, delta: Int, gameOver: Boolean)

    fun showMoves(moves: Int)

    fun showToast(msg: String)

    fun rotatingModeOff()

    fun shake()

    fun playSound(number: Int)
}