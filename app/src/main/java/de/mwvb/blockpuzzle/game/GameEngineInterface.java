package de.mwvb.blockpuzzle.game;

/**
 * GameEngineInterfaceForPlaceActions
 *
 * PlaceAction können mit diesem internen Interface die GameEngine Klasse aufrufen,
 * brauch ich insbesondere für Stone Wars (Check4VictoryPlaceAction)
 */
public interface GameEngineInterface {

    /**
     * Save game state
     */
    void save();

    /**
     * This is called to make the game pieces in the holders grey or not.
     * @return true if no game piece move is possible, false if there's at least one game piece that can be moved
     */
    boolean checkIfNoMoveIsPossible();

    /**
     * Event: game ends with success or lost game. Game could possibly goes on.
     * @param wonGame true: won game, false: lost game
     * @param stopGame true: display game over, false: game play goes on
     */
    void onEndGame(boolean wonGame, boolean stopGame);

    /**
     * Clear all four game pieces below playing field
     */
    void clearAllHolders();
}
