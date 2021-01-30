package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import android.content.res.Resources;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.cluster.SpaceObjectStates;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO;
import de.mwvb.blockpuzzle.global.Features;
import de.mwvb.blockpuzzle.global.GlobalData;
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
        int gpsn = 23;  // TO-DO speziellen Spielsteinsatz machen
        int mls = 2000;
        if (Features.developerMode) {
            mls = 10;
        }
        gameDefinitions.add(new DeathStarClassicGameDefinition(gpsn, mls, R.string.deathStarGame1));
        gameDefinitions.add(new DeathStarClassicGameDefinition(gpsn, mls, R.string.deathStarGame2));
        gameDefinitions.add(new DeathStarClassicGameDefinition(gpsn, mls, R.string.deathStarGame3));
        index = 0;
    }

    @Override
    public String getId() {
        return "C" + cluster.getNumber() + "_" + getNumber();
    }

    @Override
    public void resetGame() {
        SpielstandDAO dao = new SpielstandDAO();
        for (int i = 0; i < getGameDefinitions().size(); i++) {
            Spielstand ss = dao.load(this, i);
            ss.setScore(-9999);
            ss.setNextRound(0);
            dao.save(this, i, ss);
        }

        gameDefinitions.clear();
        init();
        selectedGame = gameDefinitions.get(index);

        GlobalData gd = GlobalData.get();
        gd.setTodesstern(0);
        gd.setTodessternReaktor(0);
        gd.setCurrentPlanet(1); // Spaceship is catapulted to planet 1 again.
        gd.save();
        // TO-DO Man könnte InfoAc anzeigen: "Roter Alarm. Captain, wir wurde erneut in die Y G. katapultiert. Wie konnte das erneut passieren? Hat jemand einen
        //       falschen Button gedrückt? ;-) Ein vollständiger Systemcheck wäre gut."
    }

    @Override
    public int getCurrentGameDefinitionIndex() {
        selectedGame = gameDefinitions.get(index);
        return index;
    }
    public int getGameIndex() {
        return index;
    }
    public void setGameIndex(int v) {
        index = v;
    }

    /**
     * @return true: switch to next alive reactor was successfully,
     *         false: no more reactor alive. Death Star destroyed!
     */
    public boolean nextGame() {
        DeathStarClassicGameDefinition g = null;
        if (!wonAll()) {
            // Find next not won game
            do {
                index = ++index % gameDefinitions.size();
                g = (DeathStarClassicGameDefinition) gameDefinitions.get(index);
            } while (g.isWon()); // jump over already destroyed reactors
        }
        selectedGame = g;
        return selectedGame != null;
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

    /**
     * @return null if game over
     */
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
    public String getInfo(Resources resources) {
        return resources.getString(R.string.deathStar) + ", " + resources.getString(R.string.gravitation) + " " + getGravitation() + "\n" + resources.getString(R.string.deathStarGames123);
    }

    @Override
    public String getGameInfo(Resources resources, int gi) {
        StringBuilder info = new StringBuilder();
        info.append(getGameDefinitions().get(0).getDescription(true));
        info.append("\n");
        info.append(resources.getString(R.string.score));
        info.append(": ");
        SpielstandDAO dao = new SpielstandDAO();
        for (int i = 0; i < getGameDefinitions().size(); i++) {
            if (i > 0) {
                info.append(" | ");
            }
            int score = dao.load(this, i).getScore();
            info.append(AbstractPlanet.thousand(Math.max(score, 0)));
        }
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
    public void draw(Canvas canvas, float f, SpaceObjectStates info) { //
    }

    @Override
    public int getName() {
        return R.string.deathStar;
    }
}
