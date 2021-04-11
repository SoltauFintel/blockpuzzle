package de.mwvb.blockpuzzle.gamestate;

import androidx.annotation.NonNull;

/**
 * Game state entity
 *
 * ID: "C" + cluster number + "_" + planet number + "_" + 0 based game definition index
 */
public class Spielstand {
    private int score = -9999;
    private int moves = 0;
    private int delta;
    private GamePlayState state = GamePlayState.PLAYING;
    private int nextRound = 0;
    private boolean emptyScreenBonusActive;
    private int highscore;
    private int highscoreMoves;
    private String playingField;
    private String gamePieceView1;
    private String gamePieceView2;
    private String gamePieceView3;
    private String gamePieceViewP;
    private int ownerScore;
    private int ownerMoves;
    private String ownerName;
    private String dailyDate;
    private String gravitationRows;
    private String gravitationExclusions;
    private boolean gravitationPlayedSound;

    public void transfer(Spielstand to) {
        to.score = score;
        to.moves = moves;
        to.delta = delta;
        to.state = state;
        to.nextRound = nextRound;
        to.emptyScreenBonusActive = emptyScreenBonusActive;
        to.highscore = highscore;
        to.highscoreMoves = highscoreMoves;
        to.playingField = playingField;
        to.gamePieceView1 = gamePieceView1;
        to.gamePieceView2 = gamePieceView2;
        to.gamePieceView3 = gamePieceView3;
        to.gamePieceViewP = gamePieceViewP;
        to.ownerScore = ownerScore;
        to.ownerMoves = ownerMoves;
        to.ownerName = ownerName;
        to.dailyDate = dailyDate;
        to.gravitationRows = gravitationRows;
        to.gravitationExclusions = gravitationExclusions;
        to.gravitationPlayedSound = gravitationPlayedSound;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void unsetScore() {
        score = -9999;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public GamePlayState getState() {
        if (state != GamePlayState.PLAYING && state != GamePlayState.WON_GAME && state != GamePlayState.LOST_GAME) {
            return GamePlayState.PLAYING;
        }
        return state;
    }

    public void setState(@NonNull GamePlayState state) {
        this.state = state;
    }

    public int getNextRound() {
        return nextRound;
    }

    public void setNextRound(int nextRound) {
        this.nextRound = nextRound;
    }

    public boolean isEmptyScreenBonusActive() {
        return emptyScreenBonusActive;
    }

    public void setEmptyScreenBonusActive(boolean emptyScreenBonusActive) {
        this.emptyScreenBonusActive = emptyScreenBonusActive;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public int getHighscoreMoves() {
        return highscoreMoves;
    }

    public void setHighscoreMoves(int highscoreMoves) {
        this.highscoreMoves = highscoreMoves;
    }

    public String getPlayingField() {
        return playingField;
    }

    public void setPlayingField(String playingField) {
        this.playingField = playingField;
    }

    public String getGamePieceView1() {
        return gamePieceView1;
    }

    public void setGamePieceView1(String gamePieceView1) {
        this.gamePieceView1 = gamePieceView1;
    }

    public String getGamePieceView2() {
        return gamePieceView2;
    }

    public void setGamePieceView2(String gamePieceView2) {
        this.gamePieceView2 = gamePieceView2;
    }

    public String getGamePieceView3() {
        return gamePieceView3;
    }

    public void setGamePieceView3(String gamePieceView3) {
        this.gamePieceView3 = gamePieceView3;
    }

    public String getGamePieceViewP() {
        return gamePieceViewP;
    }

    public void setGamePieceViewP(String gamePieceViewP) {
        this.gamePieceViewP = gamePieceViewP;
    }

    public String getGamePieceView(int index) {
        switch (index) {
            case 1: return getGamePieceView1();
            case 2: return getGamePieceView2();
            case 3: return getGamePieceView3();
            case -1: return getGamePieceViewP();
            default: throw new RuntimeException("Wrong index");
        }
    }

    public void setGamePieceView(int index, String value) {
        switch (index) {
            case 1:
                setGamePieceView1(value);
                break;
            case 2:
                setGamePieceView2(value);
                break;
            case 3:
                setGamePieceView3(value);
                break;
            case -1:
                setGamePieceViewP(value);
                break;
            default:
                throw new RuntimeException("Wrong index");
        }
    }

    public int getOwnerScore() {
        return ownerScore;
    }

    public void setOwnerScore(int ownerScore) {
        this.ownerScore = ownerScore;
    }

    public int getOwnerMoves() {
        return ownerMoves;
    }

    public void setOwnerMoves(int ownerMoves) {
        this.ownerMoves = ownerMoves;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getDailyDate() {
        return dailyDate;
    }

    public void setDailyDate(String dailyDate) {
        this.dailyDate = dailyDate;
    }

    public String getGravitationRows() {
        return gravitationRows;
    }

    public void setGravitationRows(String gravitationRows) {
        this.gravitationRows = gravitationRows;
    }

    public String getGravitationExclusions() {
        return gravitationExclusions;
    }

    public void setGravitationExclusions(String gravitationExclusions) {
        this.gravitationExclusions = gravitationExclusions;
    }

    public boolean isGravitationPlayedSound() {
        return gravitationPlayedSound;
    }

    public void setGravitationPlayedSound(boolean gravitationPlayedSound) {
        this.gravitationPlayedSound = gravitationPlayedSound;
    }
}
