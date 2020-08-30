package de.mwvb.blockpuzzle.logic.spielstein;

/** Tetris style J */
// TODO Den auch als L machen!
public class SpielsteinLangeEcke extends Spielstein {

    public SpielsteinLangeEcke() {
        fill(1, 1);
        fill(1, 2);
        fill(2, 2);
        fill(3, 2);
    }
}
