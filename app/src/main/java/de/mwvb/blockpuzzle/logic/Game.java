package de.mwvb.blockpuzzle.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mwvb.blockpuzzle.MainActivity;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.logic.spielstein.GamePieces;

public class Game {
    public static final int blocks = 10;
    private static final Random rand = new Random(System.currentTimeMillis());
    private final MainActivity view;
    private final PlayingField playingField = new PlayingField(blocks);
    private final List<GamePiece> gamePieces = new ArrayList<>();
    private int punkte;
    private int moves;
    private boolean gameOver = false;
    private boolean rotatingMode = false; // wird nicht persistiert
    private Persistence persistence;
    // TODO Bisher höchste Punktzahl persistieren.
    // TODO Drag Schatten anzeigen
    // TODO anderer Sound: Game over

    // Spielaufbau ----

    public Game(MainActivity activity) {
        view = activity;
        gamePieces.addAll(GamePieces.get());
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
        playingField.setPersistence(persistence);
    }

    // New Game ----

    public void initGame() {
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

        view.drawPlayingField();
        view.setGamePiece(-1, null, true);
        offer();
    }

    /** 3 neue zufällige Spielsteine anzeigen */
    private void offer() { // old German method name: vorschlag
        view.setGamePiece(1, createRandomGamePiece(gamePieces), true);
        view.setGamePiece(2, createRandomGamePiece(gamePieces), true);
        view.setGamePiece(3, createRandomGamePiece(gamePieces), true);
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
        return gamePiece.copy();
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
        final int punkteVorher = punkte;
        boolean ret = playingField.match(teil, pos);
        if (ret) {
            playingField.place(teil, pos);
            view.drawPlayingField();
            view.setGamePiece(index, null, true);

            detectOneColorArea();

            // Gibt es gefüllte Rows?
            FilledRows f = playingField.getFilledRows();

            // Punktzahl erhöhen
            punkte += teil.getPunkte() + 10 * f.getHits();
            rowsAdditionalBonus(f.getHits());

            view.clearRows(f, getGravityAction(f)); // Wird erst wenige Millisekunden später fertig!
            playingField.clearRows(f);
            if (f.getHits() > 0) {
                fewGamePiecesOnThePlayingField();
            }
            view.updateScore(punkte - punkteVorher);
            saveScore();
            view.showMoves(++moves);
            persistence.saveMoves(moves);
        }
        return ret;
    }

    private void detectOneColorArea() {
        List<QPosition> r = new OneColorAreaDetector(playingField, 10).getOneColorArea();
        if (r == null) return;
        playingField.makeOldColor(); // 10 -> 11
        for (QPosition k : r) {
            playingField.set(k.getX(), k.getY(), 10);
        }
        punkte += r.size() * 5;
    }

    private Action getGravityAction(FilledRows f) {
        return () -> {
            for (int i = 5; i >= 1; i--) {
                if (f.getYlist().contains(blocks - i)) {
                    // Row war voll und wurde geleert -> Gravitation auslösen
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
            return true; // TeilView ist leer
        }
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                if (playingField.match(teil, new QPosition(x, y))) {
                    view.grey(index, false);
                    return false; // Spielstein passt rein
                }
            }
        }
        view.grey(index, true);
        return true; // Spielstein passt nirgendwo rein
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
