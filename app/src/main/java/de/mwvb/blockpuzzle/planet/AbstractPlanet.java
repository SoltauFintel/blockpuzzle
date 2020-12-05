package de.mwvb.blockpuzzle.planet;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;

public abstract class AbstractPlanet implements IPlanet {
    // Stammdaten
    private final int number;
    private final int x;
    private final int y;
    private final int gravitation;
    private final List<GameDefinition> gameDefinitions = new ArrayList<>();
    // Bewegungsdaten
    private boolean visibleOnMap = true;
    private boolean owner = false;
    private GameDefinition selectedGame = null;
    private String infoText1;
    private String infoText2;
    private String infoText3;

    public AbstractPlanet(int number, int x, int y, int gravitation) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.gravitation = gravitation;
    }

    public AbstractPlanet(int number, int x, int y, int gravitation, GameDefinition gameDefinition) {
        this(number, x, y, gravitation);
        gameDefinitions.add(gameDefinition);
    }

    @Override
    public int getClusterNumber() {
        return 1;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getGravitation() {
        return gravitation;
    }

    @Override
    public boolean isVisibleOnMap() {
        return visibleOnMap;
    }

    @Override
    public void setVisibleOnMap(boolean visibleOnMap) {
        this.visibleOnMap = visibleOnMap;
    }

    @Override
    public boolean isOwner() {
        return owner;
    }

    @Override
    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public List<GameDefinition> getGameDefinitions() {
        return gameDefinitions;
    }

    @Override
    public boolean hasGames() {
        return !gameDefinitions.isEmpty();
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
    public String getInfoText(int lineNumber) {
        if (lineNumber == 1) return infoText1;
        if (lineNumber == 2) return infoText2;
        if (lineNumber == 3) return infoText3;
        return "";
    }

    public void setInfoText1(String infoText1) {
        this.infoText1 = infoText1;
    }

    public void setInfoText2(String infoText2) {
        this.infoText2 = infoText2;
    }

    public void setInfoText3(String infoText3) {
        this.infoText3 = infoText3;
    }
}
