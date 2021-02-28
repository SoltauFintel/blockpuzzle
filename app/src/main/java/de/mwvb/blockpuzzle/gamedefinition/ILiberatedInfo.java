package de.mwvb.blockpuzzle.gamedefinition;

public interface ILiberatedInfo {

    int getPlayer1Score();
    int getPlayer1Moves();

    int getPlayer2Score();
    int getPlayer2Moves();

    /**
     * @return true if the player data are returned by getPlayer1Score/Moves()
     *                 OR it is sure that the playing field is empty
     */
    boolean isPlayerIsPlayer1();

    boolean isPlayingFieldEmpty();
}
