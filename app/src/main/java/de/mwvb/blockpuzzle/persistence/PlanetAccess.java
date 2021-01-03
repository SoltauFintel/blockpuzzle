package de.mwvb.blockpuzzle.persistence;

import java.util.List;

import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.cluster.Cluster1;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;

/**
 * IPersistence helper especially for accessing the current planet.
 */
public class PlanetAccess {
    protected final IPersistence persistence;
    protected final Cluster cluster;
    protected IPlanet planet = null;

    /** should only instantiiated by PlanetAccessFactory */
    public PlanetAccess(IPersistence persistence, Cluster cluster) {
        this.persistence = persistence;
        this.cluster = cluster;
        determinePlanet();
    }

    protected void determinePlanet() {
        planet = null;
        int pn = persistence.loadCurrentPlanet();
        IPlanet first = null;
        for (ISpaceObject p : cluster.getSpaceObjects()) {
            persistence.loadPlanet(p); // loads VisibleOnMap and Owner
            if (p instanceof IPlanet) {
                if (p.getNumber() == pn) {
                    planet = (IPlanet) p;
                    // kein break
                }
                if (first == null) {
                    first = (IPlanet) p;
                }
            }
        }
        if (planet == null) {
            planet = first;
        }
    }

    public List<ISpaceObject> getSpaceObjects() {
        return cluster.getSpaceObjects();
    }

    public int getClusterNumber() {
        return cluster.getNumber();
    }

    public IPersistence getPersistence() {
        return persistence;
    }

    /**
     * @return aktueller Planet wo das Raumschiff gerade ist, nie null
     */
    public IPlanet getPlanet() {
        return planet;
    }

    /**
     * Planet setzen wo das Raumschiff aktuell ist
     * @param planet -
     */
    public void setPlanet(IPlanet planet) {
        if (planet == null) return;
        this.planet = planet;
        persistence.saveCurrentPlanet(cluster.getNumber(), planet.getNumber());
    }

    // Anwendungsfälle:
    // - visibleOnMap wurde für einige Planeten geändert
    // - Karte komplett aufdecken (Developer)
    public void savePlanets() {
        for (ISpaceObject planet : cluster.getSpaceObjects()) {
            persistence.savePlanet(planet);
        }
    }
}
