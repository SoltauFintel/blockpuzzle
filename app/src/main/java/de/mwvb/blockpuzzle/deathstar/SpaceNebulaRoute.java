package de.mwvb.blockpuzzle.deathstar;

import de.mwvb.blockpuzzle.cluster.AbstractRoute;
import de.mwvb.blockpuzzle.game.Game;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.Action;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

/**
 * Route through the space nebula leads to the Death Star, which threatens the Milky Way.
 */
public class SpaceNebulaRoute extends AbstractRoute {
    private final IPersistence per;
    private final Action alertAction;

    public SpaceNebulaRoute(int from, int to, IPersistence persistence, Action alertAction) {
        super(from, to);
        per = persistence;
        this.alertAction = alertAction;
    }

    @Override
    public boolean travel() {
        startDeathStarGamePlay();
        return false;
    }

    private void startDeathStarGamePlay() {
        IPlanet ds = MilkyWayCluster.INSTANCE.get();
        per.saveCurrentPlanet(ds.getClusterNumber(), ds.getNumber());
        for (int i = 0; i < ds.getGameDefinitions().size(); i++) {
            per.setGameID(ds, i);
            per.saveScore(-9999);
            per.save(new PlayingField(Game.blocks)); // clear playing field
            per.save(-1, null); // clear parking
        }
        per.saveDeathStarMode(1);

        // show Milky Way alert
        alertAction.execute();
        // and after that the game will be displayed immediately
    }

    public static boolean isNoDeathStarMode(IPersistence per) {
        return per.loadDeathStarMode() != 1;
    }
}
