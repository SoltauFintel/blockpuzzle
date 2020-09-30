package de.mwvb.blockpuzzle.logic.spielstein;

import java.util.ArrayList;
import java.util.List;

/**
 * Game Piece Parse Data
 */
public class GPParseData {
    public final List<GamePiece> allGamePieces = new ArrayList<>();
    /** false if comment active */
    public boolean read = true;
    public GamePiece current = null;
    /** row */
    public int y;
    /** save game piece n times (default 1) */
    public int n;
    /** copy right-rotated GP n times */
    public int r;
    /** copy twice right-rotated GP n times */
    public int rr;
    /** copy left-rotated GP n times */
    public int l;

    public GPParseData() {
        reset();
    }

    public void reset() {
        y = -1;
        n = 1;
        r = 0;
        rr = 0;
        l = 0;
    }
}
