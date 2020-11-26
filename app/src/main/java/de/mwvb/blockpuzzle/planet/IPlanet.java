package de.mwvb.blockpuzzle.planet;

import java.util.List;

import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;

public interface IPlanet {

    /**
     * @return unique planet number within star cluster
     */
    int getNumber();

    /**
     * @return unique star cluster number within Upsilon galaxy
     */
    int getClusterNumber();

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
     * @return gravitation class from 0 to 10
     */
    int getGravitation();

    List<GameDefinition> getGameDefinitions();
    GameDefinition getSelectedGame();
    void setSelectedGame(GameDefinition v);
    boolean hasGames();

    boolean isVisibleOnMap();
    void setVisibleOnMap(boolean v);

    /**
     * @return true if current player is the Liberator ("owner") of this planet
     */
    boolean isOwner();
    void setOwner(boolean v);

    String getInfoText(int lineNumber);
}
