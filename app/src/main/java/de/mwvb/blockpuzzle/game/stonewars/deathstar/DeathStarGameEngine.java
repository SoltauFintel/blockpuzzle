package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import android.os.Handler;

import de.mwvb.blockpuzzle.game.GameEngineModel;
import de.mwvb.blockpuzzle.game.stonewars.StoneWarsGameEngine;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.global.GlobalData;

/**
 * Death Star game play as a Stone Wars variant
 */
public class DeathStarGameEngine extends StoneWarsGameEngine {

    public DeathStarGameEngine(GameEngineModel model) {
        super(model);
    }

    @Override
    protected boolean isHandleNoGamePiecesAllowed() {
        // Klappt mit true nicht. Eigentlich müsste ich genauer schauen, warum das nicht klappt.
        return false;
    }

    @Override
    protected boolean offerAllowed(boolean newGameMode) {
        return (newGameMode || nextGame()) && super.offerAllowed(newGameMode);
    }

    private boolean nextGame() {
        // save current game
        save();

        // switch to next game
        DeathStar ds = getDeathStar();
        // Wenn ich im 1. Reaktor bin und die aktuelle Score 0 ist, bleibe ich in dem aktuellen Game.
        // Andernfalls weiterschalten:
        if (ds.getGameIndex() > 0 || gs.get().getScore() > 0) {
            if (ds.nextGame()) {
                // nächstes Game!
                GlobalData gd = GlobalData.get();
                gd.setTodessternReaktor(ds.getGameIndex());
                gd.save();
                rebuild = true; // Der MainAc sagen, dass sie erneut Builder aufrufen soll.
            } else {
                deathStarIsDestroyed();
            }
            return false; // abort
        }
        return true; // continue
    }

    private void deathStarIsDestroyed() {
        gs.get().setState(GamePlayState.WON_GAME); // old code: load(ds), state=WON_GAME
        undo = null;
        //noinspection Convert2Lambda
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GlobalData gd = GlobalData.get();
                gd.setTodesstern(0); // deactivate Death Star game play
                gd.setTodessternReaktor(0);
                gd.setCurrentPlanet(1); // Spaceship is catapulted to planet 1 again.
                gd.save();
                model.getView().getSpecialAction(2).execute(); // leave Death Star game (show info activity and then bridge)
            }
        }, 1500); // wait a bit for applause
    }

    private DeathStar getDeathStar() {
        return MilkyWayCluster.INSTANCE.get();
    }

    @Override
    public void undo() { //
    }
}
