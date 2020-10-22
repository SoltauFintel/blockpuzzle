package de.mwvb.blockpuzzle.gravitation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public class GravitationData {
    /** filled rows: y values */
    private final List<Integer> rows = new ArrayList<>();
    /** blocks that must not be cleared */
    private final Set<QPosition> exclusions = new HashSet<>();
    private boolean firstGravitationPlayed;
    private IPersistence persistence;

    public void setPersistence(IPersistence persistence) {
        this.persistence = persistence;
    }

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
        rows.addAll(f.getYlist());
        exclusions.addAll(f.getExclusions());
    }

    public void load() {
        persistence.load(this);
    }

    public void save() {
        persistence.save(this);
    }
}
