package de.mwvb.blockpuzzle.game;

import java.util.HashMap;
import java.util.Map;

import de.mwvb.blockpuzzle.planet.IPlanet;

public class TestPersistence extends PersistenceNoOp {
    private final Map<String, TestGameState> games = new HashMap<>();
    private String currentGameID;

    @Override
    public void loadPlanet(IPlanet planet) {
        super.loadPlanet(planet);
        planet.setVisibleOnMap(true); // Karte aufgedeckt
    }

    @Override
    public int loadCurrentPlanet() {
        return 17;
    }

    @Override
    public void setGameID(IPlanet planet, int gameDefinitionIndex) {
        currentGameID = planet.getNumber() + "_" +  gameDefinitionIndex;
    }

    @Override
    public void saveScore(int punkte) {
        get().setScore(punkte);
    }

    @Override
    public void saveMoves(int moves) {
        get().setMoves(moves);
    }

    @Override
    public int loadMoves() {
        return get().getMoves();
    }

    @Override
    public int loadScore() {
        return get().getScore();
    }

    @Override
    public int loadOwnerScore() {
        return get().getOwnerScore();
    }

    @Override
    public String loadOwnerName() {
        return get().getOwnerName();
    }

    @Override
    public int loadOwnerMoves() {
        return get().getOwnerMoves();
    }

    @Override
    public void saveOwner(int score, int moves, String name) {
        get().setOwnerScore(score);
        get().setOwnerMoves(moves);
        get().setOwnerName(name);
    }

    private TestGameState get() {
        TestGameState ret = games.get(currentGameID);
        if (ret == null) {
            ret = new TestGameState();
            games.put(currentGameID, ret);
        }
        return ret;
    }
}
