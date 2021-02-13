package de.mwvb.blockpuzzle.game.stonewars.deathstar

import de.mwvb.blockpuzzle.gamestate.GamePlayState
import de.mwvb.blockpuzzle.gamestate.Spielstand
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO
import de.mwvb.blockpuzzle.global.GlobalData

// need Kotlin class to unite source code
class DeathStarReseter() {
    private val planet = MilkyWayCluster.get()

    /** Route catapulted player to Death Star */
    fun startGame() {
        resetGameState() // reset previous game state
        val gd = GlobalData.get()
        gd.currentPlanet = planet.number
        gd.todesstern = 1
        gd.todessternReaktor = 0
        gd.save()
    }

    /** Player gave up and is catapulted to Y galaxy */
    fun resetGame() {
        resetGameState()
        val gd = GlobalData.get()
        gd.currentPlanet = 1 // Spaceship is catapulted to planet 1 again.
        gd.todesstern = 0
        gd.todessternReaktor = 0
        gd.save()
    }

    private fun resetGameState() {
        val dao = SpielstandDAO()
        for (i in planet.gameDefinitions.indices) {
            val definition = planet.gameDefinitions[i] as DeathStarClassicGameDefinition

            val ss: Spielstand = dao.load(planet, i)
            ss.state = GamePlayState.PLAYING
            definition.isWon = false
            ss.unsetScore()
            ss.moves = 0
            ss.delta = 0
            ss.playingField = ""   // clear playing field
            ss.gamePieceViewP = "" // clear parking
            dao.save(planet, i, ss)
            if (i == 0) { // reset once is enough
                definition.writeNextRound(0, dao)
            }
        }

        planet.gameIndex = 0
    }
}