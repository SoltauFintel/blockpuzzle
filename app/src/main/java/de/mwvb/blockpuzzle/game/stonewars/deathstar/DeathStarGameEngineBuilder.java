package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.game.stonewars.StoneWarsGameEngineBuilder;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;

/**
 * Initialization of DeathStarGameEngine
 */
public class DeathStarGameEngineBuilder extends StoneWarsGameEngineBuilder {

    @Override
    public DeathStarGameEngine build(IGameView view) {
        DeathStarGameEngine game = new DeathStarGameEngine(view, StoneWarsGameState.create());
        game.initGame();
        return game;
    }
}
