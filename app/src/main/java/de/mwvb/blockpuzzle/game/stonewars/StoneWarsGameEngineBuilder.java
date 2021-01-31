package de.mwvb.blockpuzzle.game.stonewars;

import de.mwvb.blockpuzzle.game.GameEngineBuilder;
import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;

/**
 * Initialization of StoneWarsGameEngine
 */
public class StoneWarsGameEngineBuilder extends GameEngineBuilder {

    @Override
    public StoneWarsGameEngine build(IGameView view) {
        StoneWarsGameEngine game = new StoneWarsGameEngine(view, StoneWarsGameState.create());
        game.initGame();
        return game;
    }
}
