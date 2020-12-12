package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess

class NewGameService {

    fun newGame(per: IPersistence) {
        val planet = PlanetAccess(per).planet
        per.setGameID(planet)
        var score = per.loadScore()
        if (score < 0 && per.loadMoves() == 0) {
            score--
            per.saveScore(score)
            if (score > -9999 && score <= -3) {
                per.clearOwner() // also clear enemy. It's for the case that the player thinks he has no chance to beat the enemy.
            }
        } else {
            per.saveScore(-1)
        }
        per.saveMoves(0)
        per.saveGameOver(false)
        planet.isOwner = false
        per.savePlanet(planet)
    }
}