package de.mwvb.blockpuzzle.gamedefinition;

import org.jetbrains.annotations.NotNull;

/**
 * Als einzige Score zählt hier der One Color Bonus aus dem Classic Game.
 *
 * Special Blocks arbeiten wie gewohnt, sonst wäre das zu verwirrend.
 */
public class OneColorGameDefinition extends ClassicGameDefinition {

    public OneColorGameDefinition(int gamePieceSetNumber, int minimumLiberationScore) {
        super(gamePieceSetNumber, minimumLiberationScore);
    }

    @NotNull
    @Override
    public String toString() {
        return "OneColorGame(GPSN=" + getGamePieceSetNumber() + ",MLS=" + getMinimumLiberationScore() + ")";
    }

    @Override
    protected String getShortGameName() {
        return "One Color";
    }

    @Override
    public int getGamePieceBlocksScoreFactor() {
        return 0;
    }

    @Override
    public int getHitsScoreFactor() {
        return 0;
    }

    @Override
    public boolean isRowsAdditionalBonusEnabled() {
        return false;
    }
}
