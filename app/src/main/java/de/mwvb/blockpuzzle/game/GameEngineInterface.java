package de.mwvb.blockpuzzle.game;

/**
 * GameEngineInterfaceForPlaceActions
 *
 * PlaceAction können mit diesem internen Interface die GameEngine Klasse aufrufen,
 * brauch ich insbesondere für Stone Wars (Check4VictoryPlaceAction)
 */
public interface GameEngineInterface {

    void save();

    /**
     * This is called to make the game pieces in the holders grey or not.
     * @return true if no game piece move is possible, false if there's at least one game piece that can be moved
     */
    boolean checkIfNoMoveIsPossible();

    void onLostGame();

    void clearAllHolders();
}
