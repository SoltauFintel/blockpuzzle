package de.mwvb.blockpuzzle.deathstar;

import android.os.Handler;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.game.StoneWarsGame;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceAdapter;
import de.mwvb.blockpuzzle.persistence.IPersistence;

/**
 * Death Star game play as a Stone Wars variant
 */
public class DeathStarGame extends StoneWarsGame {

    public DeathStarGame(IGameView view) {
        super(view);
    }

    @Override
    public void initGame() {
        super.initGame();
        view.showTerritoryName(definition.getTerritoryName());
    }

    @Override
    protected void postDispatch() {
        if (holders.is123Empty() && moves > 0) {
            setDragAllowed(false); // Don't allow player to drag something during wait time.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    offer();
                    if (!gameOver) {
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
        if (ds.getGameIndex() > 0 || punkte > 0) {
            definition = ds.nextGame(); // gape Zugriffe zeigen nun auf diese GameDefinition
            // definition is null at this point if Death Star is  destroyed.
            if (deathStarDestroyed()) {
                return false; // abort
            }
        }
        punkte = gape.loadScore();
        if (punkte < 0) {
            doNewGame();
        } else {
            loadGame(false);
        }
        view.showTerritoryName(definition.getTerritoryName());
        return true; // continue
    }

    private boolean deathStarDestroyed() {
        if (definition == null) { // looks like Death Star is destroyed
            IPersistence per = gape.get();
            gameOver = true;
            per.saveGameOver(gameOver);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    per.saveDeathStarMode(0); // deactivate Death Star game play
                    per.saveCurrentPlanet(1, 1); // Spaceship is catapulted to planet 1 again.
                    view.getSpecialAction(2).execute(); // leave Death Star game (show info activity and then bridge)
                }
            }, 1500); // wait a bit for applause
            return true;
        }
        return false;
    }

    private DeathStar getDeathStar() {
        return (DeathStar) gape.getPlanet();
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

    @Override
    protected void detectOneColorArea() { // no OneColor bonus
    }

    @Override
    protected void initNextGamePieceForNewGame() {
        // nichts machen, der NextGamePiece Index soll über alle Reaktoren weiter laufen
    }

    @Override
    protected void check4Liberation() {
        holders.clearAll(); // Spieler soll keine Spielsteine mehr setzen können. Das bewirkt außerdem auch, dass offer() aufgerufen
        // wird und somit nextGame().
    }
}
