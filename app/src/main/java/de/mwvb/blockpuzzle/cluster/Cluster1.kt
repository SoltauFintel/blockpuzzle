package de.mwvb.blockpuzzle.cluster

import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.game.stonewars.deathstar.SpaceNebula
import de.mwvb.blockpuzzle.gamedefinition.ClassicGameDefinition
import de.mwvb.blockpuzzle.gamedefinition.CleanerGameDefinition
import de.mwvb.blockpuzzle.gamedefinition.DailyClassicGameDefinition
import de.mwvb.blockpuzzle.gamedefinition.OneColorGameDefinition
import de.mwvb.blockpuzzle.global.Features
import de.mwvb.blockpuzzle.planet.*

/**
 * Star cluster 1 of unknown Upsilon galaxy
 */
object Cluster1 : Cluster(1) {

    init {
        // GAMMA QUADRANT
        add(Planet(1, 6, 5, ClassicGameDefinition(1, 1000))) // <- Startplanet in der Ypsilon Galaxie
        add(Planet(2, 3, 4, ClassicGameDefinition(2, 2000)))
        add(Planet(3, 13, 3, ClassicGameDefinition(11, 8000)))
        add(Planet(4, 17, 5, ClassicGameDefinition(10, 10000)))
        add(Planet(15, 4, 10, ClassicGameDefinition(3, 4000)))
        add(getGiantPlanet1())
        add(Planet(17, 3, 13, CleanerGameDefinition(4, 1))) // 13 Moves, 10 Moves
        add(Planet(18, 7, 15, CleanerGameDefinition(5, 2)))
        add(Planet(19, 17, 10, CleanerGameDefinition(7, 4, 60))) // 27 Moves
        add(Planet(20, 14, 14, CleanerGameDefinition(6, 3, 80))) // 37 Moves
        add(Moon(22, 12, 18, CleanerGameDefinition(9, 6))) // 64 Moves
        add(Moon(40, 9, 11, 2, CleanerGameDefinition(8, 5))) // 34 Moves

        // ALPHA QUADRANT
        add(Moon(23, 6, 21, 0, OneColorGameDefinition(19, 1000)))
        add(Planet(24, 3, 25, ClassicGameDefinition(20, 8000))) // GPSN Z20 is good for oneColor
        add(Planet(25, 11, 24, 3, ClassicGameDefinition(21, 8000)))
        add(Moon(26, 16, 22, 0, OneColorGameDefinition(22, 1000)))
        add(Planet(28, 14, 27, 6, CleanerGameDefinition(23, 5, 70))) // 49 Moves
        add(getGiantPlanet2())
        add(Planet(31, 6, 35, 7, ClassicGameDefinition(4, 40000)))
        add(Planet(32, 16, 32, 3, ClassicGameDefinition(5, 16000)))
        add(Moon(30, 3, 32, CleanerGameDefinition(14, 8)))  // reference planet for alpha quadrant
        add(Moon(41, 7, 27, 0, CleanerGameDefinition(15, 7, 130))) // 105 Moves

        // DELTA QUADRANT
        add(Planet(5, 23, 6, 6, ClassicGameDefinition(16, 10000))) // reference planet for delta quadrant
        add(Planet(6, 28, 3, 6, ClassicGameDefinition(24, 12000)))
        add(Planet(7, 34, 4, 4, ClassicGameDefinition(25, 7000)))
        add(Planet(8, 31, 6, ClassicGameDefinition(1, 16000)))
        add(Moon(9, 26, 9, 0, CleanerGameDefinition(26, 6)))
        add(Planet(10, 26, 11, 7, ClassicGameDefinition(26, 20000)))
        add(Moon(11, 27, 13, CleanerGameDefinition(26, 9)))
        add(Planet(21, 34, 12, CleanerGameDefinition(27, 5, 150))) // 114 Moves
        add(getGiantPlanet3())
        add(getDailyPlanet()) // new planet in version 5.0

        // BETA QUADRANT
        add(Planet(27, 20, 25, CleanerGameDefinition(37, 3))) // reference planet for delta quadrant
        add(Moon(12, 27, 21, 0, CleanerGameDefinition(38, 8)))
        add(Planet(13, 28, 22, 6, CleanerGameDefinition(29, 7, 200))) // 196 Moves
        add(Moon(14, 29, 23, CleanerGameDefinition(30, 9)))
        add(Planet(33, 24, 30, CleanerGameDefinition(28, 4, 20))) // 12 Moves
        add(Planet(34, 34, 21, OneColorGameDefinition(31, 2000)))
        add(Planet(35, 32, 27, CleanerGameDefinition(32, 6)))
        add(getGiantPlanet4())
        add(Planet(37, 33, 33, CleanerGameDefinition(33, 7, 200)))
        add(Planet(38, 25, 36, CleanerGameDefinition(34, 8, 200)))
        if (Features.deathStar) {
            add(SpaceNebula(90, 27, 28))
        }

        spaceObjects.forEach { i ->
            if (i is IPlanet && i.gameDefinitions[0] is ClassicGameDefinition && i.gravitation>5) {
                println("planet "+i.number + ", gravitation: " + i.gravitation + ", Quadrant: " + Cluster.getQuadrant(i) + "      "+i.javaClass)
            }
        }

        // AUFDECKUNGEN
        Cluster1Aufdeckungen(spaceObjects).aufdeckungen()

        if (Features.developerMode) {
            add(Planet(99, 6, 7, ClassicGameDefinition(41, 2000))) // for testing game pieces, blocks and colors
        }
    }

    // first quadrant (gamma)
    private fun getGiantPlanet1(): GiantPlanet {
        val gd1 = ClassicGameDefinition(12, 20000)
        gd1.territoryName = R.string.northernTerritory

        val gd2 = ClassicGameDefinition(13, 20000)
        gd2.territoryName = R.string.southernTerritory

        return GiantPlanet(16, 12, 8, 9, gd1, gd2, null)
    }

    // 2nd quadrant (alpha)
    private fun getGiantPlanet2(): GiantPlanet {
        val brandenburg = ClassicGameDefinition(1, 30000)
        brandenburg.territoryName = R.string.brandenburg

        val saxony = ClassicGameDefinition(17, 27000)
        saxony.territoryName = R.string.saxony

        val lowerSaxony = ClassicGameDefinition(18, 25000)
        lowerSaxony.territoryName = R.string.lowerSaxony

        return GiantPlanet(29, 8, 31, brandenburg, saxony, lowerSaxony)
    }

    // 3rd quadrant (delta)
    private fun getGiantPlanet3(): GiantPlanet {
        val gd1 = ClassicGameDefinition(39, 50000)
        gd1.territoryName = R.string.theBronx

        // and two quite easy territories:
        val gd2 = ClassicGameDefinition(18, 4000)
        gd2.territoryName = R.string.queens
        val gd3 = ClassicGameDefinition(17, 4000)
        gd3.territoryName = R.string.beverlyHills

        return GiantPlanet(39, 20, 16, 9, gd1, gd2, gd3)
    }

    // last planet (beta)
    private fun getGiantPlanet4(): GiantPlanet {
        val gd1 = ClassicGameDefinition(35, 40000)
        gd1.territoryName = R.string.luxemburg

        val gd2 = ClassicGameDefinition(36, 999000) // Ich setz erstmal die Score sehr hoch, damit das keiner schafft.
        gd2.territoryName = R.string.bayern

        // evtl. noch weiteres Territory "Gelre"

        return GiantPlanet(36, 28, 32, 8, gd1, gd2, null)
    }

    private fun getDailyPlanet(): Planet {
        val p = DailyPlanet(42, 34, 14, 6)
        for (day in 1..7) {
            p.add(DailyClassicGameDefinition(day))
        }
        p.gameDefinitions[0].territoryName = R.string.daily1
        p.gameDefinitions[1].territoryName = R.string.daily2
        p.gameDefinitions[2].territoryName = R.string.daily3
        p.gameDefinitions[3].territoryName = R.string.daily4
        p.gameDefinitions[4].territoryName = R.string.daily5
        p.gameDefinitions[5].territoryName = R.string.daily6
        p.gameDefinitions[6].territoryName = R.string.daily7
        return p
    }
}