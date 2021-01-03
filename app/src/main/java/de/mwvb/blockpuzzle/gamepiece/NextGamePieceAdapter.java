package de.mwvb.blockpuzzle.gamepiece;

import de.mwvb.blockpuzzle.block.BlockTypes;

public class NextGamePieceAdapter implements INextGamePiece {
    private final INextGamePiece parent;

    public NextGamePieceAdapter(INextGamePiece parent) {
        this.parent = parent;
    }

    @Override
    public GamePiece next(BlockTypes blockTypes) {
        return parent.next(blockTypes);
    }

    @Override
    public GamePiece getOther(BlockTypes blockTypes) {
        return parent.getOther(blockTypes);
    }

    @Override
    public void reset() {
        parent.reset();
    }

    @Override
    public void load() {
        parent.load();
    }
}
