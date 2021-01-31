package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import android.os.Handler;

import de.mwvb.blockpuzzle.game.GameEngineFactory;
import de.mwvb.blockpuzzle.game.GameEngineModel;
import de.mwvb.blockpuzzle.game.stonewars.StoneWarsGameEngine;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
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
    protected void postDispatch() {
        if (model.getHolders().is123Empty() && gs.get().getMoves() > 0) {
            setDragAllowed(false); // Don't allow player to drag something during wait time.
            //noinspection Convert2Lambda
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    offer();
                    if (!gs.isLostGame()) { // TO-DO evtl. Prüfung auf PLAYING
                        checkGame();
                        save();
                    }
                    setDragAllowed(true);
                }
            }, 1200); // let the player see his last move and see the row explosion
        } else {
            super.postDispatch();
        }
    }

    @Override
    protected boolean offerAllowed() {
        return nextGame() && super.offerAllowed();
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
// TODO
//                gs = StoneWarsGameState.create();
                GlobalData gd = GlobalData.get();
                gd.setTodessternReaktor(ds.getGameIndex());
                gd.save();
            } else {
                deathStarIsDestroyed();
                return false; // abort
            }
        }
// TODO
//        if (new SpielstandDAO().load(ds).getScore() >= 0) { // Is there a game state?
//            loadGame(false, false);
//        } else {
//            doNewGame();
//        }
        model.getView().showTerritoryName(((GameDefinition) getDefinition()).getTerritoryName());
        return true; // continue
    }

    private void deathStarIsDestroyed() {
        gs.get().setState(GamePlayState.WON_GAME); // old code: load(ds), state=WON_GAME
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
        return (DeathStar) new GameEngineFactory().getPlanet();
    }

// TO-DO
//  check4Liberation wird glaubich immer aufgerufen wenn ich gewonnen habe. Wenn ich das DeathStar Game teste, muss ich das hier reparieren.
//    @Override
//    protected void check4Liberation() {
//        holders.clearAll(); // Spieler soll keine Spielsteine mehr setzen können. Das bewirkt außerdem auch, dass offer() aufgerufen
//        // wird und somit nextGame().
//    }
}
