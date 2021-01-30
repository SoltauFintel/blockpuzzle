package de.mwvb.blockpuzzle.planet;

import android.content.res.Resources;

import java.util.List;

import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;

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
    int getCurrentGameDefinitionIndex();
    /** Returns true if player must select territory. */
    boolean userMustSelectTerritory();
    /** @return true: set next game piece index to 0, false: load next game piece index */
    boolean isNextGamePieceResetedForNewGame();
    int getNewLiberationAttemptButtonTextResId();
    int getNewLiberationAttemptQuestionResId();

    /**
     * Returns planet and territory info. Plus game info. Used by bridge.
     * @param resources -
     * @return planet type, planet number, gravitation and territory name
     *         plus game info
     */
    String getInfo(Resources resources);
    /**
     * Returns game description with scores. Used by SelectTerritory and by getInfo().
     * @param resources -
     * @param gi -1 to take selected game.
     *           Otherwise it's the 0 based GameDefinition index.
     *           gi must not be used by implementation.
     * @return game type, scores of player, scores of owner, liberated by ... text
     */
    String getGameInfo(Resources resources, int gi);

    void resetGame();
}
