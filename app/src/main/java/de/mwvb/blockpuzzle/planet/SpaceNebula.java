package de.mwvb.blockpuzzle.planet;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mwvb.blockpuzzle.cluster.ClusterView;
import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Weltraumnebel
 */
public class SpaceNebula extends AbstractSpaceObject {
    private final List<QPosition> dots = new ArrayList<>();
    public static Paint paint;

    public SpaceNebula(int number, int soX, int soY) {
        super(number, soX, soY);

        // Generate static nebula
        final Random random = new Random();
        final int w = ClusterView.w * 6;
        final int h = ClusterView.w * 2;
        for (int i = 0; i < 100; i++) {
            dots.add(new QPosition(random.nextInt(w), random.nextInt(h)));
        }
    }

    @Override
    public int getRadius() {
        return 20;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isDataExchangeRelevant() {
        return false;
    }

    @Override
    public void draw(Canvas canvas, float f) {
        for (QPosition dot : dots) {
            canvas.drawCircle(getX() * ClusterView.w * f + dot.getX(), getY() * ClusterView.w * f + dot.getY(), 3, paint);
        }
    }

    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public void setOwner(boolean v) { //
    }
}
