package de.mwvb.blockpuzzle.game

import android.content.res.Resources
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.cluster.Cluster
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO
import de.mwvb.blockpuzzle.planet.IPlanet

class GameInfoService {

    fun isPlanetFullyLiberated(planet: IPlanet): Boolean {
        val defs = planet.gameDefinitions
        val dao = SpielstandDAO()
        for (i in defs.indices) {
            val ss = dao.load(planet, i)
            if (!defs[i].isLiberated(ss.score, ss.moves, ss.ownerScore, ss.ownerMoves, true, planet, i)) {
                return false
            }
        }
        return defs.size > 0
    }

    fun executeLiberationFeature(planet: IPlanet) {
        planet.gameDefinitions[0].liberatedFeature?.start()
    }

    // for bridge activity
    fun getPositionInfo(planet : IPlanet, resources: Resources): String {
        val cluster = planet.cluster
        var info = resources.getString(R.string.position) + ":   G=" + cluster.galaxyShortName + "  C=" + cluster.shortName +
                "  Q=" + Cluster.getQuadrant(planet)
        if (planet.isShowCoordinates) {
            info += "  X=" + planet.x + "  Y=" + planet.y
        }
        info += "\n" + planet.getInfo(resources) +
                "\n" + planet.getGameInfo(resources, -1)
        return info
    }
}