package de.mwvb.blockpuzzle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.logic.Game;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein;

/**
 * Im unteren Bereich die View Komponente, die ein Spielstein (oder einen leeren Spielstein) enthält.
 * Aus der TeilView erfolgt die Drag-and-Drop Operation.
 * Die 4. TeilView ist das Parking Area zum vorübergehenden Ablegen eines Teil.
 * Teil ist der alte Name für Spielstein; daher TeilView.
 */
@SuppressLint("ViewConstructor")
public class TeilView extends View {
    private static final Spielstein EMPTY = new Spielstein();
    private final boolean parking;
    private final Paint p_normal = new Paint(); // TODO final
    private final Paint p_grey = new Paint();
    private final Paint p_drehmodus = new Paint();
    private final Paint p_parking = new Paint();
    private Spielstein teil = null;
    /** grey wenn Teil nicht dem Quadrat hinzugefügt werden kann, weil kein Platz ist */
    private boolean grey = false;
    private boolean dragMode = false;
    private boolean drehmodus = false;

    public TeilView(Context context, boolean parking) {
        super(context);
        this.parking = parking;
        // TODO Farben nach colors.xml
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
        final float f = getResources().getDisplayMetrics().density;
        int br = SpielfeldView.w / Game.blocks; // 60px, auf Handy groß = 36
        if (!dragMode) br /= 2;
        float p = br * 0.1f;
        if (parking && !dragMode) {
            canvas.drawRect(0, 0, br * Spielstein.max * f, br * Spielstein.max * f, p_parking);
        }
        Spielstein dasteil = teil == null ? EMPTY : teil;
        Paint fuellung;
        if (grey) {
            fuellung = p_grey;
        } else if (drehmodus) {
            fuellung = p_drehmodus;
        } else {
            fuellung = p_normal;
        }
        // TODO Ist das doppelter Code zu SpielfeldView?
        for (int x = 0; x < Spielstein.max; x++) {
            for (int y = 0; y < Spielstein.max; y++) {
                if (dasteil.filled(x, y)) {
                    float tx = x * br, ty = y * br;
                    canvas.drawRect((tx + p) * f, (ty + p) * f,
                            (tx + br - p) * f, (ty + br - p) * f, fuellung);
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

    @Override
    public boolean performClick() {
        // wegen Warning in MainActivity.initClickListener()
        return super.performClick();
    }
}
