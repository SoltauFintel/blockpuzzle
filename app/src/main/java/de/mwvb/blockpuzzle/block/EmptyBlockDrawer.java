package de.mwvb.blockpuzzle.block;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import de.mwvb.blockpuzzle.global.GlobalData;

public class EmptyBlockDrawer implements IBlockDrawer {
    private final Paint paint;

    public EmptyBlockDrawer(View view) {
        paint = new Paint();
        if (GlobalData.get().isSunMode()) {
            paint.setColor(Color.rgb(90,90,90));
        } else {
            paint.setColor(Color.rgb(30,30,30));
        }
    }

    @Override
    public void draw(float tx, float ty, BlockDrawParameters p) {
        RectF r = new RectF(
                (tx + p.getP()) * p.getF(),
                (ty + p.getP()) * p.getF(),
                (tx + p.getBr() - p.getP()) * p.getF(),
                (ty + p.getBr() - p.getP()) * p.getF());
        p.getCanvas().drawRoundRect(r, 5f, 5f, paint);
    }
}
