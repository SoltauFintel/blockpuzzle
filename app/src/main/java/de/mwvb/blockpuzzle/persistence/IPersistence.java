package de.mwvb.blockpuzzle.persistence;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public interface IPersistence {

    int loadScore();
    void saveScore(int punkte);

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
}
