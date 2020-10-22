package de.mwvb.blockpuzzle.logic.gamepiece;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.logic.gamepiece.special.ISpecialBlock;
import de.mwvb.blockpuzzle.logic.gamepiece.special.LockBlock;
import de.mwvb.blockpuzzle.logic.gamepiece.special.StarBlock;
import de.mwvb.blockpuzzle.view.ColorBlockDrawer;
import de.mwvb.blockpuzzle.view.IBlockDrawer;

// TODO Move to spielstein package
// Bauplan für alle Blöcke
public class BlockTypes {
    public static final int ONE_COLOR = 10;
    private final Map<String, Integer> charMap = new HashMap<>();
    private final Map<Integer, IBlockDrawer> blockDrawerMap = new HashMap<>();
    private final View view;
    private final List<ISpecialBlock> specialBlockTypes = new ArrayList<>();
    public static final int MIN_SPECIAL = 20;
    public static final int MAX_SPECIAL = 29; // TODO Ich muss die 30er umziehen, um mehr Platz zu bekommen

    public BlockTypes(View view) {
        this.view = view;
        specialBlockTypes.add(new StarBlock());
        specialBlockTypes.add(new LockBlock());

        add(1, R.color.colorNormal);
        add(2, R.color.orange);
        add(3, R.color.red);
        add(4, R.color.blue);
        add(5, R.color.pink);
        add(6, R.color.yellow);
        add(ONE_COLOR, 'f', R.color.oneColor);
        add(11, 'o', R.color.oneColorOld);

        for (ISpecialBlock s : getSpecialBlockTypes()) {
            charMap.put("" + s.getBlockTypeChar(), s.getBlockType());
            if (view != null) {
                blockDrawerMap.put(s.getBlockType(), s.getBlockDrawer(view));
            }
        }
    }

    public List<ISpecialBlock> getSpecialBlockTypes() {
        return specialBlockTypes;
    }

    private void add(int blockType, int color) {
        if (blockType > 9) {
            throw new RuntimeException("Use other add() for blockTypes > 9 !");
        }
        charMap.put("" + blockType, blockType);
        if (view != null) {
            blockDrawerMap.put(blockType, ColorBlockDrawer.byRColor(view, color));
        }
    }

    private void add(int blockType, char cBlockType, int color) {
        charMap.put("" + cBlockType, blockType);
        if (view != null) {
            blockDrawerMap.put(blockType, ColorBlockDrawer.byRColor(view, color));
        }
    }

    public int toBlockType(char defChar, String definition) {
        String key = "" + defChar;
        if (charMap.containsKey(key)) {
            return charMap.get(key);
        } else {
            throw new RuntimeException("Wrong game piece definition!\n" +
                    "Unsupported char: " + key + "\nline: " + definition);
        }
    }

    public IBlockDrawer getBlockDrawer(int blockType) {
        return blockDrawerMap.get(blockType);
    }
}
