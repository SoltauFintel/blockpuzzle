package de.mwvb.blockpuzzle.logic.spielstein;

/** Tetris style J, alter Name: SpielsteinLangeEcke */
// TODO Den auch als L machen!
public class SpielsteinJ extends Spielstein {

    public SpielsteinJ() {
        fill(1, 1);
        fill(1, 2);
        fill(2, 2);
        fill(3, 2);
    }
}
