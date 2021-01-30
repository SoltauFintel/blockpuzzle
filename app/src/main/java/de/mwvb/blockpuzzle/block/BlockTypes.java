package de.mwvb.blockpuzzle.block;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.block.special.ISpecialBlock;
import de.mwvb.blockpuzzle.block.special.LockBlock;
import de.mwvb.blockpuzzle.block.special.StarBlock;

// Bauplan für alle Blöcke
public class BlockTypes {
    public static final int ONE_COLOR = 10;
    public static final int OLD_ONE_COLOR = 11;
    /** key: block type char, value: block type number */
    private final Map<String, Integer> charMap = new HashMap<>();
    /** key: block type number, value: block drawing strategy */
    private final Map<Integer, IBlockDrawer> blockDrawerMap = new HashMap<>();
    private final View view;
    private final List<ISpecialBlock> specialBlockTypes = new ArrayList<>();
    public static final int MIN_SPECIAL = 20;
    public static final int MAX_SPECIAL = 29; // TO-DO Ich muss die 30er umziehen, um mehr Platz zu bekommen

    public BlockTypes(View view) {
        this.view = view;
        specialBlockTypes.add(new StarBlock());
        specialBlockTypes.add(new LockBlock());

        add(1, R.color.brown, R.color.brown_i, R.color.brown_ib);
        add(2, R.color.orange, R.color.orange_i, R.color.orange_ib);
        add(3, R.color.red, R.color.red_i, R.color.red_ib);
        add(4, R.color.blue, R.color.blue_i, R.color.blue_ib);
        add(5, R.color.pink, R.color.pink_i, R.color.pink_ib);
        add(6, R.color.yellow, R.color.yellow_i, R.color.yellow_ib);
        add(7, R.color.green, R.color.green_i, R.color.green_ib);
        add(ONE_COLOR, 'f', R.color.oneColor, R.color.oneColor_i, R.color.oneColor_ib);
        add(OLD_ONE_COLOR, 'o', R.color.oneColorOld, R.color.oneColorOld_i, R.color.oneColorOld_ib);

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

    private void add(int blockType, int color, int color_i, int color_ib) {
        if (blockType > 9) {
            throw new RuntimeException("Use other add() for blockTypes > 9 !");
        }
        charMap.put("" + blockType, blockType);
        if (view != null) {
            blockDrawerMap.put(blockType, ColorBlockDrawer.byRColor(view, color, color_i, color_ib));
        }
    }

    private void add(int blockType, char cBlockType, int color, int color_i, int color_ib) {
        charMap.put("" + cBlockType, blockType);
        if (view != null) {
            blockDrawerMap.put(blockType, ColorBlockDrawer.byRColor(view, color, color_i, color_ib));
        }
    }

    @SuppressWarnings("ConstantConditions")
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

    public char getBlockTypeChar(int blockTypeNumber) {
        for (Map.Entry<String, Integer> e : charMap.entrySet()) {
            if (e.getValue() == blockTypeNumber) {
                return e.getKey().charAt(0);
            }
        }
        throw new RuntimeException("Unknown block type number: " + blockTypeNumber);
    }

    public Integer getBlockTypeNumber(char c) {
        return charMap.get("" + c);
    }
}
