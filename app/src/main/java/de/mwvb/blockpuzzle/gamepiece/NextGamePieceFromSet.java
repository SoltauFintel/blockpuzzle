package de.mwvb.blockpuzzle.gamepiece;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.gamepiece.sets.AllGamePieceSets;

public class NextGamePieceFromSet implements INextGamePiece {
    private final List<GamePiece> allGamePieces = GamePiecesDefinition.INSTANCE.get();
    private final BlockTypes blockTypes = new BlockTypes(null);
    private final int number;
    private final INextRound persistence;
    private final String[] set;
    private int nextRound = 0;
    private int nextGamePieceInRound = 0;

    public NextGamePieceFromSet(int number, INextRound persistence) {
        if (number <= 0 || number > 9999 || persistence == null) {
            throw new IllegalArgumentException();
        }
        this.number = number;
        this.persistence = persistence;
        final String a = get4DigitNumber(number);
        for (IGamePieceSet set : AllGamePieceSets.sets) {
            if (set.getClass().getSimpleName().endsWith(a)) {
                this.set = set.getGamePieceSet();
                return;
            }
        }
        throw new RuntimeException("Game piece set with number " + number + " does not exist!");
    }

    @NotNull
    private String get4DigitNumber(int number) {
        return String.format(Locale.GERMAN, "%04d", number);
    }

    @Override
    public GamePiece next(BlockTypes blockTypes) {
        if (nextRound == set.length) {
            return null;
        }
        GamePiece ret = fetch(nextRound, nextGamePieceInRound);
        nextGamePieceInRound++;
        if (nextGamePieceInRound == 3) {
            nextGamePieceInRound = 0;
            nextRound++;
            persistence.saveNextRound(nextRound);
        }
        return ret;
    }

    @Override
    public GamePiece getOther(BlockTypes blockTypes) {
        throw new UnsupportedOperationException();
    }

    private GamePiece fetch(int round, int piece) {
        String line = set[round];
        StringBuilder current = new StringBuilder();
        int index = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '#' || c == ':') { // begin of game piece
                if (current.length() > 0 && index == piece) {
                    return fetch(current.toString());
                }
                index++;
                current = new StringBuilder();
            }
            current.append(c);
        }
        if (current.length() > 0 && index == piece) {
            return fetch(current.toString());
        }
        throw new RuntimeException("GamePieceSet line parse error! [B]\nround=" + round + ", piece=" + piece
                + ", game piece set number=" + number + ", c=" + current.toString());
    }

    private GamePiece fetch(String current) {
        if (current.startsWith("#")) {
            return byName(current.substring(1));
        } else if (current.startsWith(":") && current.length() == 5 * 5 + 1) {
            return byPlan(current.substring(1));
        } else {
            throw new RuntimeException("GamePieceSet line parse error! [A]\nround=" + nextRound
                    + ", piece=" + nextGamePieceInRound + ", game piece set number=" + number + ", c=" + current);
        }
    }

    private GamePiece byName(String name) {
        for (GamePiece p : allGamePieces) {
            if (p.getName().equals(name)) {
                return p.copy();
            }
        }
        throw new RuntimeException("Game piece with name '" + name + "' not found!\nround=" + nextRound + ", piece=" + nextGamePieceInRound + ", game piece set number=" + number);
    }

    private GamePiece byPlan(String plan) {
        GamePiece p = new GamePiece();
        int x = 0;
        int y = 0;
        for (int i = 0; i < plan.length(); i++) {
            char c = plan.charAt(i);

            int blockTypeNumber = 0;
            if (c != '.') {
                blockTypeNumber = blockTypes.getBlockTypeNumber(c);
            }
            p.setBlockType(x, y, blockTypeNumber);
            x++;
            if (x == GamePiece.max) {
                x = 0;
                y++;
            }
        }
        return p;
    }

    @Override
    public void reset() {
        nextRound = 0;
        persistence.saveNextRound(0);
        nextGamePieceInRound = 0;
    }

    @Override
    public void load() {
        nextRound = persistence.getNextRound();
        nextGamePieceInRound = 0;
    }
}
