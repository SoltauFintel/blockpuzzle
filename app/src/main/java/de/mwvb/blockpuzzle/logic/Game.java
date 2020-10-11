package de.mwvb.blockpuzzle.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mwvb.blockpuzzle.MainActivity;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.logic.spielstein.GamePieces;
import de.mwvb.blockpuzzle.logic.spielstein.special.ISpecialBlock;
import de.mwvb.blockpuzzle.logic.spielstein.special.LockBlock;
import de.mwvb.blockpuzzle.sound.SoundService;
import de.mwvb.blockpuzzle.view.BlockTypes;

public class Game {
    public static final int blocks = 10;
    private static final Random rand = new Random(System.currentTimeMillis());
    private final MainActivity view;
    private final PlayingField playingField = new PlayingField(blocks);
    private final BlockTypes blockTypes = new BlockTypes(null);
    private final List<GamePiece> gamePieces = new ArrayList<>();
    private int punkte;
    private int moves;
    private boolean gameOver = false;
    private boolean rotatingMode = false; // wird nicht persistiert
    private Persistence persistence;
    private SoundService soundService;
    private Action gravity = null;
    private boolean firstGravitationPlayed = false;
    // TODO Bisher höchste Punktzahl persistieren.
    // TODO Drag Schatten anzeigen

    // Spielaufbau ----

    public Game(MainActivity activity) {
        view = activity;
        gamePieces.addAll(GamePieces.get());
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
        playingField.setPersistence(persistence);
    }

    public void setSoundService(SoundService soundService) {
        this.soundService = soundService;
    }

    // New Game ----

    public void initGame() {
        gravity = null;
        view.setGamePiece(-1, null, false);

        // Drehmodus deaktivieren
        rotatingMode = false;
        view.rotatingModeOff();

        // Gibt es einen Spielstand?
        punkte = persistence.loadScore();
        if (punkte < 0) { // Nein -> Neues Spiel starten!
            newGame();
            return;
        }
        moves = persistence.loadMoves();
        firstGravitationPlayed = false; // TODO laden
        // Es gibt einen Spielstand.
        playingField.read();
        view.updateScore(0);
        view.showMoves(moves);
        view.drawPlayingField();
        view.restoreGamePieceViews();
        checkGame();
    }

    /** Benutzer startet freiwillig oder nach GameOver neues Spiel. */
    public void newGame() {
        playingField.clear(true);
        gameOver = false;
        punkte = 0;
        moves = 0;
        saveScore();
        persistence.saveMoves(moves);
        view.updateScore(punkte);
        view.showMoves(moves);
        firstGravitationPlayed = false;

        view.drawPlayingField();
        view.setGamePiece(-1, null, true);
        offer();
    }

    /** 3 neue zufällige Spielsteine anzeigen */
    private void offer() { // old German method name: vorschlag
        view.setGamePiece(1, createRandomGamePiece(gamePieces), true);
        view.setGamePiece(2, createRandomGamePiece(gamePieces), true);
        view.setGamePiece(3, createRandomGamePiece(gamePieces), true);
        // TODO avoid two/three 3x3 ?
    }

    private GamePiece createRandomGamePiece(List<GamePiece> teile) {
        int loop = 0;
        int index = rand.nextInt(teile.size());
        GamePiece gamePiece = teile.get(index);
        while (punkte < gamePiece.getMindestpunktzahl()) {
            if (++loop > 1000) { // Notausgang
                return teile.get(0);
            }
            index = rand.nextInt(teile.size());
            gamePiece = teile.get(index);
        }
        gamePiece = gamePiece.copy();
        // Insert a special block type randomly
        for (ISpecialBlock s : blockTypes.getSpecialBlockTypes()) {
            if (s.isRelevant(gamePiece) && s.process(gamePiece)) {
                break;
            }
        }
        return gamePiece;
    }

    // Spielaktionen ----

    /** Drop Aktion für Spielfeld oder Parking */
    public void dispatch(boolean targetIsParking, int index, GamePiece teil, QPosition xy) {
        if (gameOver) {
            return;
        }
        boolean ret;
        if (targetIsParking) {
            ret = park(index, teil);
        } else {
            ret = place(index, teil, xy);
        }
        if (ret) {
            if (view.getGamePiece(1) == null && view.getGamePiece(2) == null && view.getGamePiece(3) == null) {
                offer();
            }
            checkGame();
        } else {
            view.doesNotWork();
        }
    }

    /** Drop Aktion für Parking Area */
    private boolean park(int index, GamePiece teil) {
        if (index != -1 && view.getGamePiece(-1) == null) { // es geht wenn Source 1,2,3 und Parking frei
            view.setGamePiece(-1, view.getGamePiece(index), true); // Parking belegen
            view.setGamePiece(index, null, true); // Source leeren
            return true;
        }
        return false;
    }

    /**
     * Drop Aktion für Spielfeld
     * @return true wenn Spielstein platziert wurde, false wenn dies nicht möglich ist
     */
    private boolean place(int index, GamePiece teil, QPosition pos) { // old German name: platziere
        System.out.println("place " + pos.getX() + ", " + pos.getY());
        gravity = null; // delete previous gravity action
        Action lGravity = null;
        final int punkteVorher = punkte;
        boolean ret = playingField.match(teil, pos);
        if (ret) {
            sendPlacedEvent(teil, pos);
            playingField.place(teil, pos);
            view.drawPlayingField();
            view.setGamePiece(index, null, true);

            detectOneColorArea();

            // Gibt es gefüllte Rows?
            FilledRows f = playingField.getFilledRows();

            // Punktzahl erhöhen
            punkte += teil.getPunkte() + 10 * f.getHits();
            rowsAdditionalBonus(f.getHits());

            punkte += processSpecialBlockTypes(f);

            lGravity = getGravityAction(f);
            if (view.getWithGravity()) { // gravity needs phone shaking
                view.clearRows(f, null);
            } else { // auto-gravity
                view.clearRows(f, lGravity); // Action wird erst wenige Millisekunden später fertig!
            }
            playingField.clearRows(f);
            if (f.getHits() > 0) {
                fewGamePiecesOnThePlayingField();
            }
            view.updateScore(punkte - punkteVorher);
            saveScore();
            view.showMoves(++moves);
            persistence.saveMoves(moves);
        }
        if (view.getWithGravity()) { // gravity needs phone shaking
            gravity = lGravity; // activate gravity
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
        playingField.makeOldColor(); // 10 -> 11
        for (QPosition k : r) {
            playingField.set(k.getX(), k.getY(), 10);
        }
        int bonus = r.size() * 5;
        if (bonus < 100) bonus = 100;
        punkte += bonus;
        soundService.oneColor();
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

    private Action getGravityAction(FilledRows f) {
        return () -> {
            for (int i = 5; i >= 1; i--) {
                boolean doRemove = true;
                for (QPosition k : f.getExclusions()) {
                    if (k.getY() == blocks - i) {
                        doRemove = false;
                        break;
                    }
                }
                if (doRemove && f.getYlist().contains(blocks - i)) {
                    // Row war voll und wurde geleert -> Gravitation auslösen
                    if (!firstGravitationPlayed) {
                        firstGravitationPlayed = true;
                        soundService.firstGravitation();
                    }
                    playingField.gravitation(blocks - i);
                    view.drawPlayingField();
                }
            }
            moveImpossible(1);
            moveImpossible(2);
            moveImpossible(3);
            moveImpossible(-1);
        };
    }

    /** Player has shaked smartphone */
    public void shaked() {
        if (gravity != null) {
            Action lGravity = gravity;
            gravity = null;
            lGravity.execute();
            // TODO Ein kurzer Sound als Bestätigung wäre gut.
        }
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
    }

    private void checkGame() {
        // es muss ein Spielstein noch rein gehen
        boolean a = moveImpossible(1);
        boolean b = moveImpossible(2);
        boolean c = moveImpossible(3);
        boolean d = moveImpossible(-1);
        if (a && b && c && d && view.getGamePiece(-1) != null) {
            gameOver = true;
            view.updateScore(0);
            view.drawPlayingField(); // wenn parke die letzte Aktion war
        }
    }

    public boolean moveImpossible(int index) {
        GamePiece teil = view.getGamePiece(index);
        if (teil == null) {
            return true; // GamePieceView is empty
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
                        view.grey(index, ro > 1);
                        return false; // Spielstein passt rein
                    }
                }
            }
            teil = teil.copy().rotateToRight();
        }
        view.grey(index, true);
        return true; // There's no space for the game piece in the playing field.
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

    private void saveScore() {
        persistence.saveScore(punkte);
    }
}
