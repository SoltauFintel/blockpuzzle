package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.deathstar.DeathStarGame
import de.mwvb.blockpuzzle.persistence.IPersistence

class GameEngineFactory {

    fun create(view: IGameView, per : IPersistence): Game {
        val stoneWars = per.isStoneWars
        if (stoneWars) {
            val deathStarMode = per.loadDeathStarMode()
            if (deathStarMode == 1) {
                return DeathStarGame(view)
            } else {
                return StoneWarsGame(view)
            }
        } else {
            return Game(view)
        }
    }
}