package de.mwvb.blockpuzzle.planet;

import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;

/**
 * Dwarf planet
 */
public class Moon extends AbstractPlanet {

    public Moon(int number, int x, int y) {
        super(number, x, y, 1);
    }

    public Moon(int number, int x, int y, GameDefinition gameDefinition) {
        this(number, x, y, 1, gameDefinition);
    }

    public Moon(int number, int x, int y, int gravitation, GameDefinition gameDefinition) {
        super(number, x, y, gravitation, gameDefinition);
    }

    @Override
    public final int getRadius() {
        return 12;
    }
}
