package de.mwvb.blockpuzzle.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.game.place.DetectOneColorAreaAction;
import de.mwvb.blockpuzzle.game.place.EmptyScreenBonusPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceInfo;
import de.mwvb.blockpuzzle.game.place.ScorePlaceAction;
import de.mwvb.blockpuzzle.game.place.SendPlacedEventAction;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.Holders;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.RandomGamePiece;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Block Puzzle game logic
 *
 * This is the old game and the base class for the Stone Wars game.
 */
// Eigentlich ist das eine GameEngine.
// Zu unübersichtlich, weil zu viele Methoden. Vll kann man ein Core-GameEngine machen und Listener? Vll auch die Initialisierung rausziehen?
public class Game implements GameEngineInterface {
    // Stammdaten (read only)
    public static final int blocks = 10;
    private final BlockTypes blockTypes = new BlockTypes(null);

    // Zustand
    protected GameState gs; // not final
    protected final PlayingField playingField = new PlayingField(blocks);
    protected final Holders holders = new Holders();
    private final GravitationData gravitation = new GravitationData(); // überlegen, ob ich die Daten nicht im Spielstand halten kann
    private boolean dragAllowed = true;

    // Services
    protected final IGameView view;
    protected INextGamePiece nextGamePiece;

    // Spielaufbau ----

    public Game(IGameView view, GameState gs) {
        this.view = view;
        this.gs = gs;
    }

    // New Game ----

    // called by MainActivity.onResume()
    public void initGame() {
        holders.setView(view);
        playingField.setView(view.getPlayingFieldView());
        nextGamePiece = getNextGamePieceGenerator();

        // TODO Vielleicht könnte man init-game und game-play trennen?
        // Gibt es einen Spielstand?
        if (gs.get().getScore() < 0) { // Nein
            newGame(); // Neues Spiel starten!
        } else {
            loadGame(true, true); // Spielstand laden
        }
    }

    public boolean isNewGameButtonVisible() {
        return true;
    }

    protected INextGamePiece getNextGamePieceGenerator() {
        return new RandomGamePiece();
    }

    /** Benutzer startet freiwillig oder nach GameOver neues Spiel. */
    public void newGame() {
        doNewGame();
        offer();
        save(); // TO-DO gs.save() in doNewGame() und save() hier; schauen ob das anders geht
    }
    protected void doNewGame() {
        gs.newGame();
        gravitation.init();
        initNextGamePieceForNewGame();

        initPlayingField();
        view.showScoreAndMoves(gs.get());
        holders.clearParking();

        gs.save();
    }

    protected void initNextGamePieceForNewGame() {
        nextGamePiece.reset();
    }

    protected void initPlayingField() {
        playingField.clear();
    }

    protected void loadGame(boolean loadNextGamePiece, boolean checkGame) {
        Spielstand ss = gs.get();
        view.showScoreAndMoves(ss);

        if (loadNextGamePiece) {
            nextGamePiece.load();
        }
        gravitation.load(ss);
        playingField.load(ss);
        holders.load(ss);

        if (checkGame) {
            checkGame();
        }
    }

    /** 3 neue zufällige Spielsteine anzeigen */
    protected void offer() { // old German method name: vorschlag
        for (int i = 1; i <= 3; i++) {
            holders.get(i).setGamePiece(nextGamePiece.next(blockTypes));
        }

        if (!gs.isGameOver() && holders.is123Empty()) {
            // Ein etwaiger letzter geparkter Stein wird aus dem Spiel genommen, da dieser zur Vereinfachung keine Rolle mehr spielen soll.
            // Mag vorteilhaft oder unvorteilhaft sein, aber ich definier die Spielregeln einfach so!
            // Vorteilhaft weil man mit dem letzten Stein noch mehr Punkte als der Gegner bekommen könnte.
            // Unvorteilhaft weil man mit dem letzten Stein noch ein Spielfeld-voll-Game-over erzielen könnte.
            holders.clearParking();

            // Wenn alle Spielsteine aufgebraucht sind, ist Spielende.
            view.getMessages().getNoMoreGamePieces().show();
            handleNoGamePieces();
            onGameOver();
        }
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
        if (gs.isGameOver()) {
            return;
        }
        boolean ret;
        if (targetIsParking) {
            ret = holders.park(index); // Drop Aktion für Parking Area
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
        if (holders.is123Empty()) {
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
        holders.get(index).setGamePiece(null);

        // Actions ----
        PlaceInfo info = createInfo(index, gamePiece, pos);
        for (IPlaceAction action : getPlaceActions()) {
            action.perform(info);
        }

        // Display and save ----
        ss.setDelta(ss.getScore() - scoreBefore);
        view.showScoreAndMoves(ss);
        gs.save();
        return true;
    }

    @NotNull
    protected PlaceInfo createInfo(int index, GamePiece gamePiece, QPosition pos) {
        return new PlaceInfo(index, gamePiece, pos, gs, playingField.getFilledRows(), blockTypes, playingField, gravitation,
                blocks, view.getMessages(), view, this);
    }

    protected List<IPlaceAction> getPlaceActions() {
        List<IPlaceAction> ret = new ArrayList<>();
        ret.add(new SendPlacedEventAction());
        ret.add(getDetectOneColorAreaAction());
        ret.add(getScorePlaceAction());
        ret.add(getClearRowsPlaceAction());
        ret.add(new EmptyScreenBonusPlaceAction());
        return ret;
    }

    @NotNull
    protected IPlaceAction getDetectOneColorAreaAction() {
        return new DetectOneColorAreaAction();
    }

    @NotNull
    protected ScorePlaceAction getScorePlaceAction() {
        return new ScorePlaceAction();
    }

    @NotNull
    protected ClearRowsPlaceAction getClearRowsPlaceAction() {
        return new ClearRowsPlaceAction(5);
    }

    /** Player has shaked smartphone */
    public void shaked() {
        getClearRowsPlaceAction().executeGravitation(gravitation, this, playingField);
        view.shake();
    }

    /** check for game over */
    protected void checkGame() {
        // es muss ein Spielstein noch rein gehen
        boolean a = moveImpossible(1);
        boolean b = moveImpossible(2);
        boolean c = moveImpossible(3);
        boolean d = moveImpossible(-1);
        if (a && b && c && d && !holders.isParkingFree()) {
            onGameOver();
        }
    }

    /** lost game, game over */
    @Override
    public void onGameOver() {
        Spielstand ss = gs.get();
        ss.setState(GamePlayState.LOST_GAME); // old code: gameOver = true;
        updateHighScore();
        ss.setDelta(0);
        gs.save();
        view.showScoreAndMoves(ss); // display game over text
        playingField.gameOver();    // if park() has been the last action
    }

    // TO-DO überdenken. Macht vermutlich nur für das "old game" Sinn.
    private void updateHighScore() {
        Spielstand ss = gs.get();
        final int score = ss.getScore();
        final int moves = ss.getMoves();
        int highscore = ss.getHighscore();
        int hMoves = ss.getHighscoreMoves();
        if (score > highscore || highscore <= 0) {
            ss.setHighscore(score);
            ss.setHighscoreMoves(moves);
            gs.save();
        } else if (score == highscore && (moves < hMoves || hMoves <= 0)) {
            ss.setHighscoreMoves(moves);
            gs.save();
        }
    }

    @Override
    public void checkPossibleMoves() {
        moveImpossible(1);
        moveImpossible(2);
        moveImpossible(3);
        moveImpossible(-1);
    }

    private boolean moveImpossible(int index) {
        GamePiece gamePiece = holders.get(index).getGamePiece();
        int result = moveImpossibleR(gamePiece);
        if (result != 2) {
            holders.get(index).grey(result == 1 || result == -1);
        }
        return result > 0;
    }

    /**
     * @param gamePiece game piece to check if it cannot be placed into playing field
     * @return 2: game piece view is empty, 1: game piece does not fit in (grey true!),
     * 0: game piece fits in (ro is 1, grey false!),
     * -1: game piece fits in (ro is > 1, grey true!).
     * >0: return true (move is impossible), else return false (move is possible).
     */
    int moveImpossibleR(GamePiece gamePiece) {
        if (gamePiece == null) {
            return 2; // GamePieceView is empty
        }
        for (int ro = 1; ro <= 4; ro++) { // try all 4 rotations
            for (int x = 0; x < blocks; x++) {
                for (int y = 0; y < blocks; y++) {
                    if (playingField.match(gamePiece, new QPosition(x, y))) {
                        // GamePiece fits into playing field.
                        // original rotation (ro=1): not grey
                        // rotated (ro>1): grey, because with original rotation it doesn't fit
                        // and therefore I want to inform the player that he must rotate until
                        // it's not grey (or it's game over but there's a game over sound
                        // and he cannot rotate any more).
                        return ro > 1 ? -1 : 0; // Spielstein passt rein
                    }
                }
            }
            gamePiece = gamePiece.copy().rotateToRight();
        }
        return 1; // There's no space for the game piece in the playing field.
    }

    public boolean lessScore() {
        return gs.get().getScore() < 10;
    }

    public boolean isGameOver() {
        return gs.isGameOver();
    }

    public int get(int x, int y) {
        return playingField.get(x, y);
    }

    public void rotate(int index) {
        if (gs.get().getState() == GamePlayState.PLAYING) {
            holders.get(index).rotate();
            moveImpossible(index);
        }
    }

    @Override
    public void save() {
        Spielstand ss = gs.get();
        playingField.save(ss);
        gravitation.save(ss);
        holders.save(ss);
        gs.save();
    }

    public boolean gameCanBeWon() {
        return false;
    }

    public boolean isWon() {
        return gs.get().getState() == GamePlayState.WON_GAME;
    }

    public boolean isDragAllowed() {
        return dragAllowed;
    }

    public void setDragAllowed(boolean dragAllowed) {
        this.dragAllowed = dragAllowed;
    }

    @Override
    public void clearAllHolders() {
        holders.clearAll();
    }
}
