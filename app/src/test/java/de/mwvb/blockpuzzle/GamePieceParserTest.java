package de.mwvb.blockpuzzle;

import de.mwvb.blockpuzzle.logic.spielstein.GamePieceParser;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.logic.spielstein.GamePieces;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GamePieceParserTest {

    @Test
    public void testDefine() {
        // Test
        GamePiece p = GamePieces.find("2");

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

    @Test
    public void testMinusOneRow() {
        try {
            List<GamePiece> list = new GamePieceParser().parse("#Test\n.....\n.....\n..1..\n.....\n");
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("5 rows"));
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("(A)"));
        }
    }

    @Test
    public void testPlusOneRow() {
        try {
            List<GamePiece> list = new GamePieceParser().parse(
                    "#Test\n.....\n.....\n..1..\n.....\n.....\n.....\n");
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("5 rows"));
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("(B)"));
        }
    }
    @Test
    public void testMissingName() {
        try {
            List<GamePiece> list = new GamePieceParser().parse(
                    "\n.....\n.....\n..1..\n.....\n.....\n");
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("Missing name"));
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("(C)"));
        }
    }

    @Test
    public void testNoGamePieces() {
        try {
            List<GamePiece> list = new GamePieceParser().parse(
                    "/*\n\n#Test\n.....\n.....\n..1..\n.....\n.....\n*/\n\n");
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("No game pieces"));
        }
    }

    @Test
    public void testMindestpunktzahl() {
        List<GamePiece> list = new GamePieceParser().parse(
                "#Test\nmin=2000\n.....\n.....\n..1..\n.....\n.....\n");

        Assert.assertEquals(2000, list.get(0).getMindestpunktzahl());
    }

    @Test
    public void testN() {
        List<GamePiece> list = new GamePieceParser().parse(
                "#Test\nn=3\n.....\n.....\n..1..\n.....\n.....\n");

        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testR() {
        String def1 = ".....\n" +
                ".111.\n" +
                ".1...\n" +
                ".1...\n" +
                ".....\n";
        String defR = ".....\n" +
                ".111.\n" +
                "...1.\n" +
                "...1.\n" +
                ".....\n";
        List<GamePiece> list = new GamePieceParser().parse(
                "#Test\n" +
                        "R=1\n" +
                        def1);

        Assert.assertEquals(2, list.size());
        Assert.assertEquals(def1, list.get(0).getStringPresentation());
        Assert.assertEquals(defR, list.get(1).getStringPresentation());
    }

    @Test
    public void testRR() {
        int m = 10; // also test this: "RR=10" is 5 chars long and it's not a layout line!
        String def1 = ".....\n" +
                ".111.\n" +
                ".1...\n" +
                ".1...\n" +
                ".....\n";
        String defRR = ".....\n" +
                "...1.\n" +
                "...1.\n" +
                ".111.\n" +
                ".....\n";
        List<GamePiece> list = new GamePieceParser().parse(
                "#Test\n" +
                        "RR=" + m + "\n" +
                        def1);

        Assert.assertEquals(1 + m, list.size());
        Assert.assertEquals(def1, list.get(0).getStringPresentation());
        for (int i = 1; i < m; i++) {
            Assert.assertEquals("Error #" + i, defRR, list.get(i).getStringPresentation());
        }
    }

    @Test
    public void testL() {
        String def1 = ".....\n" +
                ".111.\n" +
                ".1...\n" +
                ".1...\n" +
                ".....\n";
        String defL = ".....\n" +
                ".1...\n" +
                ".1...\n" +
                ".111.\n" +
                ".....\n";
        List<GamePiece> list = new GamePieceParser().parse(
                "#Test\n" +
                        "L=1\n" +
                        def1);

        Assert.assertEquals(2, list.size());
        Assert.assertEquals(def1, list.get(0).getStringPresentation());
        Assert.assertEquals(defL, list.get(1).getStringPresentation());
    }
}
