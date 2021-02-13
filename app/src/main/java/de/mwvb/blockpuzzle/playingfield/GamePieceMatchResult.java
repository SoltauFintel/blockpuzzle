package de.mwvb.blockpuzzle.playingfield;

public enum GamePieceMatchResult {

    /** game piece fits into playing field but needs rotation before (grey=true) */
    FITS_ROTATED,

    /** game piece fits into playing field */
    FITS,

    /** There's no space for the game piece in the playing field. */
    NO_SPACE,

    /** There's no game piece. */
    NO_GAME_PIECE
}
