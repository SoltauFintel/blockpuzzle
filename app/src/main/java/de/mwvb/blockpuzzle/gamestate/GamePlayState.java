package de.mwvb.blockpuzzle.gamestate;

public enum GamePlayState {

    /**
     * Der Spieler kann nur im PLAYING Status noch Spielsteine setzen. Beim Classic Game
     * kann es durchaus sein, dass zwischenzeitlich MLS oder EnemyScore übertroffen worden sind.
     */
    PLAYING,

    /** z.B. wenn Spielfeld voll */
    LOST_GAME,

    /** Gibt's nur beim Cleaner Game. Ein Weiterspielen bei Spielsieg macht kein Sinn. */
    WON_GAME
}
