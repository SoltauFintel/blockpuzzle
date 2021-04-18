package de.mwvb.blockpuzzle.playingfield;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.block.BlockTypes;

public class OneColorAreaDetector {
    private final PlayingField playingField;
    private final int blocks;
    private final int minCount;

    private int[][] visit;

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
                if (v > 0 && v != BlockTypes.ONE_COLOR && v != BlockTypes.OLD_ONE_COLOR) {
                    List<QPosition> result = eineFarbe(x, y);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public List<QPosition> eineFarbe(int x, int y) {
        List<QPosition> area = new ArrayList<>();
        visit = new int[blocks][blocks];
        eineFarbeR(x, y, area);
        return area.size() >= minCount ? area : null;
    }

    private void eineFarbeR(int x, int y, List<QPosition> ret) {
        final int ref = playingField.get(x, y);
        check(x - 1, y, ref, ret);
        check(x + 1, y, ref, ret);
        check(x, y - 1, ref, ret);
        check(x, y + 1, ref, ret);
    }

    private void check(int x, int y, int ref, List<QPosition> ret) {
        if (x >= 0 && x < blocks && y >= 0 && y < blocks) { // Parameter gültig?
            if (visit[x][y] == 0 && playingField.get(x, y) == ref) {
                ret.add(new QPosition(x, y));
                visit[x][y] = 1;
                eineFarbeR(x, y, ret);
            }
        }
    }
}
