package de.mwvb.blockpuzzle.game.place;

import java.util.List;

import de.mwvb.blockpuzzle.block.special.ISpecialBlock;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Bonus score for clearing special blocks
 */
public class SpecialBlockBonusPlaceAction implements IPlaceAction {

    @Override
    public void perform(PlaceActionModel info) {
        int bonus = 0;
        List<ISpecialBlock> specialBlocks = info.getBlockTypes().getSpecialBlockTypes();
        FilledRows f = info.getFilledRows();

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

        info.getGs().addScore(bonus);
    }
}
