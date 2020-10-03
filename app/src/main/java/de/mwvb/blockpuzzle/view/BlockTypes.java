package de.mwvb.blockpuzzle.view;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

import de.mwvb.blockpuzzle.R;

// Bauplan für alle Blöcke
public class BlockTypes {
    private final Map<String, Integer> charMap = new HashMap<>();
    private final Map<Integer, IBlockDrawer> blockDrawerMap = new HashMap<>();
    private final View view;

    public BlockTypes(View view) {
        this.view = view;
        add(1, R.color.colorNormal);
        add(2, R.color.orange);
        add(3, R.color.red);
        add(4, R.color.blue);
        add(5, R.color.pink);
        add(6, R.color.yellow);
    }

    private void add(int blockType, int color) {
        charMap.put("" + blockType, blockType);
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
