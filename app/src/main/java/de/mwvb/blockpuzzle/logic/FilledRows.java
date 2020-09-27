package de.mwvb.blockpuzzle.logic;

import java.util.ArrayList;
import java.util.List;

public class FilledRows {
    private final List<Integer> ylist = new ArrayList<>();
    private final List<Integer> xlist = new ArrayList<>();

    public List<Integer> getYlist() {
        return ylist;
    }

    public List<Integer> getXlist() {
        return xlist;
    }

    public int getHits() {
        return ylist.size() + xlist.size();
    }

    public boolean containsX(Integer x) {
        return xlist.contains(x);
    }

    public boolean containsY(Integer y) {
        return ylist.contains(y);
    }
}
