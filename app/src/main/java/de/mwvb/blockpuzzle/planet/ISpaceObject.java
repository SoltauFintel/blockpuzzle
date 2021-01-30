package de.mwvb.blockpuzzle.planet;

import android.graphics.Canvas;

import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.cluster.SpaceObjectStates;

public interface ISpaceObject {

    String getId();

    /**
     * @return positive unique space object number within star cluster
     */
    int getNumber();

    /**
     * @return positive unique star cluster number within Upsilon galaxy.
     * 0 stands for our sun system in the Milky Way galaxy.
     */
    int getClusterNumber();

    Cluster getCluster();

    void setCluster(Cluster cluster);

    /**
     * @return X coordinate from left border of cluster to the right, center of planet
     */
    int getX();

    /**
     * @return Y coordinate from top border of cluster to the bottom, center of planet
     */
    int getY();

    /**
     * @return 15, 20, 30 (not the same dimension as X/Y)
     */
    int getRadius();

    /**
     * @return true if can be clicked in map by player
     */
    boolean isSelectable();

    boolean isDataExchangeRelevant();

    boolean isShowCoordinates();

    /**
     * Implementation must check itself if it is visible. No drawing if it's not.
     * @param canvas -
     * @param f dp -> px factor
     * @param info access to PlanetState objects
     */
    void draw(Canvas canvas, float f, SpaceObjectStates info);

    /**
     * Returns type name of space object, e.g. Moon
     * @return resource constant
     */
    int getName();
}
