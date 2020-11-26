package de.mwvb.blockpuzzle.persistence;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public interface IPersistence {

    void setGameID(IPlanet planet, int gameDefinitionIndex);
    /** gameDefinitionIndex: use selected game */
    void setGameID(IPlanet planet);
    void setGameID_oldGame();

    int loadScore();
    void saveScore(int punkte);
    int loadDelta();
    void saveDelta(int delta);

    void load(PlayingField f);
    void save(PlayingField f);

    void load(GravitationData data);
    void save(GravitationData data);

    GamePiece load(int index);
    void save(int index, GamePiece p);

    int loadMoves();
    void saveMoves(int moves);

    int loadHighScore();
    void saveHighScore(int punkte);
    int loadHighScoreMoves();
    void saveHighScoreMoves(int moves);

    void saveOwner(int score, int moves, String name);
    void clearOwner();
    String loadOwnerName();
    int loadOwnerScore();
    int loadOwnerMoves();

    void saveSpacePosition(int x, int y);
    int loadSpacePositionX();
    int loadSpacePositionY();
    void loadPlanet(IPlanet planet);
    void savePlanet(IPlanet planet);
    int loadTarget();
    void saveTarget(int target);
    int loadFlightMode();
    void saveFlightMode(int val);
    String loadPlayerName();
    void savePlayerName(String playername);
    void saveOldGame(int v);
    int loadOldGame();
    void saveNextRound(int nextRound);
    int loadNextRound();
}
