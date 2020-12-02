package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess

class NewGameService {

    fun newGame(per: IPersistence) {
        val planet = PlanetAccess(per).planet
        per.setGameID(planet)
        per.saveScore(-1)
        per.saveMoves(0)
        planet.isOwner = false
        per.savePlanet(planet)
    }
}