package de.mwvb.blockpuzzle.gravitation;

import org.junit.Assert;
import org.junit.Test;

import de.mwvb.blockpuzzle.game.Game;
import de.mwvb.blockpuzzle.game.TestGameBuilder;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.TestGamePieces;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public class GravitationTest {

    @Test
    public void gravitation4() {
        try {
            gravitation(4);
            Assert.fail("Exception expected");
        } catch (Throwable e) {
            Assert.assertTrue(e.getMessage().contains("Gravitation did not work!"));
        }
    }

    @Test
    public void gravitation5() {
        gravitation(5);
    }

    @Test
    public void gravitation6() {
        gravitation(6);
    }

    @Test
    public void gravitation7() {
        gravitation(7);
    }

    @Test
    public void gravitation8() {
        gravitation(8);
    }

    @Test
    public void gravitation9() {
        gravitation(9);
    }

    private void gravitation(int row) {
        // Prepare
        Game game = TestGameBuilder.create();
        GamePiece one = TestGamePieces.INSTANCE.getOne();
        GamePiece block3 = TestGamePieces.INSTANCE.getBlock3();
        game.dispatch(false, 1, one, new QPosition(0, 0));
        game.dispatch(false, 1, block3, new QPosition(5, 1)); // prevent empty PF bonus
        for (int x = 0; x < Game.blocks - 1; x++) {
            game.dispatch(false, 1, one, new QPosition(x, row));
        }
        game.dispatch(false, 1, one, new QPosition(0, row == 8 ? 7 : 8));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));

        // Test
        game.dispatch(false, 1, one, new QPosition(9, row));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));

        // Verify
        Assert.assertEquals(20 + 1 + 10, game.getScore());
        Assert.assertNotEquals("Gravitation did not work! Game piece did not drop down!", 0, game.get(0, 1));
        Assert.assertEquals("Old game piece position must be free!", 0, game.get(0, 0));
        Assert.assertEquals("Last row must be gone!", 0, game.get(9, row));
    }

    // TODO Naja, so richtig testet dieser Testfall das nicht, da hier nicht die Verzögerung statt findet.
    @Test
    public void gameOverBug() {
        // Prepare
        Game game = TestGameBuilder.create();
        GamePiece one = TestGamePieces.INSTANCE.getOne();
        GamePiece x = TestGamePieces.INSTANCE.getX();
        GamePiece block3 = TestGamePieces.INSTANCE.getBlock3();
        GamePiece three = TestGamePieces.INSTANCE.getThree();
        GamePiece ecke3 = TestGamePieces.INSTANCE.getEcke3().copy().rotateToRight().rotateToRight();

        game.dispatch(false, 1, x, new QPosition(0, 0));
        game.dispatch(false, 2, x, new QPosition(3, 0));
        game.dispatch(false, 3, x, new QPosition(6, 0));
        game.dispatch(false, 1, x, new QPosition(0, 3));
        game.dispatch(false, 2, x, new QPosition(3, 3));
        game.dispatch(false, 3, x, new QPosition(6, 3));
        game.dispatch(false, 1, x, new QPosition(0, 6));
        game.dispatch(false, 2, x, new QPosition(3, 6));
        game.dispatch(false, 3, x, new QPosition(6, 6));

        game.dispatch(false, 1, three, new QPosition(0, 9));
        game.dispatch(false, 2, three, new QPosition(3, 9));
        game.dispatch(false, 3, three, new QPosition(6, 9));
        game.dispatch(false, 1, one, new QPosition(9, 7)); // prevent ecke3
        game.dispatch(true, 2, block3, new QPosition(9, 9)); // fill parking area
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));

        game.dispatch(false, 3, one, new QPosition(9, 9)); // fill row -> gravitationb
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));

        game.dispatch(false, 3, ecke3, new QPosition(7, 0));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
        Assert.assertFalse(game.isGameOver());
    }
}
