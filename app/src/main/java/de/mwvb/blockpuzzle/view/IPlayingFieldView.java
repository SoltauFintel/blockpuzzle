package de.mwvb.blockpuzzle.view;

import de.mwvb.blockpuzzle.logic.Action;
import de.mwvb.blockpuzzle.logic.FilledRows;
import de.mwvb.blockpuzzle.logic.Game;
import de.mwvb.blockpuzzle.logic.PlayingField;
import de.mwvb.blockpuzzle.sound.SoundService;

public interface IPlayingFieldView {

    SoundService getSoundService();

    void setPlayingField(PlayingField playingField);

    void draw();

    void clearRows(FilledRows filledRows, Action action);

    void oneColor();

    void gravitation();
}
