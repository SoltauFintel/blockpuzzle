package de.mwvb.blockpuzzle.persistence;

import de.mwvb.blockpuzzle.cluster.Cluster1;
import de.mwvb.blockpuzzle.deathstar.DeathStarPlanetAccess;

public class PlanetAccessFactory {

    public static PlanetAccess getPlanetAccess(IPersistence persistence) {
        if (persistence.loadDeathStarMode() == 1) { // Death Star game active
            return new DeathStarPlanetAccess(persistence);
        }

        return new PlanetAccess(persistence, Cluster1.INSTANCE);
    }
}
