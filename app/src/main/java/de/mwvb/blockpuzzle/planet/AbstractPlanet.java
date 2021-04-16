package de.mwvb.blockpuzzle.planet;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.cluster.ClusterView;
import de.mwvb.blockpuzzle.cluster.SpaceObjectStates;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.SSLiberatedInfo;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO;

public abstract class AbstractPlanet extends AbstractSpaceObject implements IPlanet {
    private final SpielstandDAO spielstandDAO = new SpielstandDAO();
    // Stammdaten
    private final int gravitation;
    private final List<GameDefinition> gameDefinitions = new ArrayList<>();
    public static Paint ownerMarkerPaint; // set during draw action
    // Bewegungsdaten, nicht persistent
    private GameDefinition selectedGame = null;

    public AbstractPlanet(int number, int x, int y, int gravitation) {
        super(number, x, y);
        this.gravitation = gravitation;
    }

    public AbstractPlanet(int number, int x, int y, int gravitation, GameDefinition gameDefinition) {
        this(number, x, y, gravitation);
        add(gameDefinition);
    }

    @Override
    public String getId() {
        return "C" + getClusterNumber() + "_" + getNumber();
    }

    @Override
    public int getGravitation() {
        return gravitation;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean isDataExchangeRelevant() {
        return true;
    }

    @Override
    public boolean isShowCoordinates() {
        return true;
    }

    @Override
    public String getInfo(Resources resources) {
        // Position
        Cluster cluster = this.getCluster();
        String info = resources.getString(R.string.position) + ":   G=" + cluster.getGalaxyShortName() + "  C=" + cluster.getShortName() +
                "  Q=" + Cluster.getQuadrant(this);
        if (isShowCoordinates()) {
            info += "  X=" + getX() + "  Y=" + getY();
        }

        // Game Info schon etwas früher besorgen, damit die richtige GameDefinition gezogen wird und somit der Daily-Planet-Territory-Name korrekt ist.
        String gameInfo = getGameInfo(resources, -1);

        // Planet
        info += "\n" + resources.getString(getName()) + " #" + getNumber() + ", " + resources.getString(R.string.gravitation) + " " + getGravitation();
        if (getSelectedGame().getTerritoryName() != null) {
            info += "\n" + resources.getString(getSelectedGame().getTerritoryName());
        }

        // Plus game info
        return info + "\n" + gameInfo;
    }

    @Override
    public String getGameInfo(Resources resources, int gi) {
        if (hasGames()) {
            // Game description
            GameDefinition s;
            if (gi >= 0) {
                s = getGameDefinitions().get(gi);
            } else {
                s = getSelectedGame();
                gi = getCurrentGameDefinitionIndex();
            }
            String info = s.getDescription(true);

            // Scores
            Spielstand ss = spielstandDAO.load(this, gi);

            int score = ss.getScore();
            int moves = ss.getMoves();
            if (score > 0) {
                info += "\n" + resources.getString(R.string.yourScoreYourMoves, thousand(score), thousand(moves));
            }

            int otherScore = ss.getOwnerScore();
            int otherMoves = ss.getOwnerMoves();
            if (otherScore > 0) {
                info += "\n" + resources.getString(R.string.scoreOfMoves, ss.getOwnerName(), thousand(otherScore), thousand(otherMoves));
            }

            // Liberated?
            if (ss.getState() != GamePlayState.LOST_GAME && s.isLiberated(new SSLiberatedInfo(ss))) {
                if (userMustSelectTerritory()) {
                    info += "\n" + resources.getString(R.string.liberatedTerritoryByYou);
                } else {
                    info += "\n" + resources.getString(R.string.liberatedPlanetByYou);
                }
            }
            return info;
        } else {
            return resources.getString(R.string.planetNeedsNoLiberation);
        }
    }

    public static String thousand(int n) {
        return new DecimalFormat("#,##0").format(n);
    }

    @Override
    public void draw(Canvas canvas, float f, SpaceObjectStates info) {
        // draw planet
        canvas.drawCircle(getX() * ClusterView.w * f, getY() * ClusterView.w * f, getRadius() * f, getPaint());

        // draw owner mark
        if (info.isOwner(this)) {
            float ax = getX() * ClusterView.w * f + getRadius() * getOwnerMarkXFactor() * f;
            float ay = getY() * ClusterView.w * f - getRadius() * 0.7f * f;
            float bx = ax + 5 * f;
            float by = ay + 5 * f;
            float cx = bx + 5 * f;
            float cy = ay - 10 * f;
            canvas.drawLine(ax, ay, bx, by, ownerMarkerPaint);
            canvas.drawLine(bx, by, cx, cy, ownerMarkerPaint);
        }
    }

    protected abstract Paint getPaint();

    protected float getOwnerMarkXFactor() {
        return 1f;
    }

    public List<GameDefinition> getGameDefinitions() {
        return gameDefinitions;
    }

    public void add(GameDefinition definition) {
        if (definition != null) {
            gameDefinitions.add(definition);
            definition.setPlanet(this);
        }
    }

    @Override
    public boolean hasGames() {
        return !gameDefinitions.isEmpty();
    }

    @Override
    public boolean userMustSelectTerritory() {
        return getGameDefinitions().size() > 1;
    }

    @Override
    public boolean isNextGamePieceResetedForNewGame() {
        return true;
    }

    @Override
    public int getNewLiberationAttemptButtonTextResId() {
        return R.string.newLiberationAttempt;
    }

    @Override
    public int getNewLiberationAttemptQuestionResId() {
        return R.string.newLiberationAttemptQuestion;
    }

    @Override
    public int getCurrentGameDefinitionIndex() {
        return getGameDefinitions().indexOf(getSelectedGame());
    }

    @Override
    public GameDefinition getSelectedGame() {
        return selectedGame == null ? gameDefinitions.get(0) : selectedGame;
    }

    @Override
    public void setSelectedGame(GameDefinition selectedGame) {
        if (!gameDefinitions.contains(selectedGame)) {
            throw new RuntimeException("Given selectedGame is not known for this planet!");
        }
        this.selectedGame = selectedGame;
    }

    @Override
    public void resetGame() {
        SpielstandDAO dao = new SpielstandDAO();
        int gi = getCurrentGameDefinitionIndex();
        Spielstand ss = dao.load(this, gi);
        if (ss.getScore() < 0 && ss.getMoves() == 0) {
            ss.setScore(ss.getScore() - 1);
            if (ss.getScore() > -9999 && ss.getScore() <= -3) {
                // also clear enemy. It's for the case that the player thinks he has no chance to beat the enemy.
                ss.setOwnerName("");
                ss.setOwnerScore(0);
                ss.setOwnerMoves(0);
            }
        } else {
            ss.setScore(-1);
            ss.setMoves(0);
        }
        dao.save(this, gi, ss);

        new SpaceObjectStateService().saveOwner(this, false);
    }
}
