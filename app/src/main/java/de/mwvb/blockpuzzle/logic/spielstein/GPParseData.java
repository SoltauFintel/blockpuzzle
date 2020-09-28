package de.mwvb.blockpuzzle.logic.spielstein;

import java.util.ArrayList;
import java.util.List;

/**
 * Game Piece Parse Data
 */
public class GPParseData {
    public final List<GamePiece> allGamePieces = new ArrayList<>();
    public boolean read = true;
    public GamePiece current = null;
    public int y;
    public int n;
    public int r;
    public int rr;
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
