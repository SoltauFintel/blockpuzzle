package de.mwvb.blockpuzzle.planet;

import android.graphics.Paint;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;

/**
 * Dwarf planet
 */
public class Moon extends AbstractPlanet {
    public static Paint paint; // set during draw action

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

    @Override
    protected float getOwnerMarkXFactor() {
        return 1.33f;
    }

    @Override
    public int getName() {
        return R.string.moon;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }
}
