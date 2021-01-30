package de.mwvb.blockpuzzle.playingfield.gravitation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public class GravitationData {
    /** filled rows: y values */
    private final List<Integer> rows = new ArrayList<>();
    /** blocks that must not be cleared */
    private final Set<QPosition> exclusions = new HashSet<>();
    private boolean firstGravitationPlayed;

    public List<Integer> getRows() {
        return rows;
    }

    public Set<QPosition> getExclusions() {
        return exclusions;
    }

    public boolean isFirstGravitationPlayed() {
        return firstGravitationPlayed;
    }

    public void setFirstGravitationPlayed(boolean firstGravitationPlayed) {
        this.firstGravitationPlayed = firstGravitationPlayed;
    }

    public void init() {
        clear();
        firstGravitationPlayed = false;
    }

    public void clear() {
        rows.clear();
        exclusions.clear();
    }

    public void set(FilledRows f) {
        clear();
        rows.addAll(f.getYlist());
        exclusions.addAll(f.getExclusions());
    }

    public void load(Spielstand ss) {
        clear();

        String d = ss.getGravitationRows();
        if (d != null && !d.isEmpty()) {
            for (String w : d.split(",")) {
                getRows().add(Integer.parseInt(w));
            }
        }

        d = ss.getGravitationExclusions();
        if (d != null && !d.isEmpty()) {
            for (String w : d.split(",")) {
                String[] k = w.split("/");
                getExclusions().add(new QPosition(Integer.parseInt(k[0]), Integer.parseInt(k[1])));
            }
        }

        setFirstGravitationPlayed(ss.isGravitationPlayedSound());
    }

    public void save(Spielstand ss) {
        StringBuilder d = new StringBuilder();
        String k = "";
        for (int y : getRows()) {
            d.append(k).append(y);
            k = ",";
        }
        ss.setGravitationRows(d.toString());

        d = new StringBuilder();
        k = "";
        for (QPosition p : getExclusions()) {
            d.append(k);
            k = ",";
            d.append(p.getX()).append("/").append(p.getY());
        }
        ss.setGravitationExclusions(d.toString());

        ss.setGravitationPlayedSound(isFirstGravitationPlayed());
    }
}
