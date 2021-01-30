package de.mwvb.blockpuzzle.global.developer

import de.mwvb.blockpuzzle.cluster.Cluster1
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO
import de.mwvb.blockpuzzle.gamestate.TrophiesDAO
import de.mwvb.blockpuzzle.global.GlobalDataDAO
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.planet.SpaceObjectStateDAO
import kotlin.system.exitProcess

class DeveloperService {

    /** ACHTUNG! Löscht ALLE Daten. */
    fun resetAll() {
        val dao = SpielstandDAO()
        val sosDAO = SpaceObjectStateDAO()

        GlobalDataDAO().delete()
        DeveloperDataDAO().delete()
        dao.deleteOldGame()
        Cluster1.spaceObjects.forEach { so ->
            sosDAO.delete(so)

            if (so is IPlanet) {
                for (i in so.gameDefinitions.indices) {
                    dao.delete(so, i)
                }
            }
        }
        TrophiesDAO().delete(1)

        exitProcess(0)
    }

    fun saveToday(date: String) {
        val dd = DeveloperData.get()
        dd.today = date
        dd.save()
    }

    fun loadToday(): String? {
        return DeveloperData.get().today
    }
}