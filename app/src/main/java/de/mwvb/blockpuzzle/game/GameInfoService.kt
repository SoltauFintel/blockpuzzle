package de.mwvb.blockpuzzle.game

import android.content.res.Resources
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.cluster.Cluster
import de.mwvb.blockpuzzle.deathstar.DeathStar
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess
import de.mwvb.blockpuzzle.planet.AbstractPlanet
import de.mwvb.blockpuzzle.planet.GiantPlanet
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.planet.Moon
import java.text.DecimalFormat

class GameInfoService {

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

    // for bridge activity
    fun getPositionInfo(pa: PlanetAccess, resources: Resources): String {
        val cluster = pa.planet.cluster
        var info = resources.getString(R.string.position) + ":   G=" + cluster.galaxyShortName + "  C=" + cluster.shortName +
                "  Q=" + Cluster.getQuadrant(pa.planet)
        if (pa.planet.isShowCoordinates) {
            info += "  X=" + pa.planet.x + "  Y=" + pa.planet.y
        }
        info += "\n" + pa.planet.getInfo(pa.persistence, resources) +
                "\n" + pa.planet.getGameInfo(pa.persistence, resources, -1);
        return info
    }
}