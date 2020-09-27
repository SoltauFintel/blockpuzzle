package de.mwvb.blockpuzzle;

import de.mwvb.blockpuzzle.logic.spielstein.GamePieceReader;
import de.mwvb.blockpuzzle.logic.spielstein.GamePieces;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein2;

import org.junit.Assert;
import org.junit.Test;

public class GamePiecesTest {

    @Test
    public void testDefine() {
        // Test
        GamePiece p = new GamePieceReader().read("2");

        // Verify
        Assert.assertFalse(p.filled(0, 0));
        Assert.assertFalse(p.filled(1, 0));
        Assert.assertFalse(p.filled(2, 0));
        Assert.assertFalse(p.filled(3, 0));
        Assert.assertFalse(p.filled(4, 0));

        Assert.assertFalse(p.filled(0, 1));
        Assert.assertFalse(p.filled(1, 1));
        Assert.assertFalse(p.filled(2, 1));
        Assert.assertFalse(p.filled(3, 1));
        Assert.assertFalse(p.filled(4, 1));

        Assert.assertFalse(p.filled(0, 2));
        Assert.assertTrue("1/2 not set", p.filled(1, 2));
        Assert.assertTrue("2/2 not set", p.filled(2, 2));
        Assert.assertFalse(p.filled(3, 2));
        Assert.assertFalse(p.filled(4, 2));

        Assert.assertFalse(p.filled(0, 3));
        Assert.assertFalse(p.filled(1, 3));
        Assert.assertFalse(p.filled(2, 3));
        Assert.assertFalse(p.filled(3, 3));
        Assert.assertFalse(p.filled(4, 3));

        Assert.assertFalse(p.filled(0, 4));
        Assert.assertFalse(p.filled(1, 4));
        Assert.assertFalse(p.filled(2, 4));
        Assert.assertFalse(p.filled(3, 4));
        Assert.assertFalse(p.filled(4, 4));
    }
}
