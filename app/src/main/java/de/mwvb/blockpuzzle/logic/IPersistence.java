package de.mwvb.blockpuzzle.logic;

import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.view.GamePieceView;

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
