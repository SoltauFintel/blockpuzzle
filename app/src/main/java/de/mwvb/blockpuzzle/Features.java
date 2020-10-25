package de.mwvb.blockpuzzle;

public interface Features {

    // Endlosspiel, Highscore-Maximierung
    // Ende: Game over
    String GAME_MODE_CLASSIC = "classic";
    // Mit möglichst wenigen Moves und geringer Score(?) das Spielfeld leeren.
    // Ende: gewonnen oder Game over
    String GAME_MODE_CLEANER = "cleaner";

    /**
     * true: player must shake smartphone to trigger gravitation
     * false: game does auto gravitation
     */
    boolean shakeForGravitation = false;
}
