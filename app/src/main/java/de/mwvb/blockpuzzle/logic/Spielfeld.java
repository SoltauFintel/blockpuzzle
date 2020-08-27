package de.mwvb.blockpuzzle.logic;

import de.mwvb.blockpuzzle.logic.spielstein.Spielstein;

public class Spielfeld {
    private final int blocks;
    /** 1: x (nach rechts), 2: y (nach unten) */
    private int[][] matrix;

    public Spielfeld(int blocks) {
        this.blocks = blocks;
        matrix = new int[blocks][blocks];
    }

    public int get(int x, int y) {
        return matrix[x][y];
    }

    // Soll private bleiben, da nur die Game Engine die Matrix verändern darf.
    public void set(int x, int y, int value) {
        matrix[x][y] = value;
    }

    public void clear() {
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                set(x, y, 0);
            }
        }
        // TODO Demo
//        set(0, 0, 1);
//        set(0, blocks - 1, 1);
//        set(blocks - 1, 0, 1);
//        set(blocks - 1, blocks - 1, 1);
    }

    public boolean match(Spielstein teil, QPosition pos) {
        for (int x = teil.getMinX(); x <= teil.getMaxX(); x++) {
            for (int y = teil.getMinY(); y <= teil.getMaxY(); y++) {
                if (teil.filled(x, y)) {
                    int ax = pos.getX() + x - teil.getMinX();
                    int ay = pos.getY() + y - teil.getMinY();
                    if (ax < 0 || ax >= blocks || ay < 0 || ay >= blocks) {
                        debug("match false wegen out of bounds / " + teil);
                        return false;
                    }
                    int v = get(ax, ay);
                    if (v > 0 && v < 30) {
                        debug("match false / " + v + " / " + teil
                                + " / " + pos.getX() + "x" + pos.getY() + " / x="+x
                                + " (min=" + teil.getMinX() + "), y="+y
                                + " (min=" + teil.getMinY() + ")");
                        return false;
                    }
                }
            }
        }
        debug("match TRUE / " + teil);
        return true;
    }

    /** Male Teil ins Spielfeld! */
    public void platziere(Spielstein teil, QPosition pos) {
        for (int x = teil.getMinX(); x <= teil.getMaxX(); x++) {
            for (int y = teil.getMinY(); y <= teil.getMaxY(); y++) {
                if (teil.filled(x, y)) {
                    int ax = pos.getX() + x - teil.getMinX();
                    int ay = pos.getY() + y - teil.getMinY();
                    set(ax, ay, teil.getBlockType(x, y));
                }
            }
        }
    }

    public FilledRows getFilledRows() {
        FilledRows ret = new FilledRows();
        for (int y = 0; y < blocks; y++) {
            if (x_gefuellt(y)) {
                ret.getYlist().add(y);
            }
        }
        for (int x = 0; x < blocks; x++) {
            if (y_gefuellt(x)) {
                ret.getXlist().add(x);
            }
        }
        return ret;
    }

    private boolean x_gefuellt(int y) {
        for (int x = 0; x < blocks; x++) {
            if (get(x, y) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean y_gefuellt(int x) {
        for (int y = 0; y < blocks; y++) {
            if (get(x, y) == 0) {
                return false;
            }
        }
        return true;
    }

    public void clearRows(FilledRows f) {
        for (int x : f.getXlist()) {
            for (int y = 0; y < blocks; y++) {
                set(x, y, 0);
            }
        }
        for (int y : f.getYlist()) {
            for (int x = 0; x < blocks; x++) {
                set(x, y, 0);
            }
        }
    }

    private void debug(String msg) {
        System.out.println(msg);
    }
}
