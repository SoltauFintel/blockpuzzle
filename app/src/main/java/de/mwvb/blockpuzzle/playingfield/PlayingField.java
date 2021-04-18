package de.mwvb.blockpuzzle.playingfield;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.GameEngineBuilder;
import de.mwvb.blockpuzzle.gamedefinition.Crush;
import de.mwvb.blockpuzzle.gamedefinition.ICrushed;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamestate.Spielstand;

import static de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult.FITS;
import static de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult.FITS_ROTATED;
import static de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult.NO_GAME_PIECE;
import static de.mwvb.blockpuzzle.playingfield.GamePieceMatchResult.NO_SPACE;

public class PlayingField {
    // Stammdaten
    private final int blocks;
    /** null: crush not allowed */
    private ICrushed crushed;

    // Zustand
    /** 1: x (nach rechts), 2: y (nach unten) */
    private final int[][] matrix;
    private boolean gameOver = false;

    // Services
    private IPlayingFieldView view;

    // TO-DO Idee: Jeder Block sollte ein Objekt sein, welches Eigenschaften (z.B. Farbe) und Verhalten (z.B. LockBlock) hat.

    public PlayingField(int blocks) {
        this.blocks = blocks;
        matrix = new int[blocks][blocks];
    }

    public void setCrushed(ICrushed crushed) {
        this.crushed = crushed;
    }

    public static boolean isEmpty(Spielstand ss) {
        PlayingField pf = new PlayingField(GameEngineBuilder.blocks);
        pf.doLoad(ss);
        return pf.getFilled() == 0;
    }

    public void setView(IPlayingFieldView view) {
        this.view = view;
        this.view.setPlayingField(this);
    }

    public int get(int x, int y) {
        return matrix[x][y];
    }

    // Soll private bleiben, da nur die Game Engine die Matrix verändern darf.
    // -> Hab ich jetzt aber public dmachen müssen, damit Persistence darauf zugreifen kann.
    public void set(int x, int y, int value) {
        matrix[x][y] = value;
    }

    public void draw() {
        view.draw();
    }

    public void clear() {
        gameOver = false;
        doClear();
        view.draw();
    }
    private void doClear() {
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                set(x, y, 0);
            }
        }
    }

    /**
     * @param gamePiece game piece to check if it fits into playing field at any position with any rotation
     * @return detailed result
     */
    public GamePieceMatchResult match(GamePiece gamePiece) {
        if (gamePiece == null) {
            return NO_GAME_PIECE;
        }
        for (int ro = 1; ro <= 4; ro++) { // try all 4 rotations
            for (int x = 0; x < blocks; x++) {
                for (int y = 0; y < blocks; y++) {
                    if (match(gamePiece, x, y)) {
                        // GamePiece fits into playing field.
                        // original rotation (ro=1): not grey
                        // rotated (ro>1): grey, because with original rotation it doesn't fit
                        // and therefore I want to inform the player that he must rotate until
                        // it's not grey (or it's game over but there's a game over sound
                        // and he cannot rotate any more).
                        return ro > 1 ? FITS_ROTATED : FITS; // Spielstein passt rein
                    }
                }
            }
            gamePiece = gamePiece.copy().rotateToRight();
        }
        return NO_SPACE;
    }

    public boolean match(GamePiece teil, QPosition pos) {
        return match(teil, pos.getX(), pos.getY());
    }

    private boolean match(GamePiece teil, int posX, int posY) {
        for (int x = teil.getMinX(); x <= teil.getMaxX(); x++) {
            for (int y = teil.getMinY(); y <= teil.getMaxY(); y++) {
                if (teil.filled(x, y)) {
                    int ax = posX + x - teil.getMinX();
                    int ay = posY + y - teil.getMinY();
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
    }

    public FilledRows createFilledRows() {
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
    }

    public void gameOver() {
        gameOver = true;
        view.draw();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void load(Spielstand ss) {
        doLoad(ss);
        view.draw();
    }
    public void doLoad(Spielstand ss) {
        String d = ss.getPlayingField();
        if (d == null || d.isEmpty()) {
            doClear();
        } else {
            String[] w = d.split(",");
            int i = 0;
            for (int x = 0; x < blocks; x++) {
                for (int y = 0; y < blocks; y++) {
                    set(x, y, Integer.parseInt(w[i++]));
                }
            }
        }
    }

    public void save(Spielstand ss) {
        StringBuilder d = new StringBuilder();
        String k = "";
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                d.append(k);
                k = ",";
                d.append(get(x, y));
            }
        }
        ss.setPlayingField(d.toString());
    }

    public int getBlocks() {
        return blocks;
    }

    public void makeOldColor() {
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                if (matrix[x][y] == BlockTypes.ONE_COLOR) {
                    matrix[x][y] = BlockTypes.OLD_ONE_COLOR;
                }
            }
        }
        view.oneColor();
    }

    public void onTouch(int x, int y) {
        if (crushed != null && new Crush(this, crushed).crush(x, y)) {
            draw();
            view.oneColor();
        }
    }
}
