package de.mwvb.blockpuzzle.game;

import org.junit.Before;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.TestGamePieces;

public abstract class AbstractBlockPuzzleTest {
    protected final GamePiece one = TestGamePieces.INSTANCE.getOne();
    protected final GamePiece two = TestGamePieces.INSTANCE.getTwo();
    protected final GamePiece three = TestGamePieces.INSTANCE.getThree();
    protected final GamePiece five = TestGamePieces.INSTANCE.getFive();
    protected final GamePiece fiveR = TestGamePieces.INSTANCE.getFive().copy().rotateToRight();
    protected final GamePiece ecke3 = TestGamePieces.INSTANCE.getEcke3();
    protected final GamePiece block3 = TestGamePieces.INSTANCE.getBlock3();
    protected final GamePiece x = TestGamePieces.INSTANCE.getX();
    protected Game game;

    @Before
    public void before() {
        game = TestGameBuilder.create();
    }
}
