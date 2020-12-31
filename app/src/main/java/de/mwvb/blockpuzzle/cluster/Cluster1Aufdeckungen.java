package de.mwvb.blockpuzzle.cluster;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.gamedefinition.LiberatedFeature;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;

/**
 * Ich habe da Probleme mit dem Kotlin Syntax und muss das in Java machen.
 */
public class Cluster1Aufdeckungen {
    private final List<ISpaceObject> planets;

    public Cluster1Aufdeckungen(List<ISpaceObject> planets) {
        this.planets = planets;
    }

    public void aufdeckungen() {
        // nur Planet 1 sichtbar
        for (ISpaceObject p : planets) {
            p.setVisibleOnMap(p.getNumber() == 1);
        }

        // Planet 1 deckt Planet 2 auf
        findePlanet(1).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf(2));

        // Planet 2 deckt Quadrant gamma auf
        findePlanet(2).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("c"));

        // Planet 16 deckt Quadrant alpha auf
        findePlanet(16).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("a"));

        // Planet 29 deckt Quadrant delta auf
        findePlanet(29).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("d"));

        // Planet 39 deckt Quadrant beta auf
        findePlanet(39).getGameDefinitions().get(0).setLiberatedFeature(deckeAuf("ß"));
    }

    @NotNull
    private LiberatedFeature deckeAuf(final int pNumber) {
        return new LiberatedFeature() {
            @Override
            public void start(IPersistence persistence) {
                ISpaceObject x = finde(pNumber);
                x.setVisibleOnMap(true);
                persistence.savePlanet(x);
            }
        };
    }

    private LiberatedFeature deckeAuf(final String quadrant) {
        return new LiberatedFeature() {
            @Override
            public void start(IPersistence persistence) {
                for (ISpaceObject x : getPlanets(quadrant)) {
                    x.setVisibleOnMap(true);
                    persistence.savePlanet(x);
                }
            }
        };
    }

    private ISpaceObject finde(int number) {
        for (ISpaceObject p : planets) {
            if (p.getNumber() == number) {
                return p;
            }
        }
        throw new RuntimeException("Planet #" + number + " not found!");
    }

    private IPlanet findePlanet(int number) {
        return (IPlanet) finde(number);
    }

    private List<ISpaceObject> getPlanets(String quadrant) {
        List<ISpaceObject> ret = new ArrayList<>();
        for (ISpaceObject p : planets) {
            if (Cluster.getQuadrant(p).equals(quadrant)) {
                ret.add(p);
            }
        }
        return ret;
    }

    // Data fixes
    public void fix(IPersistence per) {
        // Make new space objects visible for players that are already in that quadrant.
        ISpaceObject alpha = finde(30);
        ISpaceObject beta = finde(27);
        ISpaceObject delta = finde(5);

        makeVisible(23, per, alpha); // fix for v5.0 | one color
        makeVisible(26, per, alpha); // fix for v5.0 | one color

        makeVisible(90, per, beta);  // fix for v5.0 | space nebula
        makeVisible(34, per, beta);  // fix for v5.0 | one color

        makeVisible(42, per, delta); // fix for v5.0 | daily planet
    }

    private void makeVisible(int number, IPersistence per, ISpaceObject ref) {
        if (ref.isVisibleOnMap()) {
            ISpaceObject so = finde(number);
            if (!so.isVisibleOnMap()) {
                so.setVisibleOnMap(true);
                per.savePlanet(so);
            }
        }
    }
}
