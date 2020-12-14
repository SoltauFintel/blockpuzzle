package de.mwvb.blockpuzzle.game

import android.content.res.Resources
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.cluster.Cluster
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess
import de.mwvb.blockpuzzle.planet.GiantPlanet
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.planet.Moon
import java.text.DecimalFormat

class GameInfoService {

    fun getGameInfo(pa: PlanetAccess, resources: Resources): String {
        return if (pa.planet.hasGames()) {
            getSelectedGameInfo(pa, resources, pa.planet.selectedGame)
        } else {
            resources.getString(R.string.planetNeedsNoLiberation)
        }
    }

    fun getSelectedGameInfo(pa: PlanetAccess, resources: Resources, s: GameDefinition): String {
        var info = s.info // Game definition

        // Scores
        val planet = pa.planet
        val per = pa.persistence
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
        if (s.isLiberated(score, moves, otherScore, otherMoves, per, true)) {
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

    fun isPlanetFullyLiberated(planet: IPlanet, per: IPersistence): Boolean {
        val defs = planet.gameDefinitions
        for (i in 0 until defs.size) {
            per.setGameID(planet, i)
            if (!defs[i].isLiberated(per.loadScore(), per.loadMoves(), per.loadOwnerScore(), per.loadOwnerMoves(), per, true)) {
                return false
            }
        }
        return defs.size > 0
    }

    fun executeLiberationFeature(planet: IPlanet, persistence: IPersistence) {
        planet.gameDefinitions[0].featureOnLiberation?.start(persistence)
    }

    // for Bridge Activity: Zeile 1
    public fun getPositionInfo(pa: PlanetAccess, resources: Resources): String {
        var info = resources.getString(R.string.position) + ":   G=" + pa.galaxy + "  C=" + pa.clusterNumber +
                "  Q=" + Cluster.getQuadrant(pa.planet) +
                "  X=" + pa.planet.x + "  Y=" + pa.planet.y
        info += "\n" + getPlanetInfo(pa, resources) + "\n" + getGameInfo(pa, resources)
        return info
    }

    // for Bridge Activity: Zeile 2
    private fun getPlanetInfo(pa: PlanetAccess, resources: Resources): String {
        val planet = pa.planet
        val planetType = when {
            planet is GiantPlanet -> resources.getString(R.string.giantPlanet)
            planet is Moon -> resources.getString(R.string.moon)
            else -> resources.getString(R.string.planet)
        }
        var info = planetType + " #" + planet.number + ", " + resources.getString(R.string.gravitation) + " " + planet.gravitation
        if (planet.gameDefinitions.size > 1) {
            info += "\n" + resources.getString(planet.selectedGame.territoryName)
        }
        return info
    }
}