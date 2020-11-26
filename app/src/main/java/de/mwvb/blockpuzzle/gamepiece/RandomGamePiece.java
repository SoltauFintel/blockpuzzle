package de.mwvb.blockpuzzle.gamepiece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.block.special.ISpecialBlock;

public class RandomGamePiece implements INextGamePiece {
    private final Random rand = new Random(System.currentTimeMillis());
    private final List<GamePiece> allDefinedGamePieces = new ArrayList<>();
    /** Wieviel Steine der Generator schon raus gegeben hat. */
    private int output;

    public RandomGamePiece() {
        reset();
        allDefinedGamePieces.addAll(GamePiecesDefinition.INSTANCE.get());
    }

    @Override
    public GamePiece next(BlockTypes blockTypes) {
        int loop = 0;

        int index = rand.nextInt(allDefinedGamePieces.size());
        GamePiece gamePiece = allDefinedGamePieces.get(index);
        while (output < gamePiece.getMinimumMoves()) {
            if (++loop > 1000) { // killer loop
                return allDefinedGamePieces.get(0);
            }

            index = rand.nextInt(allDefinedGamePieces.size());
            gamePiece = allDefinedGamePieces.get(index);
        }
        gamePiece = gamePiece.copy();
        addSpecialBlock(blockTypes, gamePiece);
        output++;
        return gamePiece;
    }

    @Override
    public GamePiece getOther(BlockTypes blockTypes) {
        output--;
        return next(blockTypes);
    }

    /** Insert a special block type randomly */
    private void addSpecialBlock(BlockTypes blockTypes, GamePiece gamePiece) {
        for (ISpecialBlock s : blockTypes.getSpecialBlockTypes()) {
            if (s.isRelevant(gamePiece) && s.process(gamePiece)) {
                break;
            }
        }
    }

    @Override
    public void reset() {
        output = 0;
    }

    @Override
    public void load() {
        reset();
    }
}
