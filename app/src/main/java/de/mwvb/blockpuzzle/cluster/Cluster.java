package de.mwvb.blockpuzzle.cluster;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.planet.ISpaceObject;

public class Cluster {
    private final int number;
    private final List<ISpaceObject> spaceObjects = new ArrayList<>(); // most items are planets

    public Cluster(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getShortName() {
        return "" + number;
    }

    @SuppressWarnings("SameReturnValue")
    public String getGalaxyShortName() {
        return "Y";
    }

    public List<ISpaceObject> getSpaceObjects() {
        return spaceObjects;
    }

    public void add(ISpaceObject so) {
        so.setCluster(this);
        spaceObjects.add(so);
    }

    public static String getQuadrant(ISpaceObject p) {
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
