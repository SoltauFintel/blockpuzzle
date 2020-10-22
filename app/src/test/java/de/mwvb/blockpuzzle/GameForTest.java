package de.mwvb.blockpuzzle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.mwvb.blockpuzzle.logic.Action;
import de.mwvb.blockpuzzle.logic.FilledRows;
import de.mwvb.blockpuzzle.logic.Game;
import de.mwvb.blockpuzzle.logic.IPersistence;
import de.mwvb.blockpuzzle.logic.PlayingField;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.sound.ISoundService;
import de.mwvb.blockpuzzle.sound.SoundService;
import de.mwvb.blockpuzzle.view.GamePieceView;
import de.mwvb.blockpuzzle.view.IGamePieceView;
import de.mwvb.blockpuzzle.view.IGameView;
import de.mwvb.blockpuzzle.view.IPlayingFieldView;

public class GameForTest {

    public static Game create() {
        Game game = new Game(getGameView());
        game.setPersistence(getPersistence());
        game.initGame();
        return game;
    }

    private static IGameView getGameView() {
        return new IGameView() {
            @Override
            public boolean getGravitySetting() {
                return false;
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
            public void rotatingModeOff() {
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

                    @Override
                    public void setDrehmodus(boolean d) {
                    }

                    @Override
                    public void rotate() {
                    }

                    @Override
                    public void write() {
                    }

                    @Override
                    public void read() {
                    }
                };
            }
        };
    }

    private static IPersistence getPersistence() {
        return new IPersistence() {
            @Override
            public void save(int index, GamePiece p) {
            }

            @Override
            public GamePiece load(int index) {
                return null;
            }

            @Override
            public void save(PlayingField f) {
            }

            @Override
            public void load(PlayingField f) {
            }

            @Override
            public int loadScore() {
                return -9999;
            }

            @Override
            public void saveScore(int punkte) {
            }

            @Override
            public int loadMoves() {
                return 0;
            }

            @Override
            public void saveMoves(int moves) {
            }
        };
    }

    private static ISoundService getSoundService() {
        return new ISoundService() {
            @Override
            public void clear(boolean big) {
            }

            @Override
            public void firstGravitation() {
            }

            @Override
            public void gameOver() {
            }

            @Override
            public void backPressed(boolean gameOver) {
            }

            @Override
            public void oneColor() {
            }

            @Override
            public void doesNotWork() {
            }
        };
    }

    public static String getPlayingFieldAsString(Game game) {
        String ret = "";
        for (int y = 0; y < Game.blocks; y++) {
            for (int x = 0; x < Game.blocks; x++) {
                int v = game.get(x, y);
                if (v == 0) {
                    ret += ".";
                } else if (v >= 1 && v <= 9) {
                    ret += v;
                } else {
                    ret += "B"; // Ich müsste hier eigentlich BlockTypes verwenden, ist aber nicht so wichtig.
                }
            }
            ret += "\n";
        }
        return ret;
    }
}
