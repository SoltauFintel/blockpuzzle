package de.mwvb.blockpuzzle.gamepiece;

/**
 * INextGamePiece persistence
 */
public interface INextRound {

    /**
     * Set next round value and save it
     * @param val next round
     */
    void saveNextRound(int val);

    int getNextRound();
}
