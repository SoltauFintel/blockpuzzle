package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.GameEngineModel;
import de.mwvb.blockpuzzle.game.place.DoNothingPlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.StoneWarsGameEngineBuilder;
import de.mwvb.blockpuzzle.game.stonewars.deathstar.place.DeathStarCheck4VictoryPlaceAction;
import de.mwvb.blockpuzzle.game.stonewars.place.Check4VictoryPlaceAction;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.INextRound;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceAdapter;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO;
import de.mwvb.blockpuzzle.global.Features;

/**
 * Initialization of DeathStarGameEngine
 */
public class DeathStarGameEngineBuilder extends StoneWarsGameEngineBuilder {
    private static final SpielstandDAO dao = new SpielstandDAO();
    private static final int NEXTROUND_INDEX = 99; // Ich will eine eigene Datei für den Todesstern-NextRound-Wert.
    private static final Spielstand nextRoundSS = new Spielstand();

    // create data ----

    @NonNull
    @Override
    protected IPlaceAction getDetectOneColorAreaAction() {
        // no OneColor bonus
        return new DoNothingPlaceAction();
    }

    public static void writeNextRound(int val) {
        nextRoundSS.setNextRound(val);
        dao.save(MilkyWayCluster.INSTANCE.get(), NEXTROUND_INDEX, nextRoundSS);
    }

    @Override
    protected INextGamePiece getNextGamePieceGenerator() {
        // Spielsteine sollen nicht pro Reaktor sein, sondern für Todesstern.
        final DeathStar planet = getDeathStar();
        INextRound persistence = new INextRound() {
            @Override
            public void saveNextRound(int val) {
                writeNextRound(val);
            }

            @Override
            public int getNextRound() {
                return dao.load(planet, NEXTROUND_INDEX).getNextRound();
            }
        };
        NextGamePieceFromSet nextGamePiece = new NextGamePieceFromSet(planet.getGameDefinitions().get(0).getGamePieceSetNumber(), persistence);
        // Change color of all game pieces. Each reactor has its own color.
        return new NextGamePieceAdapter(nextGamePiece) {
            @Override
            public GamePiece next(BlockTypes blockTypes) {
                GamePiece gp = super.next(blockTypes);
                gp.color(getColor());
                return gp;
            }

            private int getColor() {
                switch (getDeathStar().getGameIndex()) {
                    case 0:
                        if (Features.developerMode) return 6; // yellow
                        return 11; // dark blue for 1st reactor
                    case 1:  return  4; // blue for 2nd reactor
                    default:
                        if (Features.developerMode) return 3; // red
                        return  5; // pink for last reactor
                }
            }
        };
    }

    @NotNull
    @Override
    protected Check4VictoryPlaceAction getCheck4VictoryPlaceAction() {
        return new DeathStarCheck4VictoryPlaceAction(); // Bei Sieg die Holders clearen.
    }

    // create game engine ----

    @NotNull
    @Override
    protected DeathStarGameEngine createGameEngine(GameEngineModel model) {
        return new DeathStarGameEngine(model);
    }

    // new game ----

    @Override
    protected void initNextGamePieceForNewGame() {
        nextGamePiece.load();
    }

    // load game ----

    @Override
    protected void loadGame(boolean loadNextGamePiece, boolean checkGame) {
        super.loadGame(loadNextGamePiece, checkGame);
        if (loadNextGamePiece) {
            if (holders.is123Empty()) {
                gameEngine.offer(true/*hier ausnahmsweise true!*/);
            }
        }
    }

    // service methods ----

    private DeathStar getDeathStar() {
        return MilkyWayCluster.INSTANCE.get();
    }
}
