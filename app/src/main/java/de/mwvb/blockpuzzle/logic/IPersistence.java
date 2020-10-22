package de.mwvb.blockpuzzle.logic;

import de.mwvb.blockpuzzle.logic.gamepiece.GamePiece;

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
