package de.mwvb.blockpuzzle.gamepiece;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.persistence.IPersistence;

/**
 * Next game piece strategy
 */
public interface INextGamePiece {

    GamePiece next(BlockTypes blockTypes);

    /**
     * Der zuvor gelieferte GamePiece soll durch einen anderen ersetzt werden. Interne Counter sind aber nicht fortzuschreiben.
     * Muss nicht implementiert werden.
     */
    GamePiece getOther(BlockTypes blockTypes);

    void reset();

    void load();
}
