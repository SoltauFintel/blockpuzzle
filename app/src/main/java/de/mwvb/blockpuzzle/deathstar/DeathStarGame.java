package de.mwvb.blockpuzzle.deathstar;

import android.os.Handler;

import androidx.annotation.NonNull;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.GameEngineFactory;
import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.game.place.DoNothingPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.StoneWarsGame;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceAdapter;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;
import de.mwvb.blockpuzzle.global.GlobalData;

/**
 * Death Star game play as a Stone Wars variant
 */
public class DeathStarGame extends StoneWarsGame {

    public DeathStarGame(IGameView view, StoneWarsGameState gs) {
        super(view, gs);
    }

    @Override
    public void initGame() {
        super.initGame();
        view.showTerritoryName(getDefinition().getTerritoryName());
    }

    @Override
    protected void loadGame(boolean loadNextGamePiece, boolean checkGame) {
        super.loadGame(loadNextGamePiece, checkGame);
        if (loadNextGamePiece) {
            getDeathStar().setGameIndex(GlobalData.get().getTodessternReaktor());
            super.offer();
        }
    }

    @Override
    protected void postDispatch() {
        if (holders.is123Empty() && gs.get().getMoves() > 0) {
            setDragAllowed(false); // Don't allow player to drag something during wait time.
            //noinspection Convert2Lambda
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    offer();
                    if (!gs.isGameOver()) {
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
    protected void offer() {
        if (nextGame()) {
            super.offer();
        }
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
                gs = StoneWarsGameState.create();
                GlobalData gd = GlobalData.get();
                gd.setTodessternReaktor(ds.getGameIndex());
                gd.save();
            } else {
                deathStarIsDestroyed();
                return false; // abort
            }
        }
        if (new SpielstandDAO().load(ds).getScore() >= 0) { // Is there a game state?
            loadGame(false, false);
        } else {
            doNewGame();
        }
        view.showTerritoryName(getDefinition().getTerritoryName());
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
                view.getSpecialAction(2).execute(); // leave Death Star game (show info activity and then bridge)
            }
        }, 1500); // wait a bit for applause
    }

    private DeathStar getDeathStar() {
        return (DeathStar) new GameEngineFactory().getPlanet();
    }

    // Change color of all game pieces. Each reactor has its own color.
    @Override
    protected INextGamePiece getNextGamePieceGenerator() {
        return new NextGamePieceAdapter(super.getNextGamePieceGenerator()) {
            @Override
            public GamePiece next(BlockTypes blockTypes) {
                GamePiece gp = super.next(blockTypes);
                gp.color(getColor());
                return gp;
            }

            private int getColor() {
                switch (getDeathStar().getGameIndex()) {
                    case 0:  return 11; // dark blue
                    case 1:  return  4; // blue
                    default: return  5; // pink  for case 2
                }
            }
        };
    }

    @NonNull
    @Override
    protected IPlaceAction getDetectOneColorAreaAction() {
        // no OneColor bonus
        return new DoNothingPlaceAction();
    }

    @Override
    protected void initNextGamePieceForNewGame() {
        // nichts machen, der NextGamePiece Index soll über alle Reaktoren weiter laufen
    }

// TODO
//  check4Liberation wird glaubich immer aufgerufen wenn ich gewonnen habe. Wenn ich das DeathStar Game teste, muss ich das hier reparieren.
//    @Override
//    protected void check4Liberation() {
//        holders.clearAll(); // Spieler soll keine Spielsteine mehr setzen können. Das bewirkt außerdem auch, dass offer() aufgerufen
//        // wird und somit nextGame().
//    }
}
