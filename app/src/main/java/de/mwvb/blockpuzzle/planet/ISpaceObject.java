package de.mwvb.blockpuzzle.planet;

import android.graphics.Canvas;

import de.mwvb.blockpuzzle.cluster.Cluster;

public interface ISpaceObject {

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
     * @param canvas -
     * @param f dp -> px factor
     */
    void draw(Canvas canvas, float f);

    // Ist eigentlich etwas heikel vom Design her, dass diese Entity fixe Daten und variable Daten enthält.

    boolean isVisibleOnMap();
    void setVisibleOnMap(boolean v);

    /**
     * @return true if current player is the Liberator ("owner") of this planet
     */
    boolean isOwner();
    void setOwner(boolean v);
}