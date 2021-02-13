package de.mwvb.blockpuzzle.gamestate;

import androidx.annotation.NonNull;

import de.mwvb.blockpuzzle.global.AbstractDAO;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;

/**
 * Game state DAO
 */
public final class SpielstandDAO extends AbstractDAO<Spielstand> {
    private static final String OLD_GAME_ID = "OLD_GAME";

    /**
     * Returns game state for selected game
     *
     * @param planet -
     * @return game state
     */
    @NonNull
    public Spielstand load(IPlanet planet) {
        return load(planet, planet.getGameDefinitions().indexOf(planet.getSelectedGame()));
    }

    /**
     * @param spaceObject -
     * @param gameDefinitionIndex 0 based
     * @return game state
     */
    @NonNull
    public Spielstand load(ISpaceObject spaceObject, int gameDefinitionIndex) {
        return load(buildId(spaceObject, gameDefinitionIndex));
    }

    public void save(ISpaceObject spaceObject, int gameDefinitionIndex, Spielstand ss) {
        save(buildId(spaceObject, gameDefinitionIndex), ss);
    }

    public void delete(ISpaceObject spaceObject, int gameDefinitionIndex) {
        delete(buildId(spaceObject, gameDefinitionIndex));
    }

    private String buildId(ISpaceObject planet, int gameDefinitionIndex) {
        if (gameDefinitionIndex < 0) {
            throw new RuntimeException("Tried to access Spielstand with illegal game definition index: " + gameDefinitionIndex);
        }
        return planet.getId() + "_" + gameDefinitionIndex;
    }

    @NonNull
    public Spielstand loadOldGame() {
        return load(OLD_GAME_ID);
    }

    public void saveOldGame(Spielstand ss) {
        save(OLD_GAME_ID, ss);
    }

    public void deleteOldGame() {
        delete(OLD_GAME_ID);
    }

    @Override
    protected Class<Spielstand> getTClass() {
        return Spielstand.class;
    }
}
