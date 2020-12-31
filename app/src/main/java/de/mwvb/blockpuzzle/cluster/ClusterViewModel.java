package de.mwvb.blockpuzzle.cluster;

import android.content.res.Resources;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.AbstractPlanet;
import de.mwvb.blockpuzzle.planet.GiantPlanet;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;
import de.mwvb.blockpuzzle.planet.Moon;

public class ClusterViewModel {
    private final List<ISpaceObject> spaceObjects;
    /** Das ist der Planet wo das Raumschiff gerade ist. In Bubble gibt es noch einen weiteren Planeten, der null sein kann, falls gerade in der Karte kein Planet gewählt ist. */
    private IPlanet currentPlanet;
    private IPersistence persistence;

    public ClusterViewModel(List<ISpaceObject> spaceObjects, IPlanet planet, IPersistence per, Resources resources) {
        this.spaceObjects = spaceObjects;
        currentPlanet = planet;
        persistence = per; // for saving the current planet

        for (ISpaceObject so : spaceObjects) {
            if (so instanceof AbstractPlanet) {
                AbstractPlanet p = (AbstractPlanet) so;

                per.setGameID(p);
                p.setInfoText1(getName(p, resources) + p.getNumber());
                p.setInfoText2(createInfoText2(p));
                p.setInfoText3(createInfoText3(per, p, p.getGameDefinitions().size(),
                        per.loadScore(), per.loadMoves(), per.loadOwnerScore(), per.loadOwnerMoves()));
            }
        }
        per.setGameID(currentPlanet);
    }

    @NotNull
    private String getName(AbstractPlanet p, Resources resources) {
        String name;
        if (p instanceof Moon) {
            name = resources.getString(R.string.moon);
        } else if (p instanceof GiantPlanet) {
            name = resources.getString(R.string.giantPlanet);
        } else {
            name = resources.getString(R.string.planet);
        }
        return name + " #";
    }

    private String createInfoText2(AbstractPlanet p) {
        if (p.getGameDefinitions() != null && !p.getGameDefinitions().isEmpty()) {
            return getFirstGameDefinition(p).getClusterViewInfo();
        } else {
            return "";
        }
    }

    private GameDefinition getFirstGameDefinition(IPlanet planet) {
        return planet.getGameDefinitions().get(0);
    }

    @NotNull
    private String createInfoText3(IPersistence per, AbstractPlanet p, int n, int score, int moves, int ownerScore, int ownerMoves) {
        String i3 = "";
        if (n == 1 && getFirstGameDefinition(p).showMoves()) {
            if (moves > 0) {
                i3 = "Moves: " + moves;
            }
            if (ownerMoves > 0) {
                i3 = "Moves: " + ownerMoves + " " + per.loadOwnerName();
            }
        } else if (score > 0) {
            i3 = "Score: " + formatScore(score);
            if (ownerScore > 0) {
                i3 = "Score: " + formatScore(ownerScore) + " " + per.loadOwnerName();
            }
        }
        return i3;
    }

    private String formatScore(int score) {
        if (score <= 1000) {
            return "" + score;
        }
        return ((int) (score / 1000)) + "k";
    }

    public List<ISpaceObject> getSpaceObjects() {
        return spaceObjects;
    }

    public IPlanet getCurrentPlanet() {
        return currentPlanet;
    }

    public void setCurrentPlanet(IPlanet currentPlanet) {
        this.currentPlanet = currentPlanet;
        if (currentPlanet != null) {
            persistence.saveCurrentPlanet(currentPlanet.getClusterNumber(), currentPlanet.getNumber());
        }
    }
}
