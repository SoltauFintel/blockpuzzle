package de.mwvb.blockpuzzle.gamestate;

import de.mwvb.blockpuzzle.gamedefinition.OldGameDefinition;
import de.mwvb.blockpuzzle.gamepiece.INextRound;

/**
 * GameState classes are immutable Spielstand wrapper. If something would change a new object would be created.
 */
public class GameState implements INextRound {
    private final Spielstand ss;

    protected GameState(Spielstand ss) {
        this.ss = ss;
    }

    public static GameState create() {
        return new GameState(new SpielstandDAO().loadOldGame());
    }

    public OldGameDefinition createGameDefinition() {
        return new OldGameDefinition();
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

    // TO-DO überdenken. Macht vermutlich nur für das "old game" Sinn.
    public void updateHighScore() {
        if (ss.getScore() > ss.getHighscore() || ss.getHighscore() <= 0) {
            ss.setHighscore(ss.getScore());
            ss.setHighscoreMoves(ss.getMoves());
            save();
        } else if (ss.getScore() == ss.getHighscore() && (ss.getMoves() < ss.getHighscoreMoves() || ss.getHighscoreMoves() <= 0)) {
            ss.setHighscoreMoves(ss.getMoves());
            save();
        }
    }

    @Override
    public void saveNextRound(int nextRound) {
        get().setNextRound(nextRound);
        save();
    }

    @Override
    public int getNextRound() {
        return get().getNextRound();
    }
}
