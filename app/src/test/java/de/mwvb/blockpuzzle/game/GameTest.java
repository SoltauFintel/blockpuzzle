package de.mwvb.blockpuzzle.game;

import org.junit.Assert;
import org.junit.Test;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public class GameTest extends AbstractBlockPuzzleTest {

    @Test
    public void newGame() {
        Assert.assertFalse("Game must not be over", game.isGameOver());
        Assert.assertEquals("Score must be 0 at game start", 0, game.getScore());
        Assert.assertEquals("Moves must be 0 at game start", 0, game.getMoves());

        // Playing field must be empty
        for (int x = 0; x < Game.blocks; x++) {
            for (int y = 0; y < Game.blocks; y++) {
                Assert.assertEquals("Playing field must be empty [" + x + ";" + y + "]", 0, game.get(x, y));
            }
        }
    }

    /** Alle Spielecken mal ausloten */
    @Test
    public void fourCorners() {
        game.dispatch(false, 1, one, new QPosition(0, 0));
        Assert.assertEquals(1, game.getScore());
        game.dispatch(false, 2, one, new QPosition(9, 0));
        Assert.assertEquals(2, game.getScore());
        game.dispatch(false, 3, one, new QPosition(0, 9));
        Assert.assertEquals(3, game.getScore());
        game.dispatch(false, 1, one, new QPosition(9, 9));
        Assert.assertEquals(4, game.getScore());

        Assert.assertEquals(3, game.get(0, 0));
        Assert.assertEquals(3, game.get(9, 0));
        Assert.assertEquals(3, game.get(0, 9));
        Assert.assertEquals(3, game.get(9, 9));
    }

    @Test
    public void blockNotFree() {
        game.dispatch(false, 1, five, new QPosition(0, 0));
        try {
            game.dispatch(false, 2, five, new QPosition(0, 0));
            Assert.fail("expected DoesNotWorkException");
        } catch (DoesNotWorkException expected) {
        }
    }

    @Test
    public void blockOutside() {
        game.dispatch(false, 1, two, new QPosition(8, 0));
        try {
            game.dispatch(false, 1, two, new QPosition(9, 1));
            Assert.fail("expected DoesNotWorkException");
        } catch (DoesNotWorkException expected) {
        }
    }

    @Test
    public void fullRow() {
        game.dispatch(false, 1, five, new QPosition(0, 0));
        game.dispatch(false, 2, five, new QPosition(5, 0));
        game.dispatch(false, 3, five, new QPosition(0, 0)); // jetzt ist da wieder Platz
        // TODO Bonuspunkte für Test ausschalten!
        //      Auch zeitlich verzögerte Aktionen sind störend. (wobei ich warten könnte)
    }

    @Test
    public void fullColumn() {
        game.dispatch(false, 1, fiveR, new QPosition(0, 0));
        game.dispatch(false, 2, fiveR, new QPosition(0, 5));
        game.dispatch(false, 3, fiveR, new QPosition(0, 0)); // jetzt ist da wieder Platz
    }

    @Test
    public void doesNotfitIn() {
        // fill playing field (without full rows)
        game.dispatch(false, 1, x, new QPosition(0, 1));
        game.dispatch(false, 2, x, new QPosition(3, 1));
        game.dispatch(false, 3, x, new QPosition(6, 1));
        game.dispatch(false, 1, x, new QPosition(0, 4));
        game.dispatch(false, 2, x, new QPosition(3, 4));
        game.dispatch(false, 3, x, new QPosition(6, 4));
        game.dispatch(false, 1, x, new QPosition(0, 7));
        game.dispatch(false, 2, x, new QPosition(3, 7));
        Assert.assertEquals(0, game.moveImpossibleR(x));
        game.dispatch(false, 3, x, new QPosition(6, 7));
        game.dispatch(false, 1, five, new QPosition(1, 0));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));

        // Test
        Assert.assertEquals(-1, game.moveImpossibleR(ecke3)); // -1: if you would rotate it would fit in -> it does not fit in
        try {
            game.dispatch(false, 2, ecke3, new QPosition(7, 0));
            Assert.fail("DoesNotWorkException expected");
        } catch (DoesNotWorkException expected) {
        }
    }

    @Test
    public void fitInIfRotated() {
        // fill playing field (without full rows)
        game.dispatch(false, 1, x, new QPosition(0, 1));
        game.dispatch(false, 2, x, new QPosition(3, 1));
        game.dispatch(false, 3, x, new QPosition(6, 1));
        game.dispatch(false, 1, x, new QPosition(0, 4));
        game.dispatch(false, 2, x, new QPosition(3, 4));
        game.dispatch(false, 3, x, new QPosition(6, 4));
        game.dispatch(false, 1, x, new QPosition(0, 7));
        game.dispatch(false, 2, x, new QPosition(3, 7));
        game.dispatch(false, 3, x, new QPosition(6, 7));
        game.dispatch(false, 1, five, new QPosition(1, 0));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));

        // Test
        Assert.assertEquals(-1, game.moveImpossibleR(ecke3)); // -1: if you would rotate it would fit in
        GamePiece ecke3R = ecke3.copy().rotateToRight().rotateToRight();
        Assert.assertEquals(0, game.moveImpossibleR(ecke3R)); // 0: fits in without rotation
        game.dispatch(false, 2, ecke3R, new QPosition(7, 0));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
    }

    // extra bonus for 2 filled rows: 12
    @Test
    public void twoRowsFilled() {
        game.dispatch(false, 1, block3, new QPosition(0, 0));
        game.dispatch(false, 2, block3, new QPosition(3, 0));
        game.dispatch(false, 3, three, new QPosition(6, 1));
        game.dispatch(false, 1, three, new QPosition(6, 2));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
        int oldScore = 3*3 * 2 + 3 * 2;
        Assert.assertEquals(oldScore, game.getScore());

        game.dispatch(false, 2, fiveR, new QPosition(9, 0));
        int bonus = 2 * 10 + 12;
        Assert.assertEquals(oldScore + 5 + bonus, game.getScore());
    }

    // extra bonus for 3 filled rows: 15
    @Test
    public void threeRowsFilled() {
        game.dispatch(false, 1, block3, new QPosition(0, 0));
        game.dispatch(false, 2, block3, new QPosition(3, 0));
        game.dispatch(false, 3, three, new QPosition(6, 0));
        game.dispatch(false, 1, three, new QPosition(6, 1));
        game.dispatch(false, 2, three, new QPosition(6, 2));
        game.dispatch(false, 3, block3, new QPosition(1, 6));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
        int oldScore = 3*3 * 3 + 3 * 3;
        Assert.assertEquals(oldScore, game.getScore());

        game.dispatch(false, 1, fiveR, new QPosition(9, 0));
        int bonus = 3 * 10 + 15;
        Assert.assertEquals(oldScore + 5 + bonus, game.getScore());
    }

    // extra bonus for 4+ rows: 22
    @Test
    public void fourRowsFilled() {
        game.dispatch(false, 1, block3, new QPosition(0, 0));
        game.dispatch(false, 2, block3, new QPosition(3, 0));
        game.dispatch(false, 3, three, new QPosition(6, 0));
        game.dispatch(false, 1, three, new QPosition(6, 1));
        game.dispatch(false, 2, three, new QPosition(6, 2));
        game.dispatch(false, 3, block3, new QPosition(1, 6));
        game.dispatch(false, 1, three, new QPosition(0, 3));
        game.dispatch(false, 2, three, new QPosition(3, 3));
        game.dispatch(false, 3, three, new QPosition(6, 3));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
        int oldScore = 3*3 * 3 + 3 * 6;
        Assert.assertEquals(oldScore, game.getScore());

        game.dispatch(false, 1, fiveR, new QPosition(9, 0));
        int bonus = 4 * 10 + 22;
        Assert.assertEquals(oldScore + 5 + bonus, game.getScore());
    }

    // extra bonus for 3*7 blocks of same color: 105
    @Test
    public void oneColorDetect() {
        game.dispatch(false, 1, block3, new QPosition(0, 0));
        game.dispatch(false, 2, three, new QPosition(6, 0));
        game.dispatch(false, 3, three, new QPosition(6, 1));
        game.dispatch(false, 1, three, new QPosition(6, 2));
        game.dispatch(false, 2, three, new QPosition(0, 3));
        game.dispatch(false, 3, three, new QPosition(3, 3));
        game.dispatch(false, 1, three, new QPosition(6, 3));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
        Assert.assertEquals(3*3 + 3 * 6, game.getScore());

        game.dispatch(false, 2, three, new QPosition(3, 1));
        //System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
        Assert.assertEquals(3*3 + 3 * 7 + 105, game.getScore());
    }

    // TODO weitere Testideen
    // - Game over Situation
    // - nach Game-over wird new-game gewählt
    // - Nachdem ca. 5 Moves gespielt wurden, wird new-game gewählt
    // - coverage
}