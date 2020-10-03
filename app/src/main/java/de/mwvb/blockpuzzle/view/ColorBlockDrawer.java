package de.mwvb.blockpuzzle.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ColorBlockDrawer implements IBlockDrawer {
    private final Paint paint;

    /**
     * Auf Dauer soll das raus fliegen und nur noch mit R color constant gearbeitet werden.
     * @param view
     * @param color effective color
     */
    public ColorBlockDrawer(View view, int color) {
        paint = new Paint();
        paint.setColor(color);
    }

    /**
     * @param view
     * @param color R color constant (not effective color value)
     * @return ColorBlockDrawer
     */
    public static ColorBlockDrawer byRColor(View view, int color) {
        return new ColorBlockDrawer(view, view.getResources().getColor(color));
    }

    @Override
    public void draw(Canvas canvas, float tx, float ty, float p, int br, float f) {
        canvas.drawRect((tx + p) * f,
                (ty + p) * f,
                (tx + br - p) * f,
                (ty + br - p) * f,
                paint);
    }
}
