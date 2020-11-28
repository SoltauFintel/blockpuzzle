package de.mwvb.blockpuzzle.game;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.GameState;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.LiberatedFeature;
import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Stone Wars game engine
 */
public class StoneWarsGame extends Game {
    private GameDefinition definition;

    public StoneWarsGame(IGameView view) {
        super(view);
    }

    public StoneWarsGame(IGameView view, IPersistence persistence) {
        super(view, persistence);
    }

    @Override
    protected void initGameAndPersistence() {
        IPlanet planet = GameState.INSTANCE.getPlanet();
        definition = planet.getSelectedGame();
        persistence.setGameID(planet);
    }

    @Override
    protected INextGamePiece getNextGamePieceGenerator() {
        return new NextGamePieceFromSet(definition.getGamePieceSetNumber(), persistence);
    }

    @Override
    protected void initPlayingField() {
        super.initPlayingField();
        definition.fillStartPlayingField(playingField);
    }

    @Override
    protected void loadGame() {
        super.loadGame();
        // calculate won [for classic game]
        String msg = definition.scoreChanged(punkte, moves, GameState.INSTANCE.getPlanet(), false, persistence, getResourceAccess());
        won = (msg != null && msg.startsWith("+"));
        // calculate game over [for cleaner game]
        if (playingField.getFilled() == 0 && definition.onEmptyPlayingField()) {
            gameOver = true;
        }
    }

    @Override
    protected void offer() {
        if (!gameOver || definition.offerNewGamePiecesAfterGameOver()) {
            super.offer();
        }
    }

    @Override
    protected void checkForVictory() {
        IPlanet planet = GameState.INSTANCE.getPlanet();
        String msg = definition.scoreChanged(punkte, moves, planet, won, persistence, getResourceAccess());
        if (msg != null) {
            view.showToast(msg);
            won = msg.startsWith("+");
            if (won) {
                save();
                if (new GameInfoService().isPlanetFullyLiberated(planet)) {
                    new GameInfoService().executeLiberationFeature(planet);
                }
            }
        }
        if (playingField.getFilled() == 0 && definition.onEmptyPlayingField()) {
            gameOverOnEmptyPlayingField();
        }
    }

    @NotNull
    private ResourceAccess getResourceAccess() {
        ResourceAccess ret;
        if (view instanceof Activity) {
            ret = new ResourceAccess() {
                @Override
                public String getString(int resId) {
                    return ((Activity) view).getResources().getString(resId);
                }
            };
        } else { // testcases
            ret = new ResourceAccess() {
                @Override
                public String getString(int resId) {
                    return "#" + resId;
                }
            };
        }
        return ret;
    }

    private void gameOverOnEmptyPlayingField() {
        holders.clearAll();
        won = true;
        gameOver = true;
        playingField.gameOver();
        if (definition.isLiberated(punkte, moves, persistence.loadOwnerScore(), persistence.loadOwnerMoves())) {
            persistence.clearOwner();
            IPlanet planet = GameState.INSTANCE.getPlanet();
            planet.setOwner(true);
            persistence.savePlanet(planet);

            save();
            if (new GameInfoService().isPlanetFullyLiberated(planet)) {
                new GameInfoService().executeLiberationFeature(planet);
            }
        }
    }

    @Override
    public boolean gameCanBeWon() {
        return definition.gameCanBeWon();
    }

    @Override
    protected int getGravitationStartRow() {
        return GameState.INSTANCE.getPlanet().getGravitation();
    }
}
