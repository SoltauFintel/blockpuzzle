package de.mwvb.blockpuzzle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.logic.spielstein.Spielstein;
import de.mwvb.blockpuzzle.logic.spielstein.*;

/**
 * Im unteren Bereich die View Komponente, die ein Teil (oder ein leeres Teil) enthält.
 * Aus der TeilView erfolgt die DragNDrop Operation.
 * Die 4. TeilView ist das Parking Area zum vorübergehenden Ablegen eines Teil.
 */
public class TeilView extends View {
    private final boolean parking;
    private Paint p_normal = new Paint();
    private Paint p_grey = new Paint();
    private Paint p_drehmodus = new Paint();
    private Paint p_parking = new Paint();
    private Spielstein teil = null;
    /** grey wenn Teil nicht dem Quadrat hinzugefügt werden kann, weil kein Platz ist */
    private boolean grey = false;
    private boolean dragMode = false;
    private boolean drehmodus = false;

    public TeilView(Context context, boolean parking) {
        super(context);
        this.parking = parking;
        p_normal.setColor(Color.parseColor("#a65726"));
        p_grey.setColor(Color.parseColor("#888888"));
        p_drehmodus.setColor(Color.parseColor("#009900"));
        p_parking.setColor(Color.parseColor("#ccccff"));
    }

    public void setTeil(Spielstein v) {
        teil = v;
        draw();
    }

    public Spielstein getTeil() {
        return teil;
    }

    public boolean isParking() {
        return parking;
    }

    public void setGrey(boolean v) {
        grey = v;
    }

    public boolean isGrey() {
        return grey;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int br = dragMode ? 60 : 30;
        float p = br * 0.1f;
        if (parking && !dragMode) {
            canvas.drawRect(0, 0, br * Spielstein.max, br * Spielstein.max, p_parking);
        }
        Spielstein dasteil = teil == null ? new Spielstein() : teil;
        Paint fuellung;
        if (grey) {
            fuellung = p_grey;
        } else if (drehmodus) {
            fuellung = p_drehmodus;
        } else {
            fuellung = p_normal;
        }
        for (int x = 0; x < Spielstein.max; x++) {
            for (int y = 0; y < Spielstein.max; y++) {
                if (dasteil.filled(x, y)) {
                    float tx = x * br, ty = y * br;
                    canvas.drawRect(tx + p, ty + p,
                            tx + br - p, ty + br - p, fuellung);
                }
            }
        }
        super.onDraw(canvas);
    }

    public void draw() {
        invalidate();
        requestLayout();
    }

    public void startDragMode() {
        dragMode = true;
        setVisibility(View.INVISIBLE);
    }

    public void endDragMode() {
        dragMode = false;
        setVisibility(View.VISIBLE);
    }

    public void setDrehmodus(boolean d) {
        drehmodus = d;
        draw();
    }

    public void rotate() {
        teil.rotateToRight();
        draw();
    }
}
