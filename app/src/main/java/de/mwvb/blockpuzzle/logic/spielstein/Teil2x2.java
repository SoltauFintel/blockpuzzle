package de.mwvb.blockpuzzle.logic.spielstein;

/** Block 2x2 */
public class Teil2x2 extends Spielstein {

    public Teil2x2() {
        for (int x = 1; x <= 2; x++) {
            for (int y = 1; y <= 2; y++) {
                fill(x, y);
            }
        }
    }
}
