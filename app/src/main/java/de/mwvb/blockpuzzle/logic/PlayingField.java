package de.mwvb.blockpuzzle.logic;

import de.mwvb.blockpuzzle.entity.QPosition;
import de.mwvb.blockpuzzle.logic.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.view.IPlayingFieldView;

public class PlayingField {
    // Stammdaten
    private final int blocks;

    // Zustand
    /** 1: x (nach rechts), 2: y (nach unten) */
    private int[][] matrix;
    private boolean gameOver = false;

    // Services
    private IPlayingFieldView view;
    private IPersistence persistence;
    // TODO Der könnte noch eine IPlayingFieldView haben.

    // TODO Idee: Jeder Block sollte ein Objekt sein, welches Eigenschaften (z.B. Farbe) und Verhalten (z.B. LockBlock) hat.

    public PlayingField(int blocks) {
        this.blocks = blocks;
        matrix = new int[blocks][blocks];
    }

    public void setView(IPlayingFieldView view) {
        this.view = view;
        this.view.setPlayingField(this);
    }

    public void setPersistence(IPersistence persistence) {
        this.persistence = persistence;
    }

    public int get(int x, int y) {
        return matrix[x][y];
    }

    // Soll private bleiben, da nur die Game Engine die Matrix verändern darf.
    // -> Hab ich jetzt aber public machen müssen, damit Persistence darauf zugreifen kann.
    public void set(int x, int y, int value) {
        matrix[x][y] = value;
    }

    public void clear() {
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                set(x, y, 0);
            }
        }
        view.draw();
        write();
    }

    public boolean match(GamePiece teil, QPosition pos) {
        for (int x = teil.getMinX(); x <= teil.getMaxX(); x++) {
            for (int y = teil.getMinY(); y <= teil.getMaxY(); y++) {
                if (teil.filled(x, y)) {
                    int ax = pos.getX() + x - teil.getMinX();
                    int ay = pos.getY() + y - teil.getMinY();
                    if (ax < 0 || ax >= blocks || ay < 0 || ay >= blocks) {
                        return false;
                    }
                    int v = get(ax, ay);
                    if (v > 0 && v < 30) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Male Teil ins Spielfeld! */
    public void place(GamePiece teil, QPosition pos) { // old German method name: platziere
        for (int x = teil.getMinX(); x <= teil.getMaxX(); x++) {
            for (int y = teil.getMinY(); y <= teil.getMaxY(); y++) {
                if (teil.filled(x, y)) {
                    int ax = pos.getX() + x - teil.getMinX();
                    int ay = pos.getY() + y - teil.getMinY();
                    set(ax, ay, teil.getBlockType(x, y));
                }
            }
        }
        view.draw();
        write();
    }

    public FilledRows getFilledRows() {
        FilledRows ret = new FilledRows();
        for (int y = 0; y < blocks; y++) {
            if (x_filled(y)) {
                ret.getYlist().add(y);
            }
        }
        for (int x = 0; x < blocks; x++) {
            if (y_filled(x)) {
                ret.getXlist().add(x);
            }
        }
        return ret;
    }

    private boolean x_filled(int y) {
        for (int x = 0; x < blocks; x++) {
            if (get(x, y) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean y_filled(int x) {
        for (int y = 0; y < blocks; y++) {
            if (get(x, y) == 0) {
                return false;
            }
        }
        return true;
    }

    public void clearRows(FilledRows f, Action action) {
        for (int x : f.getXlist()) {
            for (int y = 0; y < blocks; y++) {
                if (!f.getExclusions().contains(new QPosition(x, y))) {
                    set(x, y, 0);
                }
            }
        }
        for (int y : f.getYlist()) {
            for (int x = 0; x < blocks; x++) {
                if (!f.getExclusions().contains(new QPosition(x, y))) {
                    set(x, y, 0);
                }
            }
        }
        view.clearRows(f, action);
        write();
    }

    public int getFilled() {
        int ret = 0;
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                int value = get(x, y);
                if (value > 0 && value < 30) ret++;
            }
        }
        return ret;
    }

    public void gravitation(int row, boolean playSound) {
        for (int y = row; y >= 1; y--) {
            for (int x = 0; x < blocks; x++) {
                set(x, y, get(x, y - 1));
            }
        }
        for (int x = 0; x < blocks; x++) {
            set(x, 0, 0);
        }
        view.draw();
        if (playSound) { // Sound must not be played always.
            view.gravitation();
        }
        write();
    }

    public void gameOver() {
        gameOver = true;
        view.draw();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void load() {
        persistence.load(this);
        view.draw();
    }

    private void write() {
        persistence.save(this);
    }

    public int getBlocks() {
        return blocks;
    }

    public void makeOldColor() {
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                if (matrix[x][y] == 10) {
                    matrix[x][y] = 11;
                }
            }
        }
        view.oneColor();
    }
}
