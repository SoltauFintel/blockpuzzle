package de.mwvb.blockpuzzle.gamedefinition;

import java.util.List;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.playingfield.OneColorAreaDetector;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Bei einem One Color Bereich kann der Spieler auf die Fläche klicken. Sie verschwindet dann. Die Blöcke darüber fallen herunter.
 */
public class Crush {
    private final PlayingField playingField;
    private final int blocks;
    private final ICrushed crushed;
    private List<QPosition> area;

    public Crush(PlayingField playingField, ICrushed crushed) {
        this.playingField = playingField;
        blocks = playingField.getBlocks();
        this.crushed = crushed;
    }

    public boolean crush(int px, int py) {
        boolean dirty = false;
        if (isCrushPossible(px, py)) {
            for (int y = 0; y < blocks; y++) {
                for (int x = 0; x < blocks; x++) {
                    if (area.contains(new QPosition(x, y))) {
                        changeBlock(x, y);
                        dirty = true;
                    }
                }
            }
        }
        if (dirty) {
            crushed.crushed(area.size());
            // eigentlich checken ob Rows voll sind
        }
        return dirty;
    }

    private boolean isCrushPossible(int x, int y) {
        area = null;
        int refBlockType = playingField.get(x, y);
        if (refBlockType == BlockTypes.ONE_COLOR || refBlockType == BlockTypes.OLD_ONE_COLOR) {
            area = new OneColorAreaDetector(playingField, 1).eineFarbe(x, y);
        }
        return area != null;
    }

    private void changeBlock(int x, int py) {
        playingField.set(x, py, 0);
        if (py > 0) {
            // Den Stein fallen lassen, so tief es geht.
            for (int y = py - 1; y >= 0; y--) {
                int v = playingField.get(x, y);
                if (v > 0) {
                    int tief = getTiefstePosition(x, y);
                    playingField.set(x, y, 0); // Reihenfolge nicht
                    playingField.set(x, tief, v);    // verändern!
                }
            }
        }
    }

    private int getTiefstePosition(int x, int fromY) {
        for (int y = fromY + 1; y < blocks; y++) {
            if (playingField.get(x, y) > 0) {
                return y - 1;
            }
        }
        return blocks - 1; // bis zum unteren Spielfeldrand alles frei -> Block in die letzte Zeile
    }
}
