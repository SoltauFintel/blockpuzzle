package de.mwvb.blockpuzzle.planet

import de.mwvb.blockpuzzle.cluster.Cluster
import de.mwvb.blockpuzzle.cluster.Cluster1

class SpaceObjectStateService {
    private val dao = SpaceObjectStateDAO()

    fun openMap() {
        Cluster1.spaceObjects.forEach { so ->
            val ps = dao.load(so)
            ps.isVisibleOnMap = true
            dao.save(so, ps)
        }
    }

    fun makeVisible(spaceObjectNumber: Int) {
        val so = find(spaceObjectNumber)
        val sos = dao.load(so)
        sos.isVisibleOnMap = true
        dao.save(so, sos)
    }

    fun makeVisible(quadrant: String) {
        Cluster1.spaceObjects.filter { so -> Cluster.getQuadrant(so) == quadrant }.forEach { so ->
            val sos = dao.load(so)
            sos.isVisibleOnMap = true
            dao.save(so, sos)
        }
    }

    fun makeVisible(number: Int, refIsVisible: Boolean) {
        if (refIsVisible) {
            val so = find(number)
            val sos = dao.load(so)
            if (!sos.isVisibleOnMap) {
                sos.isVisibleOnMap = true
                dao.save(so, sos)
            }
        }
    }

    fun isVisible(number: Int): Boolean {
        val so = find(number)
        return dao.load(so).isVisibleOnMap
    }

    private fun find(spaceObjectNumber: Int): ISpaceObject {
        return Cluster1.spaceObjects.filter { so -> so.number == spaceObjectNumber }[0]
    }

    fun saveOwner(planet: IPlanet, owner: Boolean) {
        val sos = dao.load(planet)
        sos.isOwner = owner
        dao.save(planet, sos)
    }
}
