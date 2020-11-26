package de.mwvb.blockpuzzle

import de.mwvb.blockpuzzle.cluster.Cluster
import de.mwvb.blockpuzzle.cluster.Cluster1
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import de.mwvb.blockpuzzle.planet.IPlanet
import java.util.*

/**
 * Zentraler Spielzustand
 *
 * Singleton
 */
object GameState {
    var persistence : IPersistence? = null
    // TODO speichern auf welcher Activity ich bin ?
    /* 0: start screen, 1: old game, 2: stone wars */
    private var oldGame = 0
    var galaxy = "Y"
    /** current star cluster in Upsilon galaxy */
    var cluster : Cluster = Cluster1
    /** Spaceship position in current star cluster */
    var x: Int = 5
    var y: Int = 5
    /** falls ich auf einem Planeten bin oder im Orbit eines Planeten, so ist dieser hier gesetzt */
    private var planet : IPlanet? = null
    /** 0: auf Planet gelandet, 1: im Orbit, 2: stehend im Raum, 3: fliegend im Raum */
    var flightMode : Int = 2
    var playername : String = ""
    /** target (planet number of current cluster, -1=no target) */
    private var target : Int = -1
    /** Wie man auf die Planetenoberfläche gelangt ist. */
    var transportation = ""

    fun load() {
        oldGame = persistence!!.loadOldGame()
        cluster.planets.forEach { p -> persistence!!.loadPlanet(p) }
        x = persistence!!.loadSpacePositionX()
        y = persistence!!.loadSpacePositionY()
        flightMode = persistence!!.loadFlightMode()
        target = persistence!!.loadTarget()

        val result = cluster.planets.filter { p -> p.x == x && p.y == y }
        if (result.size == 1) {
            planet = result[0]
        } else {
            planet = null
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
        persistence!!.saveSpacePosition(x, y)
        persistence!!.saveFlightMode(flightMode)
    }

    fun getPlanet(): IPlanet? {
        return planet
    }

    fun setPlanet(p : IPlanet?, fm: Int) {
        flightMode = fm
        persistence!!.saveFlightMode(flightMode)
        if (p != null) {
            x = p.x
            y = p.y
            persistence!!.saveSpacePosition(x, y)
        }
        planet = p
    }

    fun travel(p : IPlanet) {
        setPlanet(p, 1)
        clearTarget()
    }

    fun saveFlightMode() {
        persistence!!.saveFlightMode(flightMode)
    }

    fun savePlayername(name: String) {
        playername = name
        persistence!!.savePlayerName(playername)
    }

    fun setOldGame(v: Int) {
        oldGame = v
        persistence!!.saveOldGame(oldGame)
    }

    fun isOldGame(): Boolean {
        return oldGame == 1
    }

    fun isStoneWars(): Boolean {
        return oldGame == 2
    }

    fun setTarget(planet : IPlanet) {
        target = planet.number
        persistence!!.saveTarget(target)
    }

    fun clearTarget() {
        target = -1
        persistence!!.saveTarget(target)
    }

    fun getTarget(): IPlanet? {
        if (target == -1) {
            return null
        }
        val list = cluster.planets.filter { p -> p.number == target }
        if (list.isEmpty()) {
            return null
        }
        return list[0]
    }

    fun getTargetPlanetNumber(): Int {
        return target
    }
}
