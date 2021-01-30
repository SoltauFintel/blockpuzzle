package de.mwvb.blockpuzzle.game;

/**
 * GameEngineInterfaceForPlaceActions
 *
 * PlaceAction können mit diesem internen Interface die Game Klasse aufrufen,
 * brauch ich insbesondere für Stone Wars (Check4VictoryPlaceAction)
 */
public interface GameEngineInterface {

    void save();

    void checkPossibleMoves();

    void onGameOver();

    void clearAllHolders();
}
