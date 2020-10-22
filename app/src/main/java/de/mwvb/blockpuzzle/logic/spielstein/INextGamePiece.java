package de.mwvb.blockpuzzle.logic.spielstein;

/**
 * Next game piece strategy
 */
public interface INextGamePiece {

    GamePiece next(int score, BlockTypes blockTypes);
}
