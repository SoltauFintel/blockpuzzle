package de.mwvb.blockpuzzle.game;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.IGamePieceView;
import de.mwvb.blockpuzzle.playingfield.Action;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.playingfield.IPlayingFieldView;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.sound.ISoundService;
import de.mwvb.blockpuzzle.sound.SoundService;

public class TestGameBuilder {

    public static Game create() {
        Game game = new Game(getGameView(), new PersistenceNoOp());
        game.initGame();
        return game;
    }

    private static IGameView getGameView() {
        return new IGameView() {
            @Override
            public void shake() {
            }

            @Override
            public void showToast(@NotNull String msg) {
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
                };
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
            public void youWon() {
            }

            @Override
            public void oneColor() {
            }

            @Override
            public void doesNotWork() {
            }

            @Override
            public void shake() {
            }

            @Override
            public void alarm(boolean on) {
            }

            @Override
            public void targetSelected() {
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

    public static String getStringPresentation(GamePiece p, BlockTypes blockTypes, boolean addNewline) {
        StringBuilder ret = new StringBuilder();
        for (int y = 0; y < GamePiece.max; y++) {
            for (int x = 0; x < GamePiece.max; x++) {
                final int blockType = p.getBlockType(x, y);
                if (blockType == 0) {
                    ret.append('.');
                } else {
                    char blockTypeChar = blockTypes.getBlockTypeChar(blockType);
                    ret.append(blockTypeChar);
                }
            }
            if (addNewline) {
                ret.append("\n");
            }
        }
        return ret.toString();
    }
}
