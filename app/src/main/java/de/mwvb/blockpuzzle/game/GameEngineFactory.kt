package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.cluster.Cluster1
import de.mwvb.blockpuzzle.deathstar.DeathStarGameEngine
import de.mwvb.blockpuzzle.deathstar.MilkyWayCluster
import de.mwvb.blockpuzzle.game.stonewars.StoneWarsGameEngine
import de.mwvb.blockpuzzle.gamestate.GameState
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState
import de.mwvb.blockpuzzle.global.GameType
import de.mwvb.blockpuzzle.global.GlobalData
import de.mwvb.blockpuzzle.planet.IPlanet

class GameEngineFactory {

    fun create(view: IGameView): GameEngine {
        val gd = GlobalData.get()
        return if (gd.gameType == GameType.STONE_WARS) {
            val gs = StoneWarsGameState.create()
            if (gd.todesstern == 1) {
                DeathStarGameEngine(view, gs)
            } else {
                StoneWarsGameEngine(view, gs)
            }
        } else {
            GameEngine(view, GameState.create())
        }
    }

    fun getPlanet(): IPlanet {
        val gd = GlobalData.get()
        if  (gd.todesstern == 1) { // Death Star game active
            return MilkyWayCluster.get()
        }
        return Cluster1.spaceObjects.first { it.number == gd.currentPlanet } as IPlanet
    }
}
