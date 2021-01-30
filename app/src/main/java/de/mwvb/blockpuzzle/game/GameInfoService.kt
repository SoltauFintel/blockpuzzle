package de.mwvb.blockpuzzle.game

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
}
