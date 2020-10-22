package de.mwvb.blockpuzzle.logic;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.entity.QPosition;

public class OneColorAreaDetector {
    private final PlayingField playingField;
    private final int blocks;
    private final int minCount;

    private int[][] visit;
    private List<QPosition> ret;

    public OneColorAreaDetector(PlayingField playingField, int minCount) {
        this.playingField = playingField;
        blocks = playingField.getBlocks();
        this.minCount = minCount;
    }

    // Es gibt die Annahme dass ein einfarbiger Stein gesetzt worden ist.
    public List<QPosition> getOneColorArea() {
        // Gibt es einen zusammenhängenden Bereich mit einer Farbe?
        // Alle Blöcke untersuchen
        for (int y = 0; y < blocks; y++) {
            for (int x = 0; x < blocks; x++) {
                int v = playingField.get(x, y);
                if (v > 0 && v != 10 && v != 11) {
                    visit = new int[blocks][blocks];
                    ret = new ArrayList<>();
                    eineFarbe(x, y);
                    if (ret.size() >= minCount) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    private void eineFarbe(int x, int y) {
        final int ref = playingField.get(x, y);
        check(x - 1, y, ref);
        check(x + 1, y, ref);
        check(x, y - 1, ref);
        check(x, y + 1, ref);
    }

    private void check(int x, int y, int ref) {
        if (x >= 0 && x < blocks && y >= 0 && y < blocks) { // Parameter gültig?
            if (visit[x][y] == 0 && playingField.get(x, y) == ref) {
                ret.add(new QPosition(x, y));
                visit[x][y] = 1;
                eineFarbe(x, y);
            }
        }
    }
}
