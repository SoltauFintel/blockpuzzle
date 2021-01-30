package de.mwvb.blockpuzzle.game.stonewars.place;

import de.mwvb.blockpuzzle.game.place.ScorePlaceAction;
import de.mwvb.blockpuzzle.gamestate.GameState;

/**
 * Stone Wars
 */
public class StoneWarsScorePlaceAction extends ScorePlaceAction {

    @Override
    protected int getGamePieceBlocksScoreFactor() {
        return info.getDefinition().getGamePieceBlocksScoreFactor();
    }

    @Override
    protected int getHitsScoreFactor() {
        return info.getDefinition().getHitsScoreFactor();
    }

    @Override
    protected void rowsAdditionalBonus(int xrows, int yrows, GameState gs) {
        if (info.getDefinition().isRowsAdditionalBonusEnabled()) {
            super.rowsAdditionalBonus(xrows, yrows, gs);
        }
    }
}
