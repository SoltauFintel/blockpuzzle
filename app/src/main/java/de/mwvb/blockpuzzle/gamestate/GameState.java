package de.mwvb.blockpuzzle.gamestate;

/**
 * GameState classes are immutable Spielstand wrapper. If something would change a new object would be created.
 */
public class GameState {
    private final Spielstand ss;

    protected GameState(Spielstand ss) {
        this.ss = ss;
    }

    public static GameState create() {
        return new GameState(new SpielstandDAO().loadOldGame());
    }

    public Spielstand get() {
        return ss;
    }

    public void save() {
        new SpielstandDAO().saveOldGame(ss);
    }

    public void newGame() {
        ss.setState(GamePlayState.PLAYING);
        ss.setScore(0);
        ss.setDelta(0);
        ss.setMoves(0);
        ss.setEmptyScreenBonusActive(false);
    }

    /**
     * @return true if lost game, false if game goes on or won game
     */
    public boolean isLostGame() {
        return ss.getState() == GamePlayState.LOST_GAME;
    }

    public boolean isWonOrLostGame() {
        return ss.getState() != GamePlayState.PLAYING;
    }

    public void addScore(int score) {
        ss.setScore(ss.getScore() + score);
    }

    public void incMoves() {
        ss.setMoves(ss.getMoves() + 1);
    }
}
