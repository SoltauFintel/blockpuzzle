package de.mwvb.blockpuzzle.gamedefinition

import de.mwvb.blockpuzzle.gamestate.Spielstand
import de.mwvb.blockpuzzle.playingfield.PlayingField

/**
 * SSLiberatedInfo: ILiberatedInfo initialized by Spielstand
 */
open class SSLiberatedInfo(val ss: Spielstand) : ILiberatedInfo {

    override fun getPlayer1Score(): Int {
        return ss.score
    }

    override fun getPlayer1Moves(): Int {
        return ss.moves
    }

    override fun getPlayer2Score(): Int {
        return ss.ownerScore
    }

    override fun getPlayer2Moves(): Int {
        return ss.ownerMoves
    }

    override fun isPlayerIsPlayer1(): Boolean {
        return true
    }

    override fun isPlayingFieldEmpty(): Boolean {
        return PlayingField.isEmpty(ss)
    }
}