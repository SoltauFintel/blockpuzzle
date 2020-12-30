package de.mwvb.blockpuzzle.cluster;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.gamedefinition.LiberatedFeature;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Ich habe da Probleme mit dem Kotlin Syntax und muss das in Java machen.
 */
public class Cluster1Aufdeckungen {
    private final List<IPlanet> planets;

    public Cluster1Aufdeckungen(List<IPlanet> planets) {
        this.planets = planets;
    }

    public void aufdeckungen() {
        // nur Planet 1 sichtbar
        for (IPlanet p : planets) {
            p.setVisibleOnMap(p.getNumber() == 1);
        }

        // Planet 1 deckt Planet 2 auf
        finde(1).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf(2));

        // Planet 2 deckt Quadrant gamma auf
        finde(2).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("c"));

        // Planet 16 deckt Quadrant alpha auf
        finde(16).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("a"));

        // Planet 29 deckt Quadrant delta auf
        finde(29).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("d"));

        // Planet 39 deckt Quadrant beta auf
        finde(39).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("ß"));
    }

    @NotNull
    private LiberatedFeature deckeAuf(final int pNumber) {
        return new LiberatedFeature() {
            @Override
            public void start(IPersistence persistence) {
                IPlanet x = finde(pNumber);
                x.setVisibleOnMap(true);
                persistence.savePlanet(x);
            }
        };
    }

    private LiberatedFeature deckeAuf(final String quadrant) {
        return new LiberatedFeature() {
            @Override
            public void start(IPersistence persistence) {
                for (IPlanet x : getPlanets(quadrant)) {
                    x.setVisibleOnMap(true);
                    persistence.savePlanet(x);
                }
            }
        };
    }

    private IPlanet finde(int number) {
        for (IPlanet p : planets) {
            if (p.getNumber() == number) {
                return p;
            }
        }
        throw new RuntimeException("Planet #" + number + " not found!");
    }

    private List<IPlanet> getPlanets(String quadrant) {
        List<IPlanet> ret = new ArrayList<>();
        for (IPlanet p : planets) {
            if (Cluster.getQuadrant(p).equals(quadrant)) {
                ret.add(p);
            }
        }
        return ret;
    }

    // Data fixes
    public void fix(IPersistence persistence) {
        // Fixes for version 5.0: make one color planets visible for players that are already in alpha quadrant
        IPlanet aPlanetInAlphaQ = finde(30);
        if (aPlanetInAlphaQ.isVisibleOnMap()) {
            makeVisible(23, persistence);
            makeVisible(26, persistence);
        }

        // Fix for version 5.0: make daily planet visible for players that are already in delta quadrant
        IPlanet aPlanetInDeltaQ = finde(5);
        IPlanet dailyPlanet = finde(42);
        if (aPlanetInDeltaQ.isVisibleOnMap() && !dailyPlanet.isVisibleOnMap()) {
            dailyPlanet.setVisibleOnMap(true);
            persistence.savePlanet(dailyPlanet);
        }
    }

    private void makeVisible(int planetNumber, IPersistence persistence) {
        IPlanet planet = finde(planetNumber);
        if (!planet.isVisibleOnMap()) {
            planet.setVisibleOnMap(true);
            persistence.savePlanet(planet);
        }
    }
}
