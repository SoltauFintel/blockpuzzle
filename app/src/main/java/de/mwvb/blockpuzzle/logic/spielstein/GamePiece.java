package de.mwvb.blockpuzzle.logic.spielstein;

import org.jetbrains.annotations.NotNull;

/**
 * Teil Basisklasse und leeres Teil
 */
public class GamePiece {
    /** Breite und Höhe */
    public static final int max = 5;
    /** 1: x (nach rechts), 2: y (nach unten) */
    private int[][] matrix = new int[max][max];
    /** rotate temp matrix */
    private int[][] neu = new int[max][max];
    private int mindestpunktzahl = 0; // Wird nicht persistiert, da dieser Wert nicht mehr von Bedeutung ist, sobald der Spielstein im SpielsteinView gelandet ist.
    private int rotated = 0; // TODO Ich brauch hier die richtige Zahl.
    private String name;

    public GamePiece() {
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                matrix[x][y] = 0;
            }
        }
    }

    public GamePiece copy() {
        try {
            GamePiece n = (GamePiece) Class.forName(this.getClass().getName()).newInstance();
            for (int x = 0; x < max; x++) {
                System.arraycopy(matrix[x], 0, n.matrix[x], 0, max);
            }
            n.rotated = rotated;
            return n;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name == null ? this.getClass().getSimpleName() : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GamePiece withMindestpunktzahl(int minp) {
        mindestpunktzahl = minp;
        return this;
    }

    public boolean filled(int x, int y) {
        return matrix[x][y] != 0;
    }

    public int getBlockType(int x, int y) {
        return matrix[x][y];
    }

    protected final void fill(int x, int y) {
        matrix[x][y] = 1;
    }

    /**
     * @param value 0: leer, 1: Block
     *              weitere Werte für Boni denkbar:
     *              2: Dreh-Boni, 3: Dampfwalze, 4: Dynamit, 5: Bonuspunkte
     */
    public void setBlockType(int x, int y, int value) {
        matrix[x][y] = value;
    }

    public GamePiece rotateToLeft() {
        rotateToRight();
        rotateToRight();
        rotateToRight();
        return this;
    }

    public GamePiece rotateToRight() {
        // Kein Bock einen Algo zu programmieren. Ist immer eine 5x5 Matrix.
        // inneres Quadrat drehen
        transfer(1, 1, 3, 1);
        transfer(2, 1, 3, 2);
        transfer(3, 1, 3, 3);
        transfer(3, 2, 2, 3);
        transfer(3, 3, 1, 3);
        transfer(2, 3, 1, 2);
        transfer(1, 3, 1, 1);
        transfer(1, 2, 2, 1);
        // äußeres Quadrat drehen
        transfer(0, 0, 4, 0);
        transfer(1, 0, 4, 1);
        transfer(2, 0, 4, 2);
        transfer(3, 0, 4, 3);
        transfer(4, 0, 4, 4);
        transfer(4, 1, 3, 4);
        transfer(4, 2, 2, 4);
        transfer(4, 3, 1, 4);
        transfer(4, 4, 0, 4);
        transfer(3, 4, 0, 3);
        transfer(2, 4, 0, 2);
        transfer(1, 4, 0, 1);
        transfer(0, 4, 0, 0);
        transfer(0, 3, 1, 0);
        transfer(0, 2, 2, 0);
        transfer(0, 1, 3, 0);
        // Mittleres Feld übertragen (dreht sich nicht)
        transfer(2, 2, 2, 2);
        // Zurückübertragen
        for (int x = 0; x < 5; x++) {
            System.arraycopy(neu[x], 0, matrix[x], 0, 5);
        }
        // Anzahl der Drehungen merken
        rotated++;
        while (rotated >= 4) {
            rotated -= 4;
        }
        return this;
    }

    private void transfer(int sx, int sy, int tx, int ty) {
        neu[tx][ty] = matrix[sx][sy];
    }

    public int getMinX() {
        int ret = 0;
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                if (filled(x, y)) {
                    return x;
                }
            }
        }
        return -1;
    }

    public int getMaxX() {
        for (int x = max - 1; x >= 0; x--) {
            for (int y = 0; y < max; y++) {
                if (filled(x, y)) {
                    return x;
                }
            }
        }
        return -1;
    }

    public int getMinY() {
        for (int y = 0; y < max; y++) {
            for (int x = 0; x < max; x++) {
                if (filled(x, y)) {
                    return y;
                }
            }
        }
        return -1;
    }

    public int getMaxY() {
        for (int y = max - 1; y >= 0; y--) {
            for (int x = 0; x < max; x++) {
                if (filled(x, y)) {
                    return y;
                }
            }
        }
        return -1;
    }

    public int getPunkte() {
        int ret = 0;
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                if (filled(x, y)) {
                    ret++;
                }
            }
        }
        return ret;
    }

    /* Der Spieler muss mindestens diese Punktzahl haben, damit der Spielstein verfügbar wird. */
    public int getMindestpunktzahl() {
        return mindestpunktzahl;
    }

    @NotNull
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public int getRotated() {
        return rotated;
    }
}
