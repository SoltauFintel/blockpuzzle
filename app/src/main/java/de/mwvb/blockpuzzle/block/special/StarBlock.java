package de.mwvb.blockpuzzle.block.special;

import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.block.BlockDrawParameters;
import de.mwvb.blockpuzzle.block.IBlockDrawer;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

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
        boxPaint.setColor(view.getResources().getColor(R.color.starBlockForeground));
        final Paint linePaint = new Paint();
        linePaint.setStrokeWidth(16);
        linePaint.setColor(view.getResources().getColor(R.color.starBlockBackground));
        final Paint smallLinePaint = new Paint();
        smallLinePaint.setStrokeWidth(8);
        smallLinePaint.setColor(view.getResources().getColor(R.color.starBlockBackground));
        return new IBlockDrawer() {
            @Override
            public void draw(float x, float y, BlockDrawParameters p) {
                final float left = (x + p.getP()) * p.getF();
                final float top = (y + p.getP()) * p.getF();
                final float right = (x + p.getBr() - p.getP()) * p.getF();
                final float bottom = (y + p.getBr() - p.getP()) * p.getF();
                final float xLine = left + (right - left) / 2;
                final float yLine = top + (bottom - top) / 2;

                p.getCanvas().drawRect(left, top, right, bottom, boxPaint);
                p.getCanvas().drawLine(left, yLine, right, yLine, p.isDragMode() ? linePaint : smallLinePaint);
                p.getCanvas().drawLine(xLine, top, xLine, bottom, p.isDragMode() ? linePaint : smallLinePaint);
            }
        };
    }

    @Override
    public int getColor() {
        return R.color.starBlockForeground;
    }

    @Override
    public int cleared(PlayingField playingField, QPosition k) {
        return 200; // bonus score
    }
}
