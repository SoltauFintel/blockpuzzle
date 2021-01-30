package de.mwvb.blockpuzzle.game.stonewars.place;

import de.mwvb.blockpuzzle.game.place.PlaceInfo;
import de.mwvb.blockpuzzle.game.place.ScorePlaceAction;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;

/**
 * Stone Wars
 */
public class StoneWarsScorePlaceAction extends ScorePlaceAction {
    private GameDefinition definition;

    @Override
    public void perform(PlaceInfo info) {
        this.definition = ((StoneWarsGameState) info.getGs()).getDefinition();
        super.perform(info);
        this.definition = null;
    }

    @Override
    protected int getGamePieceBlocksScoreFactor() {
        return definition.getGamePieceBlocksScoreFactor();
    }

    @Override
    protected int getHitsScoreFactor() {
        return definition.getHitsScoreFactor();
    }

    @Override
    protected void rowsAdditionalBonus(int xrows, int yrows, GameState gs) {
        if (definition.isRowsAdditionalBonusEnabled()) {
            super.rowsAdditionalBonus(xrows, yrows, gs);
        }
    }
}
