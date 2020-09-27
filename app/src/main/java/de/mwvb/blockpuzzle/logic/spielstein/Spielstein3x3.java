package de.mwvb.blockpuzzle.logic.spielstein;

/** Block 3x3 - Killer */
public class Spielstein3x3 extends GamePiece {

    public Spielstein3x3() {
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                fill(x, y);
            }
        }
    }
}
