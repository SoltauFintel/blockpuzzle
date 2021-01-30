package de.mwvb.blockpuzzle.game.stonewars;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.mwvb.blockpuzzle.game.GameEngine;
import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.ScorePlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.place.Check4VictoryPlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.place.StoneWarsScorePlaceAction;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Stone Wars game engine
 */
public class StoneWarsGameEngine extends GameEngine {

    public StoneWarsGameEngine(IGameView view, StoneWarsGameState gs) {
        super(view, gs);
    }

    protected final IPlanet getPlanet() {
        return ((StoneWarsGameState) gs).getPlanet();
    }
    protected final GameDefinition getDefinition() {
        return ((StoneWarsGameState) gs).getDefinition();
    }

    @Override
    public boolean isNewGameButtonVisible() {
        return false;
    }

    @Override
    protected INextGamePiece getNextGamePieceGenerator() {
        return new NextGamePieceFromSet(getDefinition().getGamePieceSetNumber(), gs);
    }

    @Override
    protected void initNextGamePieceForNewGame() {
        if (getPlanet().isNextGamePieceResetedForNewGame()) {
            nextGamePiece.reset();
        } else { // Daily Planet
            nextGamePiece.load();
        }
    }

    @Override
    protected void initPlayingField() {
        super.initPlayingField();
        getDefinition().fillStartPlayingField(playingField);
    }

    @Override
    protected void loadGame(boolean loadNextGamePiece, boolean checkGame) {
        super.loadGame(loadNextGamePiece, checkGame);
        if (gs.isGameOver()) {
            view.showScoreAndMoves(gs.get()); // display game over text
            playingField.gameOver();
        }
    }

    @Override
    protected void offer() {
        GamePlayState state = gs.get().getState();
        if (state == GamePlayState.PLAYING
                || (state == GamePlayState.WON_GAME && getDefinition().gameGoesOnAfterWonGame())) {
            super.offer();
        }
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

    @Override
    protected List<IPlaceAction> getPlaceActions() {
        List<IPlaceAction> list = super.getPlaceActions();
        list.add(new Check4VictoryPlaceAction());
        return list;
    }
    @NotNull
    @Override
    protected ScorePlaceAction getScorePlaceAction() {
        return new StoneWarsScorePlaceAction();
    }

    @NotNull
    @Override
    protected ClearRowsPlaceAction getClearRowsPlaceAction() {
        return new ClearRowsPlaceAction(getPlanet().getGravitation());
    }
}
