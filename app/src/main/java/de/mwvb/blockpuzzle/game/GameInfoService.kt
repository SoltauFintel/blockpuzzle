package de.mwvb.blockpuzzle.game

import android.content.res.Resources
import de.mwvb.blockpuzzle.GameState
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition
import de.mwvb.blockpuzzle.planet.IPlanet
import java.text.DecimalFormat

class GameInfoService {

    fun getGameInfo(resources: Resources): String {
        return if (GameState.getPlanet()!!.hasGames()) {
            getSelectedGameInfo(resources, GameState.getPlanet()!!.selectedGame)
        } else {
            resources.getString(R.string.planetNeedsNoLiberation)
        }
    }

    fun getSelectedGameInfo(resources: Resources, s: GameDefinition): String {
        var info = s.info // Game definition

        // Scores
        val planet = GameState.getPlanet()!!
        val per = GameState.persistence!!
        per.setGameID(planet, planet.gameDefinitions.indexOf(s))

        val score: Int = per.loadScore()
        val moves = per.loadMoves()
        if (score > 0) {
            info += "\n" + resources.getString(R.string.yourScoreYourMoves, thousand(score), thousand(moves))
        }

        val otherScore = per.loadOwnerScore()
        val otherMoves = per.loadOwnerMoves()
        if (otherScore > 0) {
            info += "\n" + resources.getString(R.string.scoreOfMoves, per.loadOwnerName(), thousand(otherScore), thousand(otherMoves))
        }

        // Liberated?
        if (s.isLiberated(score, moves, otherScore, otherMoves)) {
            if (planet.gameDefinitions.size == 1) {
                info += "\n" + resources.getString(R.string.liberatedPlanetByYou)
            } else {
                info += "\n" + resources.getString(R.string.liberatedTerritoryByYou)
            }
        }
        return info
    }

    private fun thousand(n: Int): String {
        return DecimalFormat("#,##0").format(n)
    }

    fun isPlanetFullyLiberated(planet: IPlanet): Boolean {
        val per = GameState.persistence!!
        val defs = planet.gameDefinitions
        for (i in 0 until defs.size) {
            per.setGameID(planet, i)
            if (!defs[i].isLiberated(per.loadScore(), per.loadMoves(), per.loadOwnerScore(), per.loadOwnerMoves())) {
                return false
            }
        }
        per.setGameID(planet) // set back to selected game
        return defs.size > 0
    }

    fun executeLiberationFeature(planet: IPlanet) {
        planet.gameDefinitions[0].featureOnLiberation?.start()
        GameState.save()
    }
}