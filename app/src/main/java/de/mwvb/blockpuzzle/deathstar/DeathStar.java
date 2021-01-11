package de.mwvb.blockpuzzle.deathstar;

import android.content.res.Resources;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.AbstractPlanet;
import de.mwvb.blockpuzzle.planet.IPlanet;

public class DeathStar implements IPlanet {
    private final List<GameDefinition> gameDefinitions = new ArrayList<>();
    private int index;
    private Cluster cluster;
    private GameDefinition selectedGame;

    public DeathStar() {
        init();
    }

    private void init() {
        int gpsn = 23;  // TODO speziellen Spielsteinsatz machen
        int mls = 2000;
        if (Features.developerMode) {
            mls = 10;
        }
        gameDefinitions.add(new DeathStarClassicGameDefinition(gpsn, mls, R.string.deathStarGame1));
        gameDefinitions.add(new DeathStarClassicGameDefinition(gpsn, mls, R.string.deathStarGame2));
        gameDefinitions.add(new DeathStarClassicGameDefinition(gpsn, mls, R.string.deathStarGame3));
        index = 0;
    }

    public void resetGame() {
        gameDefinitions.clear();
        init();
        selectedGame = gameDefinitions.get(index);
    }

    @Override
    public int getCurrentGameDefinitionIndex(IPersistence persistence) {
        selectedGame = gameDefinitions.get(index);
        return index;
    }
    public int getGameIndex() {
        return index;
    }
    public void setGameIndex(int v) {
        index = v;
    }

    public GameDefinition nextGame() {
        DeathStarClassicGameDefinition g = null;
        if (!wonAll()) {
            // Find next not won game
            do {
                index = ++index % gameDefinitions.size();
                g = (DeathStarClassicGameDefinition) gameDefinitions.get(index);
            } while (g.isWon()); // jump over already destroyed reactors
        }
        selectedGame = g;
        return selectedGame;
    }

    private boolean wonAll() {
        for (GameDefinition g : gameDefinitions) {
            if (!((DeathStarClassicGameDefinition) g).isWon()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<GameDefinition> getGameDefinitions() {
        return gameDefinitions;
    }

    @Override
    public boolean hasGames() {
        return !gameDefinitions.isEmpty();
    }

    @Override
    public GameDefinition getSelectedGame() {
        return selectedGame;
    }

    @Override
    public void setSelectedGame(GameDefinition v) {
        selectedGame = v;
    }

    @Override
    public int getClusterNumber() {
        return 0; // Milky Way galaxy
    }

    @Override
    public Cluster getCluster() {
        return cluster;
    }

    @Override
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public int getNumber() {
        return 100;
    }

    @Override
    public int getX() {
        return 19; // soll im alpha Q. sein
    }
    @Override
    public int getY() {
        return 19; // soll im alpha Q. sein
    }

    @Override
    public int getRadius() {
        return 10;
    }

    @Override
    public int getGravitation() {
        return 0;
    }

    @Override
    public boolean userMustSelectTerritory() {
        return false;
    }

    @Override
    public String getInfo(IPersistence persistence, Resources resources) {
        return resources.getString(R.string.deathStar) + ", " + resources.getString(R.string.gravitation) + " " + getGravitation() + "\n" + resources.getString(R.string.deathStarGames123);
    }

    @Override
    public String getGameInfo(IPersistence per, Resources resources, int gi) {
        StringBuilder info = new StringBuilder();
        info.append(getGameDefinitions().get(0).getInfo());
        info.append("\n");
        info.append(resources.getString(R.string.score));
        info.append(": ");
        for (int i = 0; i < getGameDefinitions().size(); i++) {
            if (i > 0) {
                info.append(" | ");
            }
            per.setGameID(this, i);
            int score = per.loadScore();
            info.append(AbstractPlanet.thousand(score < 0 ? 0 : score));
        }
        per.setGameID(this, index);
        return info.toString();
    }

    @Override
    public boolean isNextGamePieceResetedForNewGame() {
        return true;
    }

    @Override
    public int getNewLiberationAttemptButtonTextResId() {
        return R.string.giveUpDeathStarGame;
    }

    @Override
    public int getNewLiberationAttemptQuestionResId() {
        return R.string.giveUpDeathStarGameQuestion;
    }

    @Override
    public String getInfoText(int lineNumber) {
        return "";
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isDataExchangeRelevant() {
        return false;
    }

    @Override
    public boolean isShowCoordinates() {
        return false;
    }

    @Override
    public void draw(Canvas canvas, float f) { //
    }

    @Override
    public boolean isVisibleOnMap() {
        return false;
    }

    @Override
    public void setVisibleOnMap(boolean v) { // do nothing
    }

    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public void setOwner(boolean v) { // do nothing
    }
}
