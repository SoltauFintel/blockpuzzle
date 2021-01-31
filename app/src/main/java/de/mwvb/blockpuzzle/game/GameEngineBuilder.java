package de.mwvb.blockpuzzle.game;

import de.mwvb.blockpuzzle.gamestate.GameState;

/**
 * Initialization of GameEngine
 */
public class GameEngineBuilder {

    public GameEngine build(IGameView view) {
        GameEngine game = new GameEngine(view, GameState.create());
        game.initGame();
        return game;
    }
}
