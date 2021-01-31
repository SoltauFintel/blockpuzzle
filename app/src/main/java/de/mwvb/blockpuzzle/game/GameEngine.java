package de.mwvb.blockpuzzle.game;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceInfo;
import de.mwvb.blockpuzzle.gamedefinition.OldGameDefinition;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

import static de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult.FITS_ROTATED;
import static de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult.NO_GAME_PIECE;
import static de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult.NO_SPACE;

/**
 * Block Puzzle game engine
 *
 * This is the old game and the base class for the Stone Wars game.
 */
public class GameEngine implements GameEngineInterface {
    protected final GameEngineModel model;
    protected final GameState gs; // for convenience
    protected final PlayingField playingField; // for convenience

    private boolean dragAllowed = true;

    public GameEngine(GameEngineModel model) {
        this.model = model;
        gs = model.getGs();
        playingField = model.getPlayingField();
    }

    public boolean isNewGameButtonVisible() {
        return true;
    }

    protected OldGameDefinition getDefinition() {
        return model.getDefinition();
    }

    // Show new game pieces ----

    /** 3 neue zufällige Spielsteine anzeigen */
    public void offer() { // old German method name: vorschlag
        if (offerAllowed()) {
            for (int i = 1; i <= 3; i++) {
                model.getHolders().get(i).setGamePiece(model.getNextGamePiece().next(model.getBlockTypes()));
            }

            if (!gs.isLostGame() && model.getHolders().is123Empty()) {
                // Ein etwaiger letzter geparkter Stein wird aus dem Spiel genommen, da dieser zur Vereinfachung keine Rolle mehr spielen soll.
                // Mag vorteilhaft oder unvorteilhaft sein, aber ich definier die Spielregeln einfach so!
                // Vorteilhaft weil man mit dem letzten Stein noch mehr Punkte als der Gegner bekommen könnte.
                // Unvorteilhaft weil man mit dem letzten Stein noch ein Spielfeld-voll-Game-over erzielen könnte.
                model.getHolders().clearParking();

                // Wenn alle Spielsteine aufgebraucht sind, ist Spielende.
                model.getView().getMessages().getNoMoreGamePieces().show();
                handleNoGamePieces();
                onLostGame();
            }
        }
    }

    protected boolean offerAllowed() {
        GamePlayState state = gs.get().getState();
        return (state == GamePlayState.PLAYING
                || (state == GamePlayState.WON_GAME && getDefinition().gameGoesOnAfterWonGame()));
    }

    protected void handleNoGamePieces() { // Template method
        // Keine Spielsteine mehr, kann hier nicht passieren, da die Spielsteine endlos per Zufall generiert werden.
    }

    // Spielaktionen ----

    /**
     * Drop Aktion für Spielfeld oder Parking
     *
     * Throws DoesNotWorkException
     */
    public void dispatch(boolean targetIsParking, int index, GamePiece teil, QPosition xy) {
        if (gs.isLostGame()) {
            return;
        }
        boolean ret;
        if (targetIsParking) {
            ret = model.getHolders().park(index); // Drop Aktion für Parking Area
        } else {
            ret = place(index, teil, xy);
        }
        if (ret) {
            postDispatch();
        } else {
            throw new DoesNotWorkException();
        }
    }

    protected void postDispatch() {
        if (model.getHolders().is123Empty()) {
            offer();
        }
        checkGame();
        save();
    }

    /**
     * Drop action for playing field
     * @param index game piece holder index
     * @param gamePiece game piece to place to playing field
     * @param pos coordinates in the playing field where the user wants the game piece to be placed
     * @return true if game piece has been placed, false if that is not possible
     */
    private boolean place(int index, GamePiece gamePiece, QPosition pos) {
        // Move possible? ----
        if (!playingField.match(gamePiece, pos)) {
            return false;
        }

        // Remember old score ----
        final Spielstand ss = gs.get();
        final int scoreBefore = ss.getScore();

        // Main placing part ----
        playingField.place(gamePiece, pos);
        model.getHolders().get(index).setGamePiece(null);

        // Actions ----
        PlaceInfo info = createPlaceInfo(index, gamePiece, pos);
        for (IPlaceAction action : model.getPlaceActions()) {
            action.perform(info);
        }

        // Display and save ----
        ss.setDelta(ss.getScore() - scoreBefore);
        model.getView().showScoreAndMoves(ss);
        gs.save();
        return true;
    }

    @NotNull
    protected PlaceInfo createPlaceInfo(int index, GamePiece gamePiece, QPosition pos) {
        return new PlaceInfo(index, gamePiece, pos, playingField.getFilledRows(), model, this);
    }

    /** Player has shaked smartphone */
    public void shaked() {
        for (IPlaceAction action : model.getPlaceActions()) {
            if (action instanceof ClearRowsPlaceAction) {
                ((ClearRowsPlaceAction) action).executeGravitation(model.getGravitation(),
                        this, playingField, getDefinition().getGravitationStartRow());
                model.getView().shake();
                return;
            }
        }
        throw new RuntimeException("Missing ClearRowsPlaceAction");
    }

    /** Check game: player lost game if no game piece can be moved into the playing field */
    public void checkGame() {
        if (checkIfNoMoveIsPossible()) {
            onLostGame();
        }
    }

    @Override
    public boolean checkIfNoMoveIsPossible() {
        // All methods must be executed!
        boolean a = moveImpossible(1);
        boolean b = moveImpossible(2);
        boolean c = moveImpossible(3);
        boolean d = moveImpossible(-1);

        return a && b && c && d && !model.getHolders().isParkingFree();
    }

    private boolean moveImpossible(int index) {
        GamePiece gamePiece = model.getHolders().get(index).getGamePiece();
        GamePieceMatchResult result = playingField.match(gamePiece);
        if (result == NO_GAME_PIECE) {
            return true; // You cannot move game piece if there's no game piece.
        }
        boolean impossible = (result == NO_SPACE);
        model.getHolders().get(index).grey(impossible || (result == FITS_ROTATED));
        return impossible;
    }

    /** lost game, game over */
    @Override
    public void onLostGame() {
        Spielstand ss = gs.get();
        final GamePlayState oldState = ss.getState();
        ss.setState(GamePlayState.LOST_GAME); // old code: gameOver = true;
        gs.updateHighScore();
        ss.setDelta(0);
        gs.save();
        model.getView().showScoreAndMoves(ss); // display game over text
        playingField.gameOver();    // if park() has been the last action
        if (oldState != ss.getState()) { // play only if state has changed
            model.getView().playSound(4);
        }
    }

    public boolean lessScore() {
        return gs.get().getScore() < 10;
    }

    public boolean isLostGame() {
        return gs.isLostGame();
    }

    public void rotate(int index) {
        if (!gs.isLostGame()) {
            model.getHolders().get(index).rotate();
            moveImpossible(index);
        }
    }

    @Override
    public void save() {
        Spielstand ss = gs.get();
        playingField.save(ss);
        model.getGravitation().save(ss);
        model.getHolders().save(ss);
        gs.save();
    }

    public boolean isDragAllowed() {
        return dragAllowed;
    }

    public void setDragAllowed(boolean dragAllowed) {
        this.dragAllowed = dragAllowed;
    }

    @Override
    public void clearAllHolders() {
        model.getHolders().clearAll();
    }

    public int getBlocks() {
        return model.getBlocks();
    }
}
