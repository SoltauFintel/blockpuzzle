package de.mwvb.blockpuzzle.sound;

public interface ISoundService {
    void clear(boolean big);

    void firstGravitation();

    void gameOver();

    void backPressed(boolean gameOver);

    void oneColor();

    void doesNotWork();
}
