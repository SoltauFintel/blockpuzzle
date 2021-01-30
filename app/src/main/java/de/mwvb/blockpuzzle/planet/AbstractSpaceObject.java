package de.mwvb.blockpuzzle.planet;

import de.mwvb.blockpuzzle.cluster.Cluster;

public abstract class AbstractSpaceObject implements ISpaceObject {
    private final int number;
    private final int x;
    private final int y;
    private Cluster cluster;

    public AbstractSpaceObject(int number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getClusterNumber() {
        return getCluster().getNumber();
    }

    @Override
    public Cluster getCluster() {
        return cluster;
    }

    @Override
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
