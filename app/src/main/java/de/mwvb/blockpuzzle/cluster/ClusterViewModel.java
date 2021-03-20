package de.mwvb.blockpuzzle.cluster;

import android.content.res.Resources;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.game.stonewars.deathstar.SpaceNebulaRoute;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO;
import de.mwvb.blockpuzzle.global.Features;
import de.mwvb.blockpuzzle.global.GlobalData;
import de.mwvb.blockpuzzle.planet.AbstractPlanet;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;
import de.mwvb.blockpuzzle.playingfield.Action;

public class ClusterViewModel {
    private final List<ISpaceObject> spaceObjects;
    private final List<IRoute> routes = new ArrayList<>();
    private final SpaceObjectStates info = new SpaceObjectStates();
    /** Das ist der Planet wo das Raumschiff gerade ist. In Bubble gibt es noch einen weiteren Planeten, der null sein kann, falls gerade in der Karte kein Planet gewählt ist. */
    private IPlanet currentPlanet;
    private final Map<IPlanet, SpaceObjectInfo> infos = new HashMap<>();

    public ClusterViewModel(List<ISpaceObject> spaceObjects, IPlanet planet, Resources resources, Action infoAction) {
        this.spaceObjects = spaceObjects;
        currentPlanet = planet;

        if (Features.deathStar) {
            routes.add(new SpaceNebulaRoute(33, 35, infoAction));
            routes.add(new SpaceNebulaRoute(35, 33, infoAction));
        }

        SpielstandDAO dao = new SpielstandDAO();
        for (ISpaceObject so : spaceObjects) {
            if (so instanceof AbstractPlanet) {
                AbstractPlanet p = (AbstractPlanet) so;

                String infoText1 = resources.getString(p.getName()) + " #" + p.getNumber();
                String infoText2 = createInfoText2(p);
                String infoText3 = createInfoText3(p, dao.load(p), resources);
                infos.put(p, new SpaceObjectInfo(infoText1, infoText2, infoText3));
            }
        }
    }

    private String createInfoText2(AbstractPlanet p) {
        if (p.getGameDefinitions() != null && !p.getGameDefinitions().isEmpty()) {
            return getFirstGameDefinition(p).getDescription(false);
        } else {
            return "";
        }
    }

    @NotNull
    private String createInfoText3(AbstractPlanet p, Spielstand ss, Resources resources) {
        String ret = "";
        int n = p.getGameDefinitions().size();
        if (n == 1 && getFirstGameDefinition(p).showMoves()) {
            int moves = ss.getMoves();
            String add = "";
            if (ss.getOwnerMoves() > 0) {
                moves = ss.getOwnerMoves();
                add = " " + ss.getOwnerName();
            }
            if (moves > 0) {
                ret = resources.getString(R.string.moves) + ": " + moves + add;
            }
        } else if (ss.getScore() > 0) { // TODO  || ss.getOwnerScore() > 0
            int score = ss.getScore();
            String add = "";
            if (ss.getOwnerScore() > 0) {
                score = ss.getOwnerScore();
                add = " " + ss.getOwnerName();
            }
            // TODO **** jux **** Das ist nicht okay hier.   Ticket t/t066bk/7c638a8b008349a2ab66f3f88d724661
            ret = resources.getString(R.string.score2).replace("XX", formatScore(score)) + add;
        }
        return ret;
    }

    private GameDefinition getFirstGameDefinition(IPlanet planet) {
        return planet.getGameDefinitions().get(0);
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

    public SpaceObjectInfo getInfo(IPlanet p) {
        return infos.get(p);
    }

    public void setCurrentPlanet(IPlanet currentPlanet) {
        this.currentPlanet = currentPlanet;
        if (currentPlanet != null) {
            GlobalData gd = GlobalData.get();
            gd.setCurrentPlanet(currentPlanet.getNumber());
            gd.save();
        }
    }

    public IRoute getRoute(int from, int to) {
        for (IRoute route : routes) {
            if (route.getFrom() == from && route.getTo() == to) {
                return route;
            }
        }
        // There is no special route.
        return new IRoute() {
            @Override
            public int getFrom() { // unused
                return from;
            }

            @Override
            public int getTo() { // unused
                return to;
            }

            @Override
            public boolean travel() {
                // do nothing special
                return true;
            }
        };
    }

    public SpaceObjectStates getInfo() {
        return info;
    }
}
