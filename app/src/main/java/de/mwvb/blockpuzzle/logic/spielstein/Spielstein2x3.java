package de.mwvb.blockpuzzle.logic.spielstein;

/** Long beard */
public class Spielstein2x3 extends GamePiece {

    public Spielstein2x3() {
        for (int x = 1; x <= 2; x++) {
            for (int y = 1; y <= 3; y++) {
                fill(x, y);
            }
        }
    }
}
