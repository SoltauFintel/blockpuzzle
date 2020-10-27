package de.mwvb.blockpuzzle.game;

import android.content.ContextWrapper;

import java.util.List;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.block.special.LockBlock;
import de.mwvb.blockpuzzle.block.special.StarBlock;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.gravitation.GravitationAction;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.persistence.Persistence;
import de.mwvb.blockpuzzle.playingfield.QPosition;
import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.RandomGamePiece;
import de.mwvb.blockpuzzle.block.special.ISpecialBlock;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.gamepiece.Holders;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.playingfield.OneColorAreaDetector;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

/**
 * Central game logic
 */
public class Game {
    // Stammdaten (read only)
    public static final int blocks = 10;
    private final BlockTypes blockTypes = new BlockTypes(null);

    // Zustand
    private String gameMode = Features.GAME_MODE_CLASSIC;
    private final PlayingField playingField = new PlayingField(blocks);
    private final Holders holders = new Holders();
    private int punkte;
    private int moves;
    private boolean gameOver = false; // wird nicht persistiert
    private boolean won = false;
    private boolean rotatingMode = false; // wird nicht persistiert
    private final GravitationData gravitation = new GravitationData();
    private static int cleanerStartRow = 9; // sowas wie ein Level fürs Cleaner Game

    // Services
    private INextGamePiece nextGamePiece;
    private final IGameView view;
    private IPersistence persistence;

    // Spielaufbau ----

    public Game(IGameView view) {
        this(view, null);
    }

    public Game(IGameView view, IPersistence persistence) {
        this.view = view;
        this.persistence = persistence == null ? new Persistence((ContextWrapper) view) : persistence;
        playingField.setPersistence(this.persistence);
        gravitation.setPersistence(this.persistence);
        holders.setPersistence(this.persistence);
    }

    // New Game ----

    public void initGame(String gameMode, int gamePieceSetNumber) {
        this.gameMode = gameMode;
        persistence.setGameMode(gameMode);
        holders.setView(view);
        playingField.setView(view.getPlayingFieldView());
        if (gamePieceSetNumber == 0) {
            nextGamePiece = new RandomGamePiece();
            if (Features.GAME_MODE_CLEANER.equals(gameMode)) {
                nextGamePiece.ausduennen();
            }
        } else {
            nextGamePiece = new NextGamePieceFromSet(gamePieceSetNumber);
            // TODO NextGamePieceFromSet muss persistiert werden
            // TODO Wenn der Spieler die GamePieceSetNumber ändert, bedeutet das Spielneustart.
        }

        // Drehmodus deaktivieren
        rotatingMode = false;
        view.rotatingModeOff();

        // Gibt es einen Spielstand?
        punkte = persistence.loadScore();
        if (punkte < 0) { // Nein -> Neues Spiel starten!
            newGame();
            return;
        }

        // Spielstand laden
        view.showScore(punkte,0, gameOver); // TODO delta laden
        moves = persistence.loadMoves();
        view.showMoves(moves);
        gravitation.load();
        playingField.load();
        holders.load();
        checkGame();
    }

    /** Benutzer startet freiwillig oder nach GameOver neues Spiel. */
    public void newGame() {
        gameOver = false;
        punkte = 0;
        gravitation.init();
        view.showScore(punkte, 0, gameOver);
        if (nextGamePiece instanceof NextGamePieceFromSet) {
            ((NextGamePieceFromSet) nextGamePiece).setNextRound(0);
        }

        playingField.clear();
        if (Features.GAME_MODE_CLEANER.equals(gameMode)) {
            newCleanerGame();
        }

        moves = 0;
        view.showMoves(moves);

        holders.clearParking();
        offer();
    }

    // TODO wie bei GPDef eine String-Notation benutzen
    private void newCleanerGame() {
        if (cleanerStartRow == 1) {
            playingField.set(2,2,3);
            playingField.set(7,2,3);

            playingField.set(1,7,LockBlock.TYPE);
            playingField.set(2,6,LockBlock.TYPE);
            playingField.set(3,5,LockBlock.TYPE);
            playingField.set(4,5,LockBlock.TYPE);
            playingField.set(5,5,LockBlock.TYPE);
            playingField.set(6,5,LockBlock.TYPE);
            playingField.set(7,6,LockBlock.TYPE);
            playingField.set(8,7,LockBlock.TYPE);
        } else {
            for (int y = cleanerStartRow; y < blocks; y++) {
                for (int x = 0; x < blocks; x++) {
                    if (x != y) {
                        playingField.set(x, y, 11);
                    }
                }
                if (y <= 5) {
                    playingField.set(y - 2, y, LockBlock.TYPE);
                }
            }
            if (cleanerStartRow >= 8) {
                playingField.set(0, blocks - 1, StarBlock.TYPE);
            }
            playingField.set(1, 0, cleanerStartRow < 6 ? 21 : 5);
            playingField.set(blocks - 2, 0, cleanerStartRow < 6 ? 21 : 5);
            if (cleanerStartRow <= 4) {
                for (int x = 1; x < blocks - 3; x++) {
                    playingField.set(x, blocks - 1, LockBlock.TYPE);
                }
                playingField.set(1, blocks - 3, 0);
            }
        }
        playingField.draw();
        cleanerStartRow--;
        if (cleanerStartRow < 1) {
            cleanerStartRow = blocks - 1;
        }
    }

    /** 3 neue zufällige Spielsteine anzeigen */
    private void offer() { // old German method name: vorschlag
        if (gameOver) return;

        for (int i = 1; i <= 3; i++) {
            holders.get(i).setGamePiece(nextGamePiece.next(punkte, blockTypes));
        }
        // TODO avoid two/three 3x3 ?

        save();
    }

    // Spielaktionen ----

    /**
     * Drop Aktion für Spielfeld oder Parking
     *
     * Throws DoesNotWorkException
     */
    public void dispatch(boolean targetIsParking, int index, GamePiece teil, QPosition xy) {
        if (gameOver) {
            return;
        }
        boolean ret;
        if (targetIsParking) {
            ret = holders.park(index); // Drop Aktion für Parking Area
        } else {
            ret = place(index, teil, xy);
        }
        if (ret) {
            if (holders.is123Empty()) {
                offer();
            }
            checkGame();
        } else {
            throw new DoesNotWorkException();
        }
    }

    /**
     * Drop Aktion für Spielfeld
     * @return true wenn Spielstein platziert wurde, false wenn dies nicht möglich ist
     */
    private boolean place(int index, GamePiece teil, QPosition pos) { // old German name: platziere
        gravitation.clear(); // delete previous gravity action
        final int punkteVorher = punkte;
        boolean ret = playingField.match(teil, pos);
        if (ret) {
            sendPlacedEvent(teil, pos);
            playingField.place(teil, pos);
            holders.get(index).setGamePiece(null);

            detectOneColorArea();

            // Gibt es gefüllte Rows?
            FilledRows f = playingField.getFilledRows();

            // Punktzahl erhöhen
            punkte += teil.getPunkte() + 10 * f.getHits();
            rowsAdditionalBonus(f.getHits());

            punkte += processSpecialBlockTypes(f);

            gravitation.set(f);
            if (Features.shakeForGravitation) { // gravity needs phone shaking
                playingField.clearRows(f, null);
            } else { // auto-gravity
                playingField.clearRows(f, new GravitationAction(gravitation, this, playingField)); // Action wird erst wenige Millisekunden später fertig!
            }
            if (f.getHits() > 0) {
                fewGamePiecesOnThePlayingField();
            }
            view.showScore(punkte,punkte - punkteVorher, gameOver);
            view.showMoves(++moves);
        }
        return ret;
    }

    private void sendPlacedEvent(GamePiece teil, QPosition pos) {
        for (int x = teil.getMinX(); x <= teil.getMaxX(); x++) {
            for (int y = teil.getMinY(); y <= teil.getMaxY(); y++) {
                if (teil.filled(x, y)) {
                    int bt = teil.getBlockType(x, y);
                    for (ISpecialBlock s : blockTypes.getSpecialBlockTypes()) {
                        if (s.getBlockType() == bt) {
                            s.placed(teil, pos, new QPosition(x, y));
                        }
                    }
                }
            }
        }
    }

    private void detectOneColorArea() {
        List<QPosition> r = new OneColorAreaDetector(playingField, 20).getOneColorArea();
        if (r == null) return;
        playingField.makeOldColor(); // 10 -> 11, plays also one color sound
        for (QPosition k : r) {
            playingField.set(k.getX(), k.getY(), 10);
        }
        int bonus = r.size() * 5;
        if (bonus < 100) bonus = 100;
        punkte += bonus;
    }

    private int processSpecialBlockTypes(FilledRows f) {
        int punkte = 0;

        // Rows ----
        for (int y : f.getYlist()) {
            for (int x = 0; x < blocks; x++) {
                int bt = playingField.get(x, y);
                for (ISpecialBlock s : blockTypes.getSpecialBlockTypes()) {
                    if (s.getBlockType() == bt) {
                        int r = s.cleared(playingField, new QPosition(x, y));
                        if (r > ISpecialBlock.CLEAR_MAX_MODE) {
                            punkte += r;
                        } else if (r == 1) {
                            f.getExclusions().add(new QPosition(x, y));
                        }
                    }
                }
            }
        }

        // Columns ----
        for (int x : f.getXlist()) {
            for (int y = 0; y < blocks; y++) {
                int bt = playingField.get(x, y);
                for (ISpecialBlock s : blockTypes.getSpecialBlockTypes()) {
                    if (s.getBlockType() == bt) {
                        int r = s.cleared(playingField, new QPosition(x, y));
                        if (r > ISpecialBlock.CLEAR_MAX_MODE) {
                            punkte += r;
                        } else if (r == 1) {
                            f.getExclusions().add(new QPosition(x, y));
                        }
                    }
                }
            }
        }

        return punkte;
    }

    /** Player has shaked smartphone */
    public void shaked() {
        new GravitationAction(gravitation, this, playingField).execute();
        // TODO Ein kurzer Sound als Bestätigung wäre gut.
    }

    private void rowsAdditionalBonus(int rows) {
        switch (rows) {
            case 0:
            case 1: break; // 0-1 kein Bonus
            // Bonuspunkte wenn mehr als 2 Rows gleichzeitig abgeräumt werden.
            // Fällt mir etwas schwer zu entscheiden wieviel Punkte das jeweils wert ist.
            case 2: punkte += 12; break;
            case 3: punkte += 15; break;
            default: /* >= 4 */ punkte += 22; break;
        }
    }

    private void fewGamePiecesOnThePlayingField() {
        // Es gibt einen Bonus, wenn nach dem Abräumen von Rows nur noch wenige Spielsteine
        // auf dem Spielfeld sind. 1-2 ist nicht einfach, 0 fast unmöglich.
        int bonus = 0;
        switch (playingField.getFilled()) {
            case 0: bonus = 444; break; // Wahnsinn!
            case 1: bonus = 111; break;
            case 2: bonus = 60; break;
            case 3: bonus = 30; break;
            case 4: bonus = 4; break;
        }
        if (bonus > 0) {
            punkte += bonus;
        }

        if (Features.GAME_MODE_CLEANER.equals(gameMode) && playingField.getFilled() == 0) {
            holders.get(1).setGamePiece(null);
            holders.get(2).setGamePiece(null);
            holders.get(3).setGamePiece(null);
            holders.clearParking();
            // TODO << Methode für diese 4 Aufrufe machen

            won = true;
            gameOver = true;
            // TODO Highscore: die wenigsten Moves und Punkte
            playingField.gameOver();
            // TODO ******** Ich will beim Sieg keine anderen Sounds haben!!! ***********
        }
    }

    private void checkGame() {
        // es muss ein Spielstein noch rein gehen
        boolean a = moveImpossible(1);
        boolean b = moveImpossible(2);
        boolean c = moveImpossible(3);
        boolean d = moveImpossible(-1);
        if (a && b && c && d && !holders.isParkingFree()) {
            gameOver = true;
            updateHighScore();
            view.showScore(punkte,0, gameOver); // display game over text
            playingField.gameOver(); // wenn parke die letzte Aktion war
        }
    }

    private void updateHighScore() {
        int highscore = persistence.loadHighScore();
        if (punkte > highscore || highscore <= 0) {
            persistence.saveHighScore(punkte);
            persistence.saveHighScoreMoves(moves);
        } else if (punkte == highscore) {
            int hMoves = persistence.loadHighScoreMoves();
            if (moves < hMoves || hMoves <= 0) {
                persistence.saveHighScoreMoves(moves);
            }
        }
    }

    public void checkPossibleMoves() {
        moveImpossible(1);
        moveImpossible(2);
        moveImpossible(3);
        moveImpossible(-1);
    }

    // TODO checken ob von außerhalb benötigt
    public boolean moveImpossible(int index) {
        GamePiece teil = holders.get(index).getGamePiece();
        int result = moveImpossibleR(teil);
        if (result != 2) {
            holders.get(index).grey(result == 1 || result == -1);
        }
        return result > 0;
    }

    /**
     * @param teil game piece
     * @return 2: game piece view is empty, 1: game piece does not fit in (grey true!),
     * 0: game piece fits in (ro is 1, grey false!),
     * -1: game piece fits in (ro is > 1, grey true!).
     * >0: return true (move is impossible), else return false (move is possible).
     */
    public int moveImpossibleR(GamePiece teil) {
        if (teil == null) {
            return 2; // GamePieceView is empty
        }
        for (int ro = 1; ro <= 4; ro++) { // try all 4 rotations
            for (int x = 0; x < blocks; x++) {
                for (int y = 0; y < blocks; y++) {
                    if (playingField.match(teil, new QPosition(x, y))) {
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
            teil = teil.copy().rotateToRight();
        }
        return 1; // There's no space for the game piece in the playing field.
    }

    public int getScore() {
        return punkte;
    }

    public boolean lessScore() {
        return punkte < 10;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int get(int x, int y) {
        return playingField.get(x, y);
    }

    public boolean toggleRotatingMode() {
        rotatingMode = !rotatingMode;
        return rotatingMode;
    }

    public int getMoves() {
        return moves;
    }

    public void rotate(int index) {
        if (!gameOver) {
            holders.get(index).rotate();
            moveImpossible(index);
        }
    }

    public void save() {
        persistence.saveScore(punkte);
        playingField.save();
        gravitation.save();
        persistence.saveMoves(moves);
        holders.save();
    }

    public boolean isCleanerGame() {
        return Features.GAME_MODE_CLEANER.equals(gameMode);
    }

    public boolean isWon() {
        return won;
    }
}
