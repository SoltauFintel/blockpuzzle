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
     * @param planet -
     * @return game state
     */
    @NonNull
    public Spielstand load(IPlanet planet) {
        return load(planet, planet.getGameDefinitions().indexOf(planet.getSelectedGame()));
    }

    /**
     * @param planet -
     * @param gameDefinitionIndex 0 based
     * @return game state
     */
    @NonNull
    public Spielstand load(IPlanet planet, int gameDefinitionIndex) {
        return load(planet.getId() + "_" + gameDefinitionIndex);
    }

    public void save(ISpaceObject spaceObject, int index, Spielstand ss) {
        save(spaceObject.getId() + "_" + index, ss);
    }

    @NonNull
    public Spielstand loadOldGame() {
        return load(OLD_GAME_ID);
    }

    public void saveOldGame(Spielstand ss) {
        save(OLD_GAME_ID, ss);
    }

    public void delete(ISpaceObject spaceObject, int gameDefinitionIndex) {
        delete(spaceObject.getId() + "_" + gameDefinitionIndex);
    }

    public void deleteOldGame() {
        delete("");
    }

    @Override
    protected Class<Spielstand> getTClass() {
        return Spielstand.class;
    }
}
