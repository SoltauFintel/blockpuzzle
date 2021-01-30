package de.mwvb.blockpuzzle.game.place;

import java.util.List;

import de.mwvb.blockpuzzle.block.special.ISpecialBlock;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Punktzahl erhöhen
 */
public class ScorePlaceAction implements IPlaceAction {

    @Override
    public void perform(PlaceInfo info) {
        GameState gs = info.getGs();
        FilledRows f = info.getFilledRows();

        // TODO create a class per part ?
        // Part 1
        gs.addScore(info.getGamePiece().getPunkte() * getGamePieceBlocksScoreFactor() + f.getHits() * getHitsScoreFactor());
        gs.incMoves();

        // Part 2
        rowsAdditionalBonus(f.getXHits(), f.getYHits(), gs);

        // Part 3
        gs.addScore(processSpecialBlockTypes(f, info));
    }

    protected int getGamePieceBlocksScoreFactor() {
        return 1;
    }

    protected int getHitsScoreFactor() {
        return 10;
    }

    protected void rowsAdditionalBonus(int xrows, int yrows, GameState gs) {
        int bonus = 0;
        switch (xrows + yrows) {
            case 0:
            case 1: break; // 0-1 kein Bonus
            // Bonuspunkte wenn mehr als 2 Rows gleichzeitig abgeräumt werden.
            // Fällt mir etwas schwer zu entscheiden wieviel Punkte das jeweils wert ist.
            case 2:  bonus = 12; break;
            case 3:  bonus = 17; break;
            case 4:  bonus = 31; break;
            case 5:  bonus = 44; break;
            default: bonus = 22; break;
        }
        if (xrows > 0 && yrows > 0) {
            bonus += 10;
        }
        gs.addScore(bonus);
        // TO-DO Reihe mit gleicher Farbe (ohne oldOneColor) könnte weiteren Bonus auslösen.
    }

    private int processSpecialBlockTypes(FilledRows f, PlaceInfo info) {
        int bonus = 0;
        List<ISpecialBlock> specialBlocks = info.getBlockTypes().getSpecialBlockTypes();

        // Rows ----
        for (int y : f.getYlist()) {
            for (int x = 0; x < info.getBlocks(); x++) {
                int bt = info.getPlayingField().get(x, y);
                for (ISpecialBlock s : specialBlocks) {
                    if (s.getBlockType() == bt) {
                        int r = s.cleared(info.getPlayingField(), new QPosition(x, y));
                        if (r > ISpecialBlock.CLEAR_MAX_MODE) {
                            bonus += r;
                        } else if (r == 1) {
                            f.getExclusions().add(new QPosition(x, y));
                        }
                    }
                }
            }
        }

        // Columns ----
        for (int x : f.getXlist()) {
            for (int y = 0; y < info.getBlocks(); y++) {
                int bt = info.getPlayingField().get(x, y);
                for (ISpecialBlock s : specialBlocks) {
                    if (s.getBlockType() == bt) {
                        int r = s.cleared(info.getPlayingField(), new QPosition(x, y));
                        if (r > ISpecialBlock.CLEAR_MAX_MODE) {
                            bonus += r;
                        } else if (r == 1) {
                            f.getExclusions().add(new QPosition(x, y));
                        }
                    }
                }
            }
        }

        return bonus;
    }
}
