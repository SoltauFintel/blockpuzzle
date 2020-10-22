package de.mwvb.blockpuzzle.logic;

import de.mwvb.blockpuzzle.logic.spielstein.BlockTypes;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;

/**
 * Next game piece strategy
 */
public interface INextGamePiece {

    GamePiece next(int score, BlockTypes blockTypes);
}
