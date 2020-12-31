package de.mwvb.blockpuzzle.planet;

import java.util.List;

import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.persistence.IPersistence;

public interface IPlanet extends ISpaceObject {

    /**
     * @return gravitation class from 0 to 10
     */
    int getGravitation();

    List<GameDefinition> getGameDefinitions();
    GameDefinition getSelectedGame();
    void setSelectedGame(GameDefinition v);
    boolean hasGames();
    /** Returns index of select game definition. */
    int getCurrentGameDefinitionIndex(IPersistence persistence);
    /** Returns true if player must select territory. */
    boolean userMustSelectTerritory();
    /** @return true: set next game piece index to 0, false: load next game piece index */
    boolean isNextGamePieceResetedForNewGame();

    /**
     * @param lineNumber 1 - 3
     * @return navigation map bubble text for given line number
     */
    String getInfoText(int lineNumber);
}
