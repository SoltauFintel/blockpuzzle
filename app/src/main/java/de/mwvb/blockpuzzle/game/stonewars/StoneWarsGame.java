package de.mwvb.blockpuzzle.game.stonewars;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.mwvb.blockpuzzle.game.Game;
import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceInfo;
import de.mwvb.blockpuzzle.game.place.ScorePlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.place.Check4VictoryPlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.place.StoneWarsScorePlaceAction;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Stone Wars game engine
 */
public class StoneWarsGame extends Game { // TODO game.stonewars package machen

    public StoneWarsGame(IGameView view, StoneWarsGameState gs) {
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
//        } else {
            // TODO Dieser Part hier sollte nach GameDefinition verschoben werden bzw. der WON/LOST state sollte ja persistiert sein.
//            // calculate won [for classic game]
//            ScoreChangeInfo info = new ScoreChangeInfo((StoneWarsGameState) gs, view.getMessages()).forceCalculation();
//            MessageObjectWithGameState msg = getDefinition().scoreChanged(info);
//            if (msg.isWonGame()) {
//                gs.get().setState(GamePlayState.WON_GAME); // old code: set won = ...
//            }
//
//            // calculate game over [for cleaner game]
//            if (playingField.getFilled() == 0 && getDefinition().onEmptyPlayingField()) {
//                gs.get().setState(GamePlayState.WON_GAME); // old code: gameOver=true
//            }
        }
    }

    @Override
    protected void offer() {
        if (!gs.isGameOver() || getDefinition().offerNewGamePiecesAfterGameOver()) {
            super.offer();
        }
    }

    // keine Spielsteine mehr
    @Override
    protected void handleNoGamePieces() {
        new Check4VictoryPlaceAction().handleNoGamePieces((StoneWarsGameState) gs, this);
    }

    @Override
    public void onGameOver() {
        super.onGameOver();
        ((StoneWarsGameState) gs).saveOwner(false); // owner is Orange Union or enemy
    }

    @NotNull
    @Override
    protected PlaceInfo createInfo(int index, GamePiece gamePiece, QPosition pos) {
        PlaceInfo info = super.createInfo(index, gamePiece, pos);
        info.setDefinition(getDefinition());
        return info;
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

    @Override
    public boolean gameCanBeWon() {
        return getDefinition().gameCanBeWon();
    }
}
