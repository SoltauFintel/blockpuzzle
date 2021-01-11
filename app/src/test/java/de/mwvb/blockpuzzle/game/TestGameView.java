package de.mwvb.blockpuzzle.game;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.IGamePieceView;
import de.mwvb.blockpuzzle.playingfield.Action;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.playingfield.IPlayingFieldView;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.sound.SoundService;

public class TestGameView implements IGameView {

    @Override
    public void shake() {
    }

    @Override
    public void showToast(@NotNull String msg) {
        System.out.println("TOAST: " + msg);
    }

    @NotNull
    @Override
    public IPlayingFieldView getPlayingFieldView() {
        return new IPlayingFieldView() {
            @Override
            public SoundService getSoundService() {
                return getSoundService();
            }

            @Override
            public void setPlayingField(PlayingField playingField) {
            }

            @Override
            public void draw() {
            }

            @Override
            public void clearRows(FilledRows filledRows, Action action) {
                if (action != null) {
                    action.execute();
                }
            }

            @Override
            public void oneColor() {
            }

            @Override
            public void gravitation() {
            }
        };
    }

    @Override
    public void showScore(int score, int delta, boolean gameOver) {
    }

    @Override
    public void showMoves(int moves) {
    }

    @Override
    public void showTerritoryName(int resId) {
    }

    @NotNull
    @Override
    public IGamePieceView getGamePieceView(int index) {
        return new IGamePieceView() {
            @Override
            public void setGamePiece(GamePiece v) {
            }

            @Override
            public GamePiece getGamePiece() {
                return null;
            }

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public void setGrey(boolean v) {
            }

            @Override
            public void draw() {
            }

            @Override
            public void startDragMode() {
            }

            @Override
            public void endDragMode() {
            }
        };
    }

    @Override
    public void playSound(int number) {
    }

    @NotNull
    @Override
    public Action getSpecialAction(int specialState) {
        return null;
    }
}
