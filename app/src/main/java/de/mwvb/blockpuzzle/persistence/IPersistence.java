package de.mwvb.blockpuzzle.persistence;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public interface IPersistence {

    void save(int index, GamePiece p);

    GamePiece load(int index);

    void save(PlayingField f);

    void load(PlayingField f);

    int loadScore();

    void saveScore(int punkte);

    int loadMoves();

    void saveMoves(int moves);
}
