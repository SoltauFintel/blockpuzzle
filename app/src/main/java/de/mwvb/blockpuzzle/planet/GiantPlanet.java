package de.mwvb.blockpuzzle.planet;

import android.graphics.Paint;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;

public class GiantPlanet extends AbstractPlanet {
    public static Paint paint; // set during draw action

    public GiantPlanet(int number, int x, int y, GameDefinition g1, GameDefinition g2, GameDefinition g3) {
        this(number, x, y, 7, g1, g2, g3);
    }

    public GiantPlanet(int number, int x, int y, int gravitation, GameDefinition g1, GameDefinition g2, GameDefinition g3) {
        super(number, x, y, gravitation, g1);
        add(g2);
        add(g3);
    }

    @Override
    public final int getRadius() {
        return 30;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public int getName() {
        return R.string.giantPlanet;
    }
}
