package de.mwvb.blockpuzzle.gamepiece;

import org.junit.Assert;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.TestGameBuilder;

/**
 * Test for the special rotation of game pieces J and L
 */
public class JLRotationTest {
    final String defJ =
                    ".....\n" +
                    ".7...\n" +
                    ".777.\n" +
                    ".....\n" +
                    ".....\n";
    final String defL =
                    ".....\n" +
                    "...7.\n" +
                    ".777.\n" +
                    ".....\n" +
                    ".....\n";
    final String defS =
                    ".....\n" +
                    ".....\n" +
                    "..55.\n" +
                    ".55..\n" +
                    ".....\n";
    final String defZ =
                    ".....\n" +
                    ".....\n" +
                    ".55..\n" +
                    "..55.\n" +
                    ".....\n";

    @org.junit.Test
    public void testJ() {
        GamePiece theJ = new GamePieceParser().parse("#J\n" + defJ).get(0);

        // Test J -> L
        rotate4x(theJ);
        Assert.assertEquals("Test J -> L failed", defL, TestGameBuilder.getStringPresentation(theJ, new BlockTypes(null), true));

        // Test L -> J
        rotate4x(theJ);
        Assert.assertEquals("Test L -> J failed", defJ, TestGameBuilder.getStringPresentation(theJ, new BlockTypes(null), true));
    }

    @org.junit.Test
    public void testL() {
        GamePiece theL = new GamePieceParser().parse("#L\n" + defL).get(0);

        // Test L -> J
        rotate4x(theL);
        Assert.assertEquals("Test L -> J failed", defJ, TestGameBuilder.getStringPresentation(theL, new BlockTypes(null), true));

        // Test L -> J
        rotate4x(theL);
        Assert.assertEquals("Test J -> L failed", defL, TestGameBuilder.getStringPresentation(theL, new BlockTypes(null), true));
    }

    @org.junit.Test
    public void testS() {
        GamePiece theS = new GamePieceParser().parse("#S\n" + defS).get(0);

        // Test L -> J
        rotate4x(theS);
        Assert.assertEquals("Test S -> Z failed", defZ, TestGameBuilder.getStringPresentation(theS, new BlockTypes(null), true));

        // Test L -> J
        rotate4x(theS);
        Assert.assertEquals("Test J -> L failed", defS, TestGameBuilder.getStringPresentation(theS, new BlockTypes(null), true));
    }

    private void rotate4x(GamePiece gp) {
        gp.rotateToRight();
        gp.rotateToRight();
        gp.rotateToRight();
        gp.rotateToRight();
    }
}
