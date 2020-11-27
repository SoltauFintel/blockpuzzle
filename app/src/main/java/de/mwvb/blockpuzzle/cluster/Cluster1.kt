package de.mwvb.blockpuzzle.cluster

import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.gamedefinition.ClassicGameDefinition
import de.mwvb.blockpuzzle.gamedefinition.CleanerGameDefinition
import de.mwvb.blockpuzzle.planet.GiantPlanet
import de.mwvb.blockpuzzle.planet.Moon
import de.mwvb.blockpuzzle.planet.Planet

/**
 * Star cluster 1 of unknown Upsilon galaxy
 */
object Cluster1 : Cluster(1) {
    // TODO variable Gravitation bei Riesenplaneten und Monden, insbesondere Monde mit gar keiner Gravitation

    init {
        // GAMMA QUADRANT
        planets.add(Planet(1,6,5, ClassicGameDefinition(1, 1000))) // <- Startplanet in der Ypsilon Galaxie
        planets.add(Planet(2,3,4, ClassicGameDefinition(2, 2000)))
        planets.add(Planet(3,13,3, ClassicGameDefinition(11, 8000)))
        planets.add(Planet(4,17,5, ClassicGameDefinition(10, 10000)))
        planets.add(Planet(15,4,10, ClassicGameDefinition(3, 4000)))
        planets.add(getGiantPlanet1())
        planets.add(Planet(17,3,13, CleanerGameDefinition(4, 1)))
        planets.add(Planet(18,7,15, CleanerGameDefinition(5, 2)))
        planets.add(Planet(19,17,10, CleanerGameDefinition(7, 4)))
        planets.add(Planet(20,14,14, CleanerGameDefinition(6, 3)))
        planets.add(Moon(22,12,18, CleanerGameDefinition(9, 6)))
        planets.add(Moon(40,9, 11, 2, CleanerGameDefinition(8, 5)))

        // ALPHA QUADRANT
        planets.add(Planet(23,6,21, CleanerGameDefinition(19, 2)))
        planets.add(Planet(24,3,25, ClassicGameDefinition(20, 8000)))
        planets.add(Planet(25,11,24, ClassicGameDefinition(21, 10000)))
        planets.add(Planet(26,16,22, CleanerGameDefinition(22, 3)))
        planets.add(Planet(28,14,27, CleanerGameDefinition(23, 4)))
        planets.add(getGiantPlanet2())
        planets.add(Planet(31,6,35, ClassicGameDefinition(4, 40000)))
        planets.add(Planet(32,16,32, 4, ClassicGameDefinition(5, 6000)))
        planets.add(Moon(30,3,32, CleanerGameDefinition(14, 8, 200)))
        planets.add(Moon(41,7,27, 0, CleanerGameDefinition(15, 7)))

        // DELTA QUADRANT
        planets.add(Planet(5,23,6, ClassicGameDefinition(16)))
        planets.add(Planet(6,28,3, ClassicGameDefinition(24)))
        planets.add(Planet(7,34,4, 3, ClassicGameDefinition(25)))
        planets.add(Planet(8,31,6, ClassicGameDefinition(1)))
        planets.add(Moon(9,26,9, CleanerGameDefinition(26, 6)))
        planets.add(Planet(10,26,11, ClassicGameDefinition(26, 5000)))
        planets.add(Moon(11,27,13, CleanerGameDefinition(26, 6)))
        planets.add(Planet(21,34,12, CleanerGameDefinition(27, 5, 150))) // 114 Moves
        planets.add(GiantPlanet(39,20,16, 9, ClassicGameDefinition(37, 50000), null, null))

        // BETA QUADRANT
        planets.add(Planet(27,20,25, CleanerGameDefinition(28, 3))) // TODO
        planets.add(Moon(12,27,21, CleanerGameDefinition(28, 8)))
        planets.add(Planet(13,28,22, 6, CleanerGameDefinition(29, 7)))
        planets.add(Moon(14,29,23, CleanerGameDefinition(30, 9)))
        planets.add(Planet(33,24,30, CleanerGameDefinition(28, 4, 20))) // 12 Moves
        planets.add(Planet(34,34,21, CleanerGameDefinition(31, 5)))
        planets.add(Planet(35,32,27, CleanerGameDefinition(32, 6)))
        planets.add(getGiantPlanet3())
        planets.add(Planet(37,33,33, CleanerGameDefinition(33, 7, 200)))
        planets.add(Planet(38,25,36, CleanerGameDefinition(34, 8, 200)))

        //planets.filter { p -> p.number != 1 }.forEach { p -> p.isVisibleOnMap = false }
    }

    private fun getGiantPlanet1(): GiantPlanet {
        val gd1 = ClassicGameDefinition(12, 20000)
        gd1.territoryName = R.string.northernTerritory

        val gd2 = CleanerGameDefinition(13, 5)
        gd2.territoryName = R.string.southernTerritory

        return GiantPlanet(16, 12, 8, 9, gd1, gd2, null)
    }

    private fun getGiantPlanet2(): GiantPlanet {
        val brandenburg = ClassicGameDefinition(1, 30000)
        brandenburg.territoryName = R.string.brandenburg

        val saxony = ClassicGameDefinition(17, 30000)
        saxony.territoryName = R.string.saxony

        val lowerSaxony = ClassicGameDefinition(18, 30000)
        lowerSaxony.territoryName = R.string.lowerSaxony

        return GiantPlanet(29, 8, 31, brandenburg, saxony, lowerSaxony)
    }

    private fun getGiantPlanet3(): GiantPlanet {
        val gd1 = ClassicGameDefinition(35, 40000)
        gd1.territoryName = R.string.bayern

        val gd2 = ClassicGameDefinition(36, 40000)
        gd2.territoryName = R.string.luxemburg

        return GiantPlanet(36, 28, 32, 8, gd1, gd2, null)
    }
}