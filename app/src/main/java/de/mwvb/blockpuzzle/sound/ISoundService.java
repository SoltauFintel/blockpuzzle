package de.mwvb.blockpuzzle.sound;

public interface ISoundService {

    void clear(boolean big);

    void firstGravitation();

    void gameOver();

    void youWon();

    void oneColor();

    void doesNotWork();

    void shake();

    void alarm(boolean on);

    void targetSelected();
}
