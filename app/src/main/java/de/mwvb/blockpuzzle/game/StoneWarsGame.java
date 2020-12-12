package de.mwvb.blockpuzzle.game;

import android.app.Activity;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.persistence.IPersistence;

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
        definition = gape.init4StoneWars().getSelectedGame();
    }

    @Override
    public boolean isNewGameButtonVisible() {
        return false;
    }

    @Override
    protected INextGamePiece getNextGamePieceGenerator() {
        return new NextGamePieceFromSet(definition.getGamePieceSetNumber(), gape);
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
        String msg = definition.scoreChanged(punkte, moves, gape.getPlanet(), false, gape, getResourceAccess());
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
        String msg = definition.scoreChanged(punkte, moves, gape.getPlanet(), won, gape, getResourceAccess());
        if (msg != null) {
            view.showToast(msg);
            won = msg.startsWith("+");
            if (won) {
                check4Liberation();
            }
        }
        if (playingField.getFilled() == 0 && definition.onEmptyPlayingField()) {
            gameOverOnEmptyPlayingField();
        }
    }

    private void check4Liberation() {
        save();
        if (new GameInfoService().isPlanetFullyLiberated(gape.getPlanet(), gape.getPersistenceOK())) {
            new GameInfoService().executeLiberationFeature(gape.getPlanet(), gape.getPersistenceOK());
        }
    }

    @Override
    protected void onGameOver() {
        super.onGameOver();
        gape.setOwnerToOrangeUnion();
        punkte = 0;
        moves = 0;
    }

    private void gameOverOnEmptyPlayingField() {
        holders.clearAll();
        won = true;
        gameOver = true;
        playingField.gameOver();
        if (definition.isLiberated(punkte, moves, gape.loadOwnerScore(), gape.loadOwnerMoves())) {
            // Folgende Aktionen dürfen nur bei einem 1-Game-Planet gemacht werden! Ein Cleaner Game wird aber auch nur bei 1-Game-Planets angeboten.
            // Daher passt das.
            gape.setOwnerToMe();
            check4Liberation();
        }
        view.showToast(getResourceAccess().getString(R.string.planetLiberated));
    }

    @Override
    public boolean gameCanBeWon() {
        return definition.gameCanBeWon();
    }

    @Override
    protected int getGravitationStartRow() {
        return gape.getPlanet().getGravitation();
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
}
