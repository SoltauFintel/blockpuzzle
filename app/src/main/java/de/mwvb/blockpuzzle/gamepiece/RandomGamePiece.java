package de.mwvb.blockpuzzle.gamepiece;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.block.special.ISpecialBlock;

public class RandomGamePiece implements INextGamePiece {
    private final Random rand = new Random(System.currentTimeMillis());
    private final List<GamePiece> gamePieces = new ArrayList<>();

    public RandomGamePiece() {
        gamePieces.addAll(GamePiecesDefinition.INSTANCE.get());
    }

    @Override
    public void ausduennen() {
        // TODO Das ist erstmal vorläufig so; zum Testen soll's einfacher sein.
        Iterator<GamePiece> iter = gamePieces.iterator();
        while (iter.hasNext()) {
            GamePiece p = iter.next();
            if (p.getName().length() > 1 && !"Ecke2".equals(p.getName())) {
                iter.remove();
            }
        }
    }

    @Override
    public GamePiece next(int score, BlockTypes blockTypes) {
        int loop = 0;
        int index = rand.nextInt(gamePieces.size());
        GamePiece gamePiece = gamePieces.get(index);
        while (score < gamePiece.getMindestpunktzahl()) {
            if (++loop > 1000) { // Notausgang
                return gamePieces.get(0);
            }
            index = rand.nextInt(gamePieces.size());
            gamePiece = gamePieces.get(index);
        }
        gamePiece = gamePiece.copy();
        addSpecialBlock(blockTypes, gamePiece);
        return gamePiece;
    }

    /** Insert a special block type randomly */
    private void addSpecialBlock(BlockTypes blockTypes, GamePiece gamePiece) {
        for (ISpecialBlock s : blockTypes.getSpecialBlockTypes()) {
            if (s.isRelevant(gamePiece) && s.process(gamePiece)) {
                break;
            }
        }
    }
}
