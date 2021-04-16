package de.mwvb.blockpuzzle.game.stonewars;

import de.mwvb.blockpuzzle.game.GameEngine;
import de.mwvb.blockpuzzle.game.GameEngineModel;
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

    @Override
    protected boolean isHandleNoGamePiecesAllowed() {
        return true;
    }

    @Override
    public void onEndGame(boolean wonGame, boolean stopGame) {
        super.onEndGame(wonGame, stopGame);
        if (!wonGame) { // lost game
            ((StoneWarsGameState) gs).saveOwner(false); // owner is Orange Union or enemy
        }
    }
}
