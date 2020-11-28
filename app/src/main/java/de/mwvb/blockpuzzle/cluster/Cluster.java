package de.mwvb.blockpuzzle.cluster;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.planet.IPlanet;

public class Cluster {
    private final int number;
    private final List<IPlanet> planets = new ArrayList<>();

    public Cluster(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public List<IPlanet> getPlanets() {
        return planets;
    }

    public static String getQuadrant(IPlanet p) {
        return getQuadrant(p.getX(), p.getY());
    }

    public static String getQuadrant(int x, int y) {
        String ret;
        if (x < 20) {
            if (y < 19) {
                ret = "c"; // gamma
            } else {
                ret = "a"; // alpha
            }
        } else {
            if (y < 19) {
                ret = "d"; // delta
            } else {
                ret = "ß"; // beta
            }
        }
        return ret;
    }
}
