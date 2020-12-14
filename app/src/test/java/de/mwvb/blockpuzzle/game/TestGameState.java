package de.mwvb.blockpuzzle.game;

public class TestGameState {
    private int score = -9999;
    private int moves = 0;
    private int ownerScore = 0;
    private int ownerMoves = 0;
    private String ownerName = "";

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
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
}
