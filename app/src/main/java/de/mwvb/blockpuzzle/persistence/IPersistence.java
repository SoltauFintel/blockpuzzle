package de.mwvb.blockpuzzle.persistence;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public interface IPersistence {

    int loadScore();
    void saveScore(int punkte);

    void load(PlayingField f);
    void save(PlayingField f);

    GamePiece load(int index);
    void save(int index, GamePiece p);

    int loadMoves();
    void saveMoves(int moves);
}
