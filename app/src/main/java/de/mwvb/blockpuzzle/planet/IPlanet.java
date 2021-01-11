package de.mwvb.blockpuzzle.planet;

import android.content.res.Resources;

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
    int getNewLiberationAttemptButtonTextResId();
    int getNewLiberationAttemptQuestionResId();

    // TODO Namen der getInfo, getGameInfo und getInfoText Methoden nicht so aussagekräftig bzw. abgrenzend.
    /**
     * @param persistence -
     * @param resources -
     * @return planet type, planet number, gravitation and territory name
     */
    String getInfo(IPersistence persistence, Resources resources);

    /**
     * @param per -
     * @param resources -
     * @param gi -1 to take selected game.
     *                  Otherwise it's the 0 based GameDefinition index.
     *                  gameIndex must not be used by implementation.
     * @return game type, scores of player, scores of owner, liberated by ... text
     */
    String getGameInfo(IPersistence per, Resources resources, int gi);

    /**
     * @param lineNumber 1 - 3
     * @return navigation map bubble text for given line number
     */
    String getInfoText(int lineNumber);
}
