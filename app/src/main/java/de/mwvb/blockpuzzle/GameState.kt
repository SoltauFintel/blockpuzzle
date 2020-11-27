package de.mwvb.blockpuzzle

import de.mwvb.blockpuzzle.cluster.Cluster
import de.mwvb.blockpuzzle.cluster.Cluster1
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.planet.IPlanet
import java.util.*

/**
 * Zentraler Spielzustand
 *
 * Singleton
 */
object GameState {
    var persistence : IPersistence? = null
    /* 0: start screen, 1: old game, 2: stone wars */
    private var oldGame = 0
    var galaxy = "Y"
    /** current star cluster in Upsilon galaxy */
    var cluster : Cluster = Cluster1
    /** aktuelle Position im aktuellen Cluster */
    private var planet : IPlanet? = null
    var playername : String = ""
    /** 0: play mode, 1: new game mode */
    var selectTerritoryMode = 0

    fun load() {
        oldGame = persistence!!.loadOldGame()
        cluster.planets.forEach { p -> persistence!!.loadPlanet(p) }

        val pn = persistence!!.loadCurrentPlanet()
        val result = cluster.planets.filter { p -> p.number == pn }
        if (result.size == 1) {
            planet = result[0]
        } else {
            planet = cluster.planets[0]
        }

        playername = persistence!!.loadPlayerName()
        if (playername == "") {
            val rand = Random(System.currentTimeMillis())
            playername = "Player_" + rand.nextInt(9999)
            persistence!!.savePlayerName(playername)
        }
    }

    fun save() {
        cluster.planets.forEach { p -> persistence!!.savePlanet(p) }
        persistence!!.saveCurrentPlanet(planet!!.clusterNumber, planet!!.number)
    }

    fun getPlanet(): IPlanet? {
        return planet
    }

    fun setPlanet(p : IPlanet) {
        planet = p
        persistence!!.saveCurrentPlanet(p.clusterNumber, p.number)
    }

    fun savePlayername(name: String) {
        playername = name
        persistence!!.savePlayerName(playername)
    }

    fun activateStoneWars() {
        setOldGame(2);
    }

    fun activateOldGame() {
        setOldGame(1);
    }

    fun quitGame() {
        setOldGame(0);
        save()
    }

    private fun setOldGame(v: Int) {
        oldGame = v
        persistence!!.saveOldGame(oldGame)
    }

    fun isOldGame(): Boolean {
        return oldGame == 1
    }

    fun isStoneWars(): Boolean {
        return oldGame == 2
    }
}
