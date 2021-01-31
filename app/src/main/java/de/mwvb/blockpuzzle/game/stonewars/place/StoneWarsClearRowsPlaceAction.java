package de.mwvb.blockpuzzle.game.stonewars.place;

import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;

public class StoneWarsClearRowsPlaceAction extends ClearRowsPlaceAction {

    public StoneWarsClearRowsPlaceAction(int gravitationStartRow) {
        super(gravitationStartRow);
    }

    @Override
    protected void rowsAdditionalBonus(int xrows, int yrows, GameState gs) {
        if (((StoneWarsGameState) gs).getDefinition().isRowsAdditionalBonusEnabled()) {
            super.rowsAdditionalBonus(xrows, yrows, gs);
        }
    }
}
