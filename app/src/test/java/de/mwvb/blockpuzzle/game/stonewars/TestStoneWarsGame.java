package de.mwvb.blockpuzzle.game.stonewars;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.mwvb.blockpuzzle.game.IGameView;
import de.mwvb.blockpuzzle.game.StoneWarsGame;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess;
import de.mwvb.blockpuzzle.gamepiece.Holders;
import de.mwvb.blockpuzzle.persistence.GamePersistence;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;

public class TestStoneWarsGame extends StoneWarsGame {
    private final Stack<Integer> expectedResources = new Stack<>();

    public TestStoneWarsGame(IGameView view) {
        super(view);
    }

    public TestStoneWarsGame(IGameView view, IPersistence persistence) {
        super(view, persistence);
    }

    public GameDefinition getDefinition() {
        return definition;
    }

    public GamePersistence getGape() {
        return gape;
    }

    public IPlanet getPlanet() {
        return gape.getPlanet();
    }

    public Holders getHolders() {
        return holders;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    @Override
    public Stack<Integer> getExpectedResources() {
        return expectedResources;
    }
}
