package de.mwvb.blockpuzzle.gamestate

import de.mwvb.blockpuzzle.game.stonewars.deathstar.MilkyWayCluster
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition
import de.mwvb.blockpuzzle.global.GlobalData

// TODO Entweder mehr Code hier rein oder Klasse auflösen.
class SpielstandService {
    private val dao = SpielstandDAO()

    fun startDeathStarGamePlay() {
        val planet = MilkyWayCluster.get() // Death Star planet

        val gd = GlobalData.get()
        gd.currentPlanet = planet.number
        gd.todesstern = 1
        gd.save()

        for (i in planet.gameDefinitions.indices) {
            val ss: Spielstand = dao.load(planet, i)
            ss.unsetScore()
            ss.playingField = "" // clear playing field
            ss.gamePieceViewP = "" // clear parking
            dao.save(planet, i, ss)
        }
    }

    fun setSpielstandState(ss: Spielstand, state: GamePlayState, definition: GameDefinition) {
        if (state == GamePlayState.PLAYING) {
            ss.state = state // game goes on
        } else if (state == GamePlayState.LOST_GAME) {
            ss.state = state // game ends (e.g. playing field full)
        } else if (state == GamePlayState.WON_GAME) {
            if (definition.gameCanBeWon()) {
                ss.state = state // game ends (e.g. playing field empty in Cleaner game)
            } // else: game goes on
        } else {
            throw IllegalArgumentException("Illegal argument: $state")
        }
    }
    fun setSpielstandStatePlaying(ss: Spielstand) {
        ss.state = GamePlayState.PLAYING
    }
    fun setSpielstandStateLostGame(ss: Spielstand) {
        ss.state = GamePlayState.LOST_GAME
    }
}