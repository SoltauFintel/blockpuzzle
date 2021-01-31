package de.mwvb.blockpuzzle.gamepiece;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Game pieces can have different shapes with maximum size to 5x5 blocks and minimum size of 1x1.
 */
public class GamePiece {
    /** maximum width and height */
    public static final int max = 5;
    /** 1: x (to right), 2: y (to bottom) */
    private final int[][] matrix = new int[max][max];
    /** rotate temp matrix */
    private final int[][] neu = new int[max][max];
    private int minimumMoves = 0; // Wird nicht persistiert, da dieser Wert nicht mehr von Bedeutung ist, sobald der Spielstein im GamePieceView gelandet ist.
    private String name; // Wird nicht persistiert, da dieser Wert nicht mehr von Bedeutung ist, sobald der Spielstein im GamePieceView gelandet ist.

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
            n.minimumMoves = minimumMoves;
            n.name = name;
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

    public boolean filled(int x, int y) {
        return matrix[x][y] != 0;
    }

    public int getBlockType(int x, int y) {
        return matrix[x][y];
    }

    /**
     * @param value 0: leer, 1: Block
     *              weitere Werte für Boni denkbar:
     *              2: Dreh-Boni, 3: Dampfwalze, 4: Dynamit, 5: Bonuspunkte
     */
    public void setBlockType(int x, int y, int value) {
        matrix[x][y] = value;
    }

    public void color(int newBlockType) {
        for (int y = 0; y < GamePiece.max; y++) {
            for (int x = 0; x < GamePiece.max; x++) {
                if (matrix[x][y] != 0) {
                    matrix[x][y] = newBlockType;
                }
            }
        }
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

        // Special handling for game pieces J and L
        String def = getStringPresentation(this);
        final String defJ =  ".....|.7...|.777.|.....|.....|";
        final String defL =  ".....|...7.|.777.|.....|.....|";
        if (defJ.equals(def)) {
            reverse(defL);
        } else if (defL.equals(def)) {
            reverse(defJ);
        }
        final String defS =  ".....|.....|..55.|.55..|.....|";
        final String defZ =  ".....|.....|.55..|..55.|.....|";
        if (defS.equals(def)) {
            reverse(defZ);
        } else if (defZ.equals(def)) {
            reverse(defS);
        }

        return this;
    }

    private void reverse(String def) {
        int i = 0;
        for (int y = 0; y < GamePiece.max; y++) {
            for (int x = 0; x < GamePiece.max; x++) {
                char c = def.charAt(i++);
                if (c == '5') {
                    setBlockType(x, y, 5);
                } else if (c == '6') {
                    setBlockType(x, y, 6);
                } else if (c == '7') {
                    setBlockType(x, y, 7);
                } else {
                    setBlockType(x, y, 0);
                }
            }
            i++; // jump over "|"
        }
    }

    private static String getStringPresentation(GamePiece p) {
        StringBuilder ret = new StringBuilder();
        for (int y = 0; y < GamePiece.max; y++) {
            for (int x = 0; x < GamePiece.max; x++) {
                final int blockType = p.getBlockType(x, y);
                if (blockType == 0) {
                    ret.append('.');
                } else if (blockType == 5) {
                    ret.append('5');
                } else if (blockType == 6) {
                    ret.append('6');
                } else if (blockType == 7) {
                    ret.append('7');
                } else {
                    ret.append('?');
                }
            }
            ret.append("|");
        }
        return ret.toString();
    }

    private void transfer(int sx, int sy, int tx, int ty) {
        neu[tx][ty] = matrix[sx][sy];
    }

    public int getMinX() {
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                if (filled(x, y)) {
                    return x;
                }
            }
        }
        return -1;
    }

    /** use <= */
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

    /** use <= */
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

    public int getScore() {
        int score = 0;
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                if (filled(x, y)) {
                    score++;
                }
            }
        }
        return score;
    }

    public int getMinimumMoves() {
        return minimumMoves;
    }

    public void setMinimumMoves(int v) {
        minimumMoves = v;
    }

    @NotNull
    public List<QPosition> getAllFilledBlocks() {
        List<QPosition> filledBlocks = new ArrayList<>();
        final int minX = getMinX();
        final int maxX = getMaxX();
        final int minY = getMinY();
        final int maxY = getMaxY();
        if (minX > -1 && maxX > -1 && minY > -1 && maxY > -1) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    if (filled(x, y)) {
                        filledBlocks.add(new QPosition(x, y));
                    }
                }
            }
        }
        return filledBlocks;
    }

    @NotNull
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
