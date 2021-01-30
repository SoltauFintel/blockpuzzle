package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import de.mwvb.blockpuzzle.cluster.AbstractRoute;
import de.mwvb.blockpuzzle.gamestate.SpielstandService;
import de.mwvb.blockpuzzle.global.GlobalData;
import de.mwvb.blockpuzzle.playingfield.Action;

/**
 * Route through the space nebula leads to the Death Star, which threatens the Milky Way.
 */
public class SpaceNebulaRoute extends AbstractRoute {
    private final Action alertAction;

    public SpaceNebulaRoute(int from, int to, Action alertAction) {
        super(from, to);
        this.alertAction = alertAction;
    }

    @Override
    public boolean travel() {
        new SpielstandService().startDeathStarGamePlay();

        // show Milky Way alert
        alertAction.execute();
        // and after that the game will be displayed immediately
        return false;
    }

    public static boolean isNoDeathStarMode() {
        return GlobalData.get().getTodesstern() != 1;
    }
}
