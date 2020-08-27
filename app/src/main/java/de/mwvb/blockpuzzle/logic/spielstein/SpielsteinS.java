package de.mwvb.blockpuzzle.logic.spielstein;

/** Tetris style S/Z */
public class SpielsteinS extends Spielstein {

    public SpielsteinS() {
        fill(1, 3);
        fill(2, 3);
        fill(2, 2);
        fill(3, 2);
    }
}
