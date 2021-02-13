package de.mwvb.blockpuzzle.game.stonewars.deathstar.place;

import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.game.stonewars.place.Check4VictoryPlaceAction;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;

public class DeathStarCheck4VictoryPlaceAction extends Check4VictoryPlaceAction {

    @Override
    protected void check4Liberation(GameEngineInterface game, StoneWarsGameState gs) {
        game.clearAllHolders(); // Spieler soll keine Spielsteine mehr setzen können. Das bewirkt außerdem auch, dass offer() aufgerufen wird und somit nextGame().
    }
}
