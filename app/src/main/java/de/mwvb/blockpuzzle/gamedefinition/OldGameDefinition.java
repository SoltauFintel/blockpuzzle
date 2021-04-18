package de.mwvb.blockpuzzle.gamedefinition;

import de.mwvb.blockpuzzle.game.TopButtonMode;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.RandomGamePiece;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;

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

    public INextGamePiece createNextGamePieceGenerator(GameState gs) {
        return new RandomGamePiece();
    }

    public boolean isWonAfterNoGamePieces(Spielstand ss) {
        return true;
    }

    public TopButtonMode getTopButtonMode() {
        return TopButtonMode.NEW_GAME;
    }

    public boolean isCrushAllowed() {
        return false;
    }
}
