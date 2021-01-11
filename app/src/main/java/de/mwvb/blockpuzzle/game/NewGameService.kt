package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.deathstar.DeathStar
import de.mwvb.blockpuzzle.deathstar.MilkyWayCluster
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.PlanetAccessFactory

class NewGameService {

    // TODO Den Code nach IPlanet verlagern. (Andererseits verfrachte ich zu viel Service-Code nach Entity-Klassen. Vielleicht könnte so Code [auch getInfo...]
    //      in einer Extra Klasse stehen, die nur von IPlanet emittiert wird. z.B. IPlanetService)
    fun resetGame(per: IPersistence) {
        val planet = PlanetAccessFactory.getPlanetAccess(per).planet
        if (planet is DeathStar) {
            for (i in 0 until planet.gameDefinitions.size) {
                per.setGameID(planet, i)
                per.saveScore(-9999)
                per.saveNextRound(0)
            }
            MilkyWayCluster.get().resetGame()
            per.saveDeathStarMode(0)
            per.saveDeathStarReactor(0)
            per.saveCurrentPlanet(1, 1) // Spaceship is catapulted to planet 1 again.
            // TODO Man könnte InfoAc anzeigen: "Roter Alarm. Captain, wir wurde erneut in die Y G. katapultiert. Wie konnte das erneut passieren? Hat jemand einen
            //      falschen Button gedrückt? ;-) Ein vollständiger Systemcheck wäre gut."
            return;
        }

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
        planet.isOwner = false
        per.savePlanet(planet)
    }
}