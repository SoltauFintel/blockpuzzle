package de.mwvb.blockpuzzle.logic.spielstein;

public class Spielstein2x3 extends Spielstein {

    public Spielstein2x3() {
        for (int x = 1; x <= 2; x++) {
            for (int y = 1; y <= 3; y++) {
                fill(x, y);
            }
        }
    }
}
