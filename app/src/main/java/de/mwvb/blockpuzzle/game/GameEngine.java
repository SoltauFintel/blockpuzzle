package de.mwvb.blockpuzzle.game;

import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceActionModel;
import de.mwvb.blockpuzzle.gamedefinition.OldGameDefinition;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

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
    public boolean rebuild = false;

    public GameEngine(GameEngineModel model) {
        this.model = model;
        gs = model.getGs();
        playingField = model.getPlayingField();
    }

    // Game play actions (dispatch, rotate, shaked) ----

    /**
     * Drop action for playing field or parking area
     *
     * @param targetIsParking true: player moved game piece onto the parking area,
     *                        false: player moved game piece onto the playing field
     * @param dropActionModel data
     * @throws DoesNotWorkException if game piece can not be placed
     */
    public void dispatch(boolean targetIsParking, DropActionModel dropActionModel) {
        if (gs.isLostGame()) {
            return;
        }
        boolean ret;
        if (targetIsParking) {
            ret = model.getHolders().park(dropActionModel.getIndex()); // Drop action for parking area
        } else {
            ret = place(dropActionModel);
        }
        if (ret) {
            postDispatch();
        } else {
            throw new DoesNotWorkException();
        }
    }

    protected void postDispatch() {
        if (model.getHolders().is123Empty()) {
            offer(false);
        }
        checkGame();
        save();
    }

    /**
     * Drop action for playing field. Most important method of game.
     * @param dropActionModel drop action data
     * @return true if game piece has been placed, false if that is not possible
     */
    private boolean place(DropActionModel dropActionModel) {
        // Move possible? ----
        if (!playingField.match(dropActionModel.getGamePiece(), dropActionModel.getXy())) {
            return false;
        }

        // Remember old score ----
        final Spielstand ss = gs.get();
        final int scoreBefore = ss.getScore();

        // Main placing part ----
        playingField.place(dropActionModel.getGamePiece(), dropActionModel.getXy());
        model.getHolders().get(dropActionModel.getIndex()).setGamePiece(null);

        // Actions ----
        PlaceActionModel info = new PlaceActionModel(this, model, dropActionModel);
        for (IPlaceAction action : model.getPlaceActions()) {
            action.perform(info);
        }

        // Display and save ----
        ss.setDelta(ss.getScore() - scoreBefore);
        model.getView().showScoreAndMoves(ss);
        gs.save();
        return true;
    }

    public void rotate(int index) {
        if (!gs.isLostGame()) {
            model.getHolders().get(index).rotate();
            moveImpossible(index);
        }
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

    // Show new game pieces ----

    /** 3 neue zufällige Spielsteine anzeigen */
    public void offer(boolean newGameMode) { // old German method name: vorschlag
        if (offerAllowed(newGameMode)) {
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

    protected boolean offerAllowed(boolean newGameMode) {
        GamePlayState state = gs.get().getState();
        return (state == GamePlayState.PLAYING
                || (state == GamePlayState.WON_GAME && getDefinition().gameGoesOnAfterWonGame()));
    }

    protected void handleNoGamePieces() { // Template method
        // Keine Spielsteine mehr, kann hier nicht passieren, da die Spielsteine endlos per Zufall generiert werden.
    }

    // Game state checks ----

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

    @Override
    public void clearAllHolders() {
        model.getHolders().clearAll();
    }

    // Persistence ----

    @Override
    public void save() {
        model.save();
    }

    // Properties ----

    public boolean isNewGameButtonVisible() {
        return true;
    }

    protected OldGameDefinition getDefinition() {
        return model.getDefinition();
    }

    public int getBlocks() {
        return model.getBlocks();
    }

    public boolean lessScore() {
        return gs.get().getScore() < 10;
    }

    public boolean isLostGame() {
        return gs.isLostGame();
    }

    public boolean isDragAllowed() {
        return dragAllowed;
    }

    public void setDragAllowed(boolean dragAllowed) {
        this.dragAllowed = dragAllowed;
    }
}
