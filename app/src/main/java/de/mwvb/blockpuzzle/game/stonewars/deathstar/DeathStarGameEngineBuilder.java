package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.GameEngineModel;
import de.mwvb.blockpuzzle.game.place.DoNothingPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.StoneWarsGameEngineBuilder;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceAdapter;
import de.mwvb.blockpuzzle.global.GlobalData;

/**
 * Initialization of DeathStarGameEngine
 */
public class DeathStarGameEngineBuilder extends StoneWarsGameEngineBuilder {

    // create data ----

    @NonNull
    @Override
    protected IPlaceAction getDetectOneColorAreaAction() {
        // no OneColor bonus
        return new DoNothingPlaceAction();
    }

    @Override
    protected INextGamePiece getNextGamePieceGenerator() {
        // Change color of all game pieces. Each reactor has its own color.
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

    // create game engine ----

    @NotNull
    @Override
    protected DeathStarGameEngine createGameEngine(GameEngineModel model) {
        return new DeathStarGameEngine(model);
    }

    // init game ----

    /*
     TODO nach initgame:            Vielleicht in die DeathStarGameEngine einbauen?
          view.showTerritoryName(getDefinition().getTerritoryName());
     */

    // new game ----

    @Override
    protected void initNextGamePieceForNewGame() {
        // nichts machen, der NextGamePiece Index soll über alle Reaktoren weiter laufen
    }

    // load game ----

    @Override
    protected void loadGame(boolean loadNextGamePiece, boolean checkGame) {
        super.loadGame(loadNextGamePiece, checkGame);
        if (loadNextGamePiece) {
            getDeathStar().setGameIndex(GlobalData.get().getTodessternReaktor());
// TODO            super.offer();
        }
    }

    // service methods ----

    private DeathStar getDeathStar() {
        return MilkyWayCluster.INSTANCE.get();
    }
}
