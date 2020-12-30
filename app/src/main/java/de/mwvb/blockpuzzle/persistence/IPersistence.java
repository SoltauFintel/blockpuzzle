package de.mwvb.blockpuzzle.persistence;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public interface IPersistence {

    // GAME SPECIFIC ----

    void setGameID(IPlanet planet, int gameDefinitionIndex);
    /** gameDefinitionIndex: use selected game */
    void setGameID(IPlanet planet);
    void setGameID_oldGame();

    int loadScore();
    void saveScore(int punkte);
    int loadDelta();
    void saveDelta(int delta);
    int loadMoves();
    void saveMoves(int moves);
    boolean loadEmptyScreenBonusActive();
    void saveEmptyScreenBonusActive(boolean v);
    int loadHighScore();
    void saveHighScore(int punkte);
    int loadHighScoreMoves();
    void saveHighScoreMoves(int moves);
    void saveNextRound(int nextRound);
    int loadNextRound();
    /** Wenn true hat der Spieler es verkackt. */
    boolean loadGameOver();
    void saveGameOver(boolean gameOver);
    String loadDailyDate(IPlanet planet, int gameDefinitionIndex);
    /**
     * @param planet -
     * @param gameDefinitionIndex -
     * @param date Datum im Format JJJJ-MM-TT + Konstante DailyPlanet.ACTIVE_GAME oder DailyPlanet.WON_GAME
     */
    void saveDailyDate(IPlanet planet, int gameDefinitionIndex, String date);

    void load(PlayingField f);
    void save(PlayingField f);

    void load(GravitationData data);
    void save(GravitationData data);

    GamePiece load(int index);
    void save(int index, GamePiece p);

    void saveOwner(int score, int moves, String name);
    void clearOwner();
    String loadOwnerName();
    int loadOwnerScore();
    int loadOwnerMoves();

    // PLANT SPECIFIC ----

    void loadPlanet(IPlanet planet);
    void savePlanet(IPlanet planet);

    // GLOBAL DATA ----

    /**
     * @return never null or empty, generates random player name if none is stored
     */
    String loadPlayerName();
    /**
     * @param playername null or empty value won't be saved
     */
    void savePlayerName(String playername);
    boolean loadPlayernameEntered();
    void savePlayernameEntered(boolean v);
    boolean isGameSoundOn();
    void saveGameSound(boolean on);

    void saveCurrentPlanet(int clusterNumber, int planetNumber);
    int loadCurrentPlanet();
    int loadCurrentCluster();

    /**
     * @param v 0: no selection (show start screen), 1: old game, 2: Stone Wars game
     */
    void saveOldGame(int v);
    int loadOldGame();
    boolean isStoneWars();

    /** DELETE ALL DATA */
    void resetAll();
}
