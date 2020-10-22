package de.mwvb.blockpuzzle.block.special;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.playingfield.QPosition;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.block.IBlockDrawer;

public class StarBlock extends SpecialBock {
    public static final int TYPE = 20;

    public StarBlock() {
        super(TYPE);
    }

    @Override
    protected int getRandomMax() {
        return 250;
    }

    @Override
    public char getBlockTypeChar() {
        return 'S';
    }

    @Override
    public IBlockDrawer getBlockDrawer(View view) {
        final Paint boxPaint = new Paint();
        final Paint linePaint = new Paint();
        linePaint.setStrokeWidth(16);
        linePaint.setColor(view.getResources().getColor(android.R.color.holo_red_dark)); // TODO Farbe definieren!
        boxPaint.setColor(view.getResources().getColor(R.color.starBlock));
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
                canvas.drawLine(left, yLine, right, yLine, linePaint);
                canvas.drawLine(xLine, top, xLine, bottom, linePaint);
            }
        };
    }

    @Override
    public int getColor() {
        return R.color.starBlock;
    }

    @Override
    public int cleared(PlayingField playingField, QPosition k) {
        return 200; // bonus score
    }
}
