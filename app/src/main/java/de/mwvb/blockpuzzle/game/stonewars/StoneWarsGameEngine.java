package de.mwvb.blockpuzzle.game.stonewars;

import de.mwvb.blockpuzzle.game.GameEngine;
import de.mwvb.blockpuzzle.game.GameEngineModel;
import de.mwvb.blockpuzzle.game.stonewars.place.Check4VictoryPlaceAction;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;

/**
 * Stone Wars game engine
 */
public class StoneWarsGameEngine extends GameEngine {

    public StoneWarsGameEngine(GameEngineModel model) {
        super(model);
    }

    @Override
    public boolean isNewGameButtonVisible() {
        return false;
    }

    // keine Spielsteine mehr
    @Override
    protected void handleNoGamePieces() {
        new Check4VictoryPlaceAction().handleNoGamePieces((StoneWarsGameState) gs, this);
    }

    @Override
    public void onLostGame() {
        super.onLostGame();
        ((StoneWarsGameState) gs).saveOwner(false); // owner is Orange Union or enemy
    }
}
