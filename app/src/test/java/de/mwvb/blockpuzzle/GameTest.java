package de.mwvb.blockpuzzle;

import org.junit.Assert;
import org.junit.Test;

import de.mwvb.blockpuzzle.logic.DoesNotWorkException;
import de.mwvb.blockpuzzle.logic.Game;
import de.mwvb.blockpuzzle.entity.QPosition;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;

public class GameTest {

    @Test
    public void newGame() {
        Game game = GameForTest.create();
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
        Game game = GameForTest.create();
        GamePiece one = GamePiecesForTest.INSTANCE.getOne();
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
        Game game = GameForTest.create();
        GamePiece five = GamePiecesForTest.INSTANCE.getFive();
        game.dispatch(false, 1, five, new QPosition(0, 0));
        try {
            game.dispatch(false, 2, five, new QPosition(0, 0));
            Assert.fail("expected DoesNotWorkException");
        } catch (DoesNotWorkException expected) {
        }
    }

    @Test
    public void blockOutside() {
        Game game = GameForTest.create();
        GamePiece two = GamePiecesForTest.INSTANCE.getTwo();
        game.dispatch(false, 1, two, new QPosition(8, 0));
        try {
            game.dispatch(false, 1, two, new QPosition(9, 1));
            Assert.fail("expected DoesNotWorkException");
        } catch (DoesNotWorkException expected) {
        }
    }

    @Test
    public void fullRow() {
        Game game = GameForTest.create();
        GamePiece five = GamePiecesForTest.INSTANCE.getFive();
        game.dispatch(false, 1, five, new QPosition(0, 0));
        game.dispatch(false, 2, five, new QPosition(5, 0));
        game.dispatch(false, 3, five, new QPosition(0, 0)); // jetzt ist da wieder Platz
        // TODO Bonuspunkte für Test ausschalten!
        //      Auch zeitlich verzögerte Aktionen sind störend. (wobei ich warten könnte)
    }

    @Test
    public void fullColumn() {
        Game game = GameForTest.create();
        GamePiece five = GamePiecesForTest.INSTANCE.getFive().copy().rotateToRight();
        game.dispatch(false, 1, five, new QPosition(0, 0));
        game.dispatch(false, 2, five, new QPosition(0, 5));
        game.dispatch(false, 3, five, new QPosition(0, 0)); // jetzt ist da wieder Platz
    }

    @Test
    public void doesNotfitIn() {
        Game game = GameForTest.create();
        // fill playing field (without full rows)
        GamePiece x = GamePiecesForTest.INSTANCE.getX();
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
        GamePiece five = GamePiecesForTest.INSTANCE.getFive();
        game.dispatch(false, 1, five, new QPosition(1, 0));
        //System.out.println(GameForTest.getPlayingFieldAsString(game));

        // Test
        GamePiece ecke3 = GamePiecesForTest.INSTANCE.getEcke3();
        Assert.assertEquals(-1, game.moveImpossibleR(ecke3)); // -1: if you would rotate it would fit in -> it does not fit in
        try {
            game.dispatch(false, 2, ecke3, new QPosition(7, 0));
            Assert.fail("DoesNotWorkException expected");
        } catch (DoesNotWorkException expected) {
        }
    }

    @Test
    public void fitInIfRotated() {
        Game game = GameForTest.create();
        // fill playing field (without full rows)
        GamePiece x = GamePiecesForTest.INSTANCE.getX();
        game.dispatch(false, 1, x, new QPosition(0, 1));
        game.dispatch(false, 2, x, new QPosition(3, 1));
        game.dispatch(false, 3, x, new QPosition(6, 1));
        game.dispatch(false, 1, x, new QPosition(0, 4));
        game.dispatch(false, 2, x, new QPosition(3, 4));
        game.dispatch(false, 3, x, new QPosition(6, 4));
        game.dispatch(false, 1, x, new QPosition(0, 7));
        game.dispatch(false, 2, x, new QPosition(3, 7));
        game.dispatch(false, 3, x, new QPosition(6, 7));
        GamePiece five = GamePiecesForTest.INSTANCE.getFive();
        game.dispatch(false, 1, five, new QPosition(1, 0));
        //System.out.println(GameForTest.getPlayingFieldAsString(game));

        // Test
        GamePiece ecke3 = GamePiecesForTest.INSTANCE.getEcke3();
        Assert.assertEquals(-1, game.moveImpossibleR(ecke3)); // -1: if you would rotate it would fit in
        ecke3 = ecke3.copy().rotateToRight().rotateToRight();
        Assert.assertEquals(0, game.moveImpossibleR(ecke3)); // 0: fits in without rotation
        game.dispatch(false, 2, ecke3, new QPosition(7, 0));
        //System.out.println(GameForTest.getPlayingFieldAsString(game));
    }

    // TODO mehrere Rows abräumen (Bonuskontrolle)

    // TODO one color detect

    // TODO Game over bug: Gravitation schafft doch noch Platz für GP

    // TODO Game over Situation

    // TODO nach Game-over wird new-game gewählt
    // TODO Nachdem ca. 5 Moves gespielt wurden, wird new-game gewählt

    // TODO coverage
}