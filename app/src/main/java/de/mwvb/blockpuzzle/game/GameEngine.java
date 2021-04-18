package de.mwvb.blockpuzzle.game;

import java.text.DecimalFormat;

import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceActionModel;
import de.mwvb.blockpuzzle.gamedefinition.ICrushed;
import de.mwvb.blockpuzzle.gamedefinition.OldGameDefinition;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.global.messages.MessageFactory;
import de.mwvb.blockpuzzle.global.messages.MessageObject;
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
public class GameEngine implements GameEngineInterface, ICrushed {
    protected final GameEngineModel model;
    protected final GameState gs; // for convenience
    protected final PlayingField playingField; // for convenience

    private boolean dragAllowed = true;
    public boolean rebuild = false;
    private boolean rowsWillBeCleared = false;
    protected Spielstand undo = null;

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
        boolean ret;
        if (gs.isLostGame()) {
            return;
        }

        Spielstand nextUndo = new Spielstand();
        playingField.save(gs.get()); // anscheinend wird nach clear-row der gs.get.playingField nicht aktualisiert
        gs.get().transfer(nextUndo);

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

        undo = nextUndo;
    }

    protected void postDispatch() {
        if (model.getHolders().is123Empty()) {
            offer(false);
        }
        if (!rebuild) {  // Das ist für Todesstern. Eigentlich müsste ich gucken, dass ich beim Reaktorwechsel offer erlaube, damit das hier nicht notwendig ist.
            checkGame();
        }
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
        PlaceActionModel info = new PlaceActionModel(this, model, dropActionModel, playingField.createFilledRows());
        rowsWillBeCleared = info.getFilledRows().getHits() > 0;
        for (IPlaceAction action : model.getPlaceActions()) {
            action.perform(info);
        }

        // Display and save ----
        ss.setDelta(ss.getScore() - scoreBefore);
        showScoreAndMoves();
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
        if (!offerAllowed(newGameMode)) {
            return;
        }

        for (int i = 1; i <= 3; i++) {
            model.getHolders().get(i).setGamePiece(model.getNextGamePiece().next(model.getBlockTypes()));
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean offerAllowed(boolean newGameMode) {
        GamePlayState state = gs.get().getState();
        return (state == GamePlayState.PLAYING
                || (state == GamePlayState.WON_GAME && getDefinition().gameGoesOnAfterWonGame()));
    }

    // Game state checks ----

    /** Check game: player lost game if no game piece can be moved into the playing field */
    public void checkGame() {
        if (isHandleNoGamePiecesAllowed() && model.getHolders().is123Empty()) { // can't happen in Old Game
            // Ein etwaiger letzter geparkter Stein wird aus dem Spiel genommen, da dieser zur Vereinfachung keine Rolle mehr spielen soll.
            // Mag vorteilhaft oder unvorteilhaft sein, aber ich definier die Spielregeln einfach so!
            // Vorteilhaft weil man mit dem letzten Stein noch mehr Punkte als der Gegner bekommen könnte.
            // Unvorteilhaft weil man mit dem letzten Stein noch ein Spielfeld-voll-Game-over erzielen könnte.
            model.getHolders().clearParking();

            // Wenn alle Spielsteine aufgebraucht sind, ist Spielende.
            handleNoGamePieces();

        } else if (checkIfNoMoveIsPossible()) { // no game piece fits into playing field
            if (rowsWillBeCleared) {
                rowsWillBeCleared = false;
                return;
            }
            playingField.gameOver();
            Spielstand ss = gs.get();
            if (ss.getState() != GamePlayState.LOST_GAME) {
                model.getView().playSound(4);
            }
            ss.setState(GamePlayState.LOST_GAME);
            undo = null;
            gs.updateHighScore();
            ss.setDelta(0);
            gs.save();
            showScoreAndMoves(); // display game over text
        }
    }

    protected boolean isHandleNoGamePiecesAllowed() {
        return false;
    }

    protected void handleNoGamePieces() {
        if (gs.get().getState() != GamePlayState.PLAYING) {
            return;
        }

        // Show message to player that there are no game pieces left.
        model.getView().getMessages().getNoMoreGamePieces().show();

        playingField.gameOver();

        Spielstand ss = gs.get();
        if (model.getDefinition().isWonAfterNoGamePieces(ss)) {
            if (ss.getState() != GamePlayState.WON_GAME) {
                ss.setState(GamePlayState.WON_GAME);
                model.getView().playSound(3);
                undo = null;
            }
        } else {
            if (ss.getState() != GamePlayState.LOST_GAME) {
                ss.setState(GamePlayState.LOST_GAME);
                model.getView().playSound(4);
                undo = null;
            }
        }
        gs.updateHighScore();
        ss.setDelta(0);
        gs.save();
        showScoreAndMoves(); // display game over text
    }

    @Override
    public void onEndGame(boolean wonGame, boolean stopGame) {
        if (stopGame || !wonGame) {
            playingField.gameOver();
        }

        Spielstand ss = gs.get();
        if (wonGame) {
            if (ss.getState() != GamePlayState.WON_GAME) {
                ss.setState(GamePlayState.WON_GAME);
                model.getView().playSound(3);
                undo = null;
                gs.updateHighScore();
                ss.setDelta(0);
                gs.save();
            }
        } else {
            if (ss.getState() != GamePlayState.LOST_GAME) {
                ss.setState(GamePlayState.LOST_GAME);
                model.getView().playSound(4);
                undo = null;
                gs.updateHighScore();
                ss.setDelta(0);
                gs.save();
            }
        }

        showScoreAndMoves(); // display game over text
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

    protected final boolean moveImpossible(int index) {
        GamePiece gamePiece = model.getHolders().get(index).getGamePiece();
        GamePieceMatchResult result = playingField.match(gamePiece);
        if (result == NO_GAME_PIECE) {
            return true; // You cannot move game piece if there's no game piece.
        }
        boolean impossible = (result == NO_SPACE);
        model.getHolders().get(index).grey(impossible || (result == FITS_ROTATED));
        return impossible;
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

    // Show game state ----

    public void showScoreAndMoves() {
        Spielstand ss = gs.get();
        MessageFactory m = model.getView().getMessages();

        String text = getScoreText(ss, m);
        if (ss.getDelta() > 0) {
            text += " (" + new DecimalFormat("+#,##0").format(ss.getDelta()) + ")";
        }
        model.getView().showScore(text); //         info.text = text;

        switch (ss.getMoves()) {
            case 0:
                text = "";
                break;
            case 1:
                text = new DecimalFormat("#,##0").format(ss.getMoves()) + " " + m.getMove().toString();
                break;
            default:
                text = new DecimalFormat("#,##0").format(ss.getMoves()) + " " + m.getMoves().toString();
        }
        model.getView().showMoves(text);
    }

    private String getScoreText(Spielstand ss, MessageFactory m) {
        MessageObject ret;
        if (ss.getState() == GamePlayState.LOST_GAME) {
            if (ss.getScore() == 1) ret = m.getGameOverScore1();
            else ret = m.getGameOverScore2();
        } else if (ss.getState() == GamePlayState.WON_GAME) {
            if (ss.getScore() == 1) ret = m.getWinScore1();
            else ret = m.getWinScore2();
        } else { // PLAYING
            if (ss.getScore() == 1) ret = m.getScore1();
            else ret = m.getScore2();
        }
        return ret.toString().replace("XX", new DecimalFormat("#,##0").format(ss.getScore()));
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

    public TopButtonMode getTopButtonMode() {
        return model.getDefinition().getTopButtonMode();
    }

    public void undo() {
        if (undo == null || isLostGame()) {
            throw new DoesNotWorkException();
        }

        // Undo previous move
        Spielstand ss = gs.get();
        undo.transfer(ss);
        undo = null; // consumed

        // Refresh GUI
        showScoreAndMoves();
        model.getNextGamePiece().load();
        model.getGravitation().clear(); //.load(ss);  // ???
        playingField.load(ss);
        model.getHolders().load(ss);
    }

    @Override
    public void crushed(int areaSize) {
        int bonus = areaSize * 10;
        gs.get().setDelta(bonus);
        gs.addScore(bonus);
        showScoreAndMoves();
    }
}
