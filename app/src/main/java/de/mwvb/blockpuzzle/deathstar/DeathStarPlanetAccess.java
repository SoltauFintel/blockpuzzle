package de.mwvb.blockpuzzle.deathstar;

import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.persistence.PlanetAccess;

public class DeathStarPlanetAccess extends PlanetAccess {

    /** should only instantiiated by PlanetAccessFactory */
    public DeathStarPlanetAccess(IPersistence persistence) {
        super(persistence, MilkyWayCluster.INSTANCE);
    }

    @Override
    protected void determinePlanet() {
        planet = ((MilkyWayCluster) cluster).get();
    }
}
