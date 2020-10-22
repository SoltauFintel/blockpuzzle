package de.mwvb.blockpuzzle.logic.gamepiece;

/**
 * Next game piece strategy
 */
public interface INextGamePiece {

    GamePiece next(int score, BlockTypes blockTypes);
}
