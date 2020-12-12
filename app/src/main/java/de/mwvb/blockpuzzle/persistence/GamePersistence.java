package de.mwvb.blockpuzzle.persistence;

import android.content.ContextWrapper;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Sicherstellen, dass der richtige Planet bzw. das richtige Game angesprochen wird. Die Persistence Implementierung ist da zu wackelig.
 * Ich verstehe noch nicht so richtig wie man Application State und Persistence bei Android macht.
 */
public class GamePersistence {
    private final IPersistence persistence;
    /** 0: non initialized mode, 1: old game, 2: Stone Wars */
    private int oldGame;
    /** only set in oldGame=2 mode */
    private IPlanet planet;

    public GamePersistence(IPersistence persistence, IGameView view) {
        this.persistence = persistence == null ? new Persistence((ContextWrapper) view) : persistence;
        oldGame = 0;
    }

    /** Direct access to IPersistence */
    @NotNull
    public IPersistence getPersistenceOK() {
        return persistence;
    }

    @NotNull
    public IPersistence get() {
        prepare("get");
        return persistence;
    }

    public void setGameID_oldGame() {
        persistence.setGameID_oldGame();
        oldGame = 1;
        planet = null;
    }

    @NotNull
    public IPlanet init4StoneWars() {
        planet = new PlanetAccess(persistence).getPlanet(); // load current planet
        persistence.setGameID(planet);
        oldGame = 2;
        return planet;
    }

    public IPlanet getPlanet() {
        return planet;
    }

    private void prepare(String caller) {
        if (oldGame == 1) {
            persistence.setGameID_oldGame();
        } else if (oldGame == 2) {
            persistence.setGameID(planet);
        } else {
            System.err.println("WARNING: GamePersistence in wrong mode! caller: " + caller);
        }
    }

    public int loadScore() {
        prepare("loadScore");
        return persistence.loadScore();
    }

    public void saveScore(int punkte) {
        prepare("saveScore");
        persistence.saveScore(punkte);
    }

    public void saveDelta(int v) {
        prepare("saveDelta");
        persistence.saveDelta(v);
    }

    public int loadDelta() {
        prepare("loadDelta");
        return persistence.loadDelta();
    }

    public int loadMoves() {
        prepare("loadMoves");
        return persistence.loadMoves();
    }

    public void saveMoves(int v) {
        prepare("saveMoves");
        persistence.saveMoves(v);
    }

    public void setOwnerToMe() {
        prepare("setOwnerToMe");
        persistence.clearOwner();
        planet.setOwner(true);
        persistence.savePlanet(planet);
    }

    public void gameOver() {
        prepare("gameOver");
        planet.setOwner(false);
        persistence.savePlanet(planet);
        persistence.saveGameOver(true);
    }

    public int loadOwnerScore() {
        prepare("loadOwnerScore");
        return persistence.loadOwnerScore();
    }

    public int loadOwnerMoves() {
        prepare("loadOwnerMoves");
        return persistence.loadOwnerMoves();
    }

    public boolean loadGameOver() {
        prepare("loadGameOver");
        return persistence.loadGameOver();
    }
}
