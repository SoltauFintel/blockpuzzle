package de.mwvb.blockpuzzle.logic.spielstein.special;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.logic.PlayingField;
import de.mwvb.blockpuzzle.entity.QPosition;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.view.IBlockDrawer;

public class LockBlock extends SpecialBock {
    public static final int TYPE = 21;
    private int c = 0;

    public LockBlock() {
        super(TYPE);
    }

    @Override
    public boolean isRelevant(GamePiece p) {
        final int n = 100;
        if (++c > n) {
            c = 0;
        }
        return c == n;
    }

    @Override
    protected int getRandomMax() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char getBlockTypeChar() {
        return 'L';
    }

    @Override
    public int getColor() {
        return R.color.lockBlock;
    }

    @Override
    public IBlockDrawer getBlockDrawer(View view) {
        final Paint boxPaint = new Paint();
        final Paint linePaint = new Paint();
        linePaint.setStrokeWidth(16);
        linePaint.setColor(view.getResources().getColor(R.color.lockBlock));
        boxPaint.setColor(view.getResources().getColor(android.R.color.white)); // TODO Farbe definieren!
        return new IBlockDrawer() {
            @Override
            public void draw(Canvas canvas, float tx, float ty, float p, int br, float f) {
                final float left = (tx + p) * f;
                final float top = (ty + p) * f;
                final float right = (tx + br - p) * f;
                final float bottom = (ty + br - p) * f;
                final float xLine = left + (right - left) / 2;
                final float yLine = top + (bottom - top) / 2;

                canvas.drawRect(left, top, right, bottom, boxPaint);
                canvas.drawLine(left, top, right, bottom, linePaint);
                canvas.drawLine(left, bottom, right, top, linePaint);
            }
        };
    }

    @Override
    public int cleared(PlayingField playingField, QPosition k) {
        playingField.set(k.getX(), k.getY(), 1);
        return 1;
    }
}
