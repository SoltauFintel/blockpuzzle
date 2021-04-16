package de.mwvb.blockpuzzle.game.stonewars;

import de.mwvb.blockpuzzle.game.GameEngine;
import de.mwvb.blockpuzzle.game.GameEngineModel;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;

/**
 * Stone Wars game engine
 */
public class StoneWarsGameEngine extends GameEngine {

    public StoneWarsGameEngine(GameEngineModel model) {
        super(model);
        GameDefinition definition = (GameDefinition) model.getDefinition();
        if (definition.getTerritoryName() == null) {
            model.getView().showPlanetNumber(myGS().getPlanet().getNumber());
        } else {
            model.getView().showTerritoryName(definition.getTerritoryName());
        }
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
            myGS().saveOwner(false); // owner is Orange Union or enemy
        }
    }

    private StoneWarsGameState myGS() {
        return (StoneWarsGameState) model.getGs();
    }
}
