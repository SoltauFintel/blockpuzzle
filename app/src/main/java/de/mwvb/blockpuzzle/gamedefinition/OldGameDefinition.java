package de.mwvb.blockpuzzle.gamedefinition;

/**
 * Game definition for old game
 */
public class OldGameDefinition {

    public int getGravitationStartRow() {
        return 5;
    }

    public int getGamePieceBlocksScoreFactor() {
        return 1;
    }

    public int getHitsScoreFactor() {
        return 10;
    }

    public boolean isRowsAdditionalBonusEnabled() {
        return true;
    }

    public boolean gameGoesOnAfterWonGame() {
        return true;
    }
}
