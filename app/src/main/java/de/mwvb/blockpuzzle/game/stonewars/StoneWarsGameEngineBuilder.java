package de.mwvb.blockpuzzle.game.stonewars;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.mwvb.blockpuzzle.game.GameEngineBuilder;
import de.mwvb.blockpuzzle.game.GameEngineModel;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.place.Check4VictoryPlaceAction;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

/**
 * Initialization of StoneWarsGameEngine
 */
public class StoneWarsGameEngineBuilder extends GameEngineBuilder {

    // create data ----

    @NotNull
    @Override
    protected GameState createGameState() {
        return StoneWarsGameState.create();
    }

    @NotNull
    @Override
    protected GameDefinition provideDefinition() {
        return ((StoneWarsGameState) gs).getDefinition();
    }

    @Override
    protected List<IPlaceAction> createPlaceActions() {
        List<IPlaceAction> list = super.createPlaceActions();
        list.add(new Check4VictoryPlaceAction());
        return list;
    }

    @Override
    protected INextGamePiece getNextGamePieceGenerator() {
        return new NextGamePieceFromSet(((GameDefinition) definition).getGamePieceSetNumber(), gs);
    }

    @Override
    protected void initPlayingField(PlayingField playingField) {
        super.initPlayingField(playingField);
        ((GameDefinition) definition).fillStartPlayingField(playingField);
    }

    // create game engine ----

    @NotNull
    protected StoneWarsGameEngine createGameEngine(GameEngineModel model) {
        return new StoneWarsGameEngine(model);
    }

    // new game ----

    @Override
    protected void initNextGamePieceForNewGame() {
        if (((StoneWarsGameState) gs).getPlanet().isNextGamePieceResetedForNewGame()) {
            nextGamePiece.reset();
        } else { // Daily Planet
            nextGamePiece.load();
        }
    }

    // load game ----

    @Override
    protected void loadGame(boolean loadNextGamePiece, boolean checkGame) {
        super.loadGame(loadNextGamePiece, checkGame);
        if (gs.isLostGame()) {
            view.showScoreAndMoves(gs.get()); // display game over text
            playingField.gameOver();
        }
    }
}
