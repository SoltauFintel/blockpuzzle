package de.mwvb.blockpuzzle.gamestate

import de.mwvb.blockpuzzle.deathstar.MilkyWayCluster
import de.mwvb.blockpuzzle.global.GlobalData

// TODO Entweder mehr Code hier rein oder Klasse auflösen.
class SpielstandService {
    private val dao = SpielstandDAO()

    fun startDeathStarGamePlay() {
        val planet = MilkyWayCluster.get() // Death Star planet

        val gd = GlobalData.get()
        gd.currentPlanet = planet.number
        gd.todesstern = 1
        gd.save()

        for (i in planet.gameDefinitions.indices) {
            val ss: Spielstand = dao.load(planet, i)
            ss.unsetScore()
            ss.playingField = "" // clear playing field
            ss.gamePieceViewP = "" // clear parking
            dao.save(planet, i, ss)
        }
    }
}