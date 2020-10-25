package de.mwvb.blockpuzzle.gamepiece;

import de.mwvb.blockpuzzle.block.BlockTypes;

/**
 * Next game piece strategy
 */
public interface INextGamePiece {

    void ausduennen();

    GamePiece next(int score, BlockTypes blockTypes);
}
