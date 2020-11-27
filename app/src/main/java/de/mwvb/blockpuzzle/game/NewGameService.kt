package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.GameState

class NewGameService {

    fun newGame() {
        val per = GameState.persistence!!
        val planet = GameState.getPlanet()!!
        per.setGameID(planet)
        per.saveScore(-1)
        per.saveMoves(0)
        planet.isOwner = false
        per.savePlanet(planet)
    }
}