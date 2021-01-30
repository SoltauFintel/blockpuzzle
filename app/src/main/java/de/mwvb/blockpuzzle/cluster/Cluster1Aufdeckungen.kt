package de.mwvb.blockpuzzle.cluster

import de.mwvb.blockpuzzle.gamedefinition.GameDefinition
import de.mwvb.blockpuzzle.global.Features
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.planet.ISpaceObject
import de.mwvb.blockpuzzle.planet.SpaceObjectStateService

/**
 * Ich habe da Probleme mit dem Kotlin Syntax und muss das in Java machen.
 */
class Cluster1Aufdeckungen(private val spaceObjects: List<ISpaceObject>) {

    fun aufdeckungen() {
        val sv = SpaceObjectStateService()

        // nur Planet 1 sichtbar
        sv.makeVisible(1)

        // Planet 1 deckt Planet 2 auf
        findFirstGameOfPlanet(1).setLiberatedFeature { sv.makeVisible(2) }

        // Planet 2 deckt Quadrant gamma auf
        findFirstGameOfPlanet(2).setLiberatedFeature { sv.makeVisible("c") }

        // Planet 16 deckt Quadrant alpha auf
        findFirstGameOfPlanet(16).setLiberatedFeature { sv.makeVisible("a") }

        // Planet 29 deckt Quadrant delta auf
        findFirstGameOfPlanet(29).setLiberatedFeature { sv.makeVisible("d") }

        // Planet 39 deckt Quadrant beta auf
        findFirstGameOfPlanet(39).setLiberatedFeature { sv.makeVisible("ß") }
    }

    private fun findFirstGameOfPlanet(number: Int): GameDefinition {
        val so = spaceObjects.filter { so -> so.number == number }[0]
        return (so as IPlanet).gameDefinitions[0]
    }

    // Data fixes
    fun fix() {
        // Make new space objects visible for players that are already in that quadrant.
        val sv = SpaceObjectStateService()
        val alpha = sv.isVisible(30)
        val beta = sv.isVisible(27)
        val delta = sv.isVisible(5)
        sv.makeVisible(23, alpha) // fix for v5.0 | one color
        sv.makeVisible(26, alpha) // fix for v5.0 | one color
        if (Features.deathStar) {
            sv.makeVisible(90, beta) // fix for v5.0 | space nebula
        }
        sv.makeVisible(34, beta) // fix for v5.0 | one color
        sv.makeVisible(42, delta) // fix for v5.0 | daily planet
    }
}