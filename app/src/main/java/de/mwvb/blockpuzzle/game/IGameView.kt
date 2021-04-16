package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.gamepiece.IGamePieceView
import de.mwvb.blockpuzzle.global.messages.MessageFactory
import de.mwvb.blockpuzzle.playingfield.Action
import de.mwvb.blockpuzzle.playingfield.IPlayingFieldView

/**
 * Alle Zugriffe vom Game (Controller) auf die View Schicht
 */
interface IGameView {

    // init phase
    fun getPlayingFieldView(): IPlayingFieldView

    // init phase (and internal use)
    fun getGamePieceView(index: Int): IGamePieceView

    fun showScore(text: String)

    fun showMoves(text: String)

    fun showPlanetNumber(number: Int)

    fun showTerritoryName(resId: Int)

    fun shake()

    fun playSound(number: Int)

    fun getSpecialAction(specialState: Int): Action

    fun getMessages(): MessageFactory
}