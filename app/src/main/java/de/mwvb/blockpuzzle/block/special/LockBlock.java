package de.mwvb.blockpuzzle.block.special;

import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.block.BlockDrawParameters;
import de.mwvb.blockpuzzle.block.IBlockDrawer;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

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
        boxPaint.setColor(view.getResources().getColor(android.R.color.black));
        final Paint linePaint = new Paint();
        linePaint.setStrokeWidth(16);
        linePaint.setColor(view.getResources().getColor(R.color.lockBlock));
        final Paint smallLinePaint = new Paint();
        smallLinePaint.setStrokeWidth(8);
        smallLinePaint.setColor(view.getResources().getColor(R.color.lockBlock));
        return new IBlockDrawer() {
            @Override
            public void draw(float x, float y, BlockDrawParameters p) {
                final float left = (x + p.getP()) * p.getF();
                final float top = (y + p.getP()) * p.getF();
                final float right = (x + p.getBr() - p.getP()) * p.getF();
                final float bottom = (y + p.getBr() - p.getP()) * p.getF();

                p.getCanvas().drawRect(left, top, right, bottom, boxPaint);
                p.getCanvas().drawLine(left, top, right, bottom, p.isDragMode() ? linePaint : smallLinePaint);
                p.getCanvas().drawLine(left, bottom, right, top, p.isDragMode() ? linePaint : smallLinePaint);
            }
        };
    }

    @Override
    public int cleared(PlayingField playingField, QPosition k) {
        playingField.set(k.getX(), k.getY(), 1);
        return 1;
    }
}
