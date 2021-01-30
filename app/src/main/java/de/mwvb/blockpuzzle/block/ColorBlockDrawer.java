package de.mwvb.blockpuzzle.block;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.ColorInt;

public class ColorBlockDrawer implements IBlockDrawer {
    private final Paint paint;
    private final Paint darkPaint;
    private final Paint brightPaint;
    private final Paint backgroundPaint;
    private final Paint whitePaint;

    public ColorBlockDrawer(View view, @ColorInt int color) {
        this(view, color, color, color);
    }

        /**
         * Auf Dauer soll das raus fliegen und nur noch mit R color constant gearbeitet werden.
         * @param view -
         * @param color effective color, außen
         * @param color_i effective color, innen
         * @param color_ib effective color, Rahmen von innen
         */
    public ColorBlockDrawer(View view, @ColorInt int color, @ColorInt int color_i, @ColorInt int color_ib) {
        paint = new Paint();
        paint.setColor(color);

        darkPaint = new Paint();
        darkPaint.setColor(color_i);

        brightPaint = new Paint();
        brightPaint.setColor(color_ib);
        brightPaint.setStyle(Paint.Style.STROKE);
        brightPaint.setStrokeWidth(2f);

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.5f;
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.HSVToColor(hsv));

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
    }

    /**
     * @param view -
     * @param color R color constant (not effective color value)  äußere Farbe
     * @param color_i innere Farbe
     * @param color_ib Rahmen der inneren Farbe
     * @return ColorBlockDrawer
     */
    public static ColorBlockDrawer byRColor(View view, int color, int color_i, int color_ib) {
        return new ColorBlockDrawer(view, view.getResources().getColor(color), view.getResources().getColor(color_i), view.getResources().getColor(color_ib));
    }

    @Override
    public void draw(float tx, float ty, BlockDrawParameters p) {
        // Grundfläche
        float l = tx * p.getF();
        float o = ty * p.getF();
        float r = (tx + p.getBr()) * p.getF();
        float u = (ty + p.getBr()) * p.getF();
        p.getCanvas().drawRect(l, o, r, u, backgroundPaint);

        // Hauptfläche
        final float a = 1f;
        l = (tx + a) * p.getF();
        o = (ty + a) * p.getF();
        r = (tx + p.getBr() - a) * p.getF();
        u = (ty + p.getBr() - a) * p.getF();
        p.getCanvas().drawRect(l, o, r, u, paint);

        // weißes Kästchen oben links
        float b = 3f;
        r = (tx + b) * p.getF();
        u = (ty + b) * p.getF();
        p.getCanvas().drawRect(l, o, r, u, whitePaint);

// Baustelle
//        // helle Linie oben
//        b = 10;
//        l = (tx + b) * p.getF();
//        r = (tx + p.getBr() - b) * p.getF();
//        p.getCanvas().drawLine(l, o, r, o, whitePaint);
//
//        // helle Linie links
//        l = (tx + a) * p.getF();
//        o = (ty + a + b) * p.getF();
//        u = (ty + p.getBr() - a - b) * p.getF();
//        p.getCanvas().drawLine(l, o, l, u, whitePaint);

        // innere Fläche mit Rahmen
        float fa = 5f;
        float li = (tx + p.getP() * fa) * p.getF();
        float oi = (ty + p.getP() * fa) * p.getF();
        float ri = (tx + p.getBr() - p.getP() * fa) * p.getF();
        float ui = (ty + p.getBr() - p.getP() * fa) * p.getF();
        RectF rect = new RectF(li, oi, ri, ui);
        p.getCanvas().drawRect(rect, darkPaint);
        p.getCanvas().drawRect(rect, brightPaint);
    }
}
