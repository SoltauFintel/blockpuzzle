package de.mwvb.blockpuzzle.planet;

import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;

public class Planet extends AbstractPlanet {

    public Planet(int number, int x, int y) {
        super(number, x, y, 5);
    }

    public Planet(int number, int x, int y, GameDefinition gameDefinition) {
        this(number, x, y, 5, gameDefinition);
    }

    public Planet(int number, int x, int y, int gravitation, GameDefinition gameDefinition) {
        super(number, x, y, gravitation, gameDefinition);
    }

    @Override
    public final int getRadius() {
        return 20;
    }
}
