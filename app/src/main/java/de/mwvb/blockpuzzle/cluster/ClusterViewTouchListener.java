package de.mwvb.blockpuzzle.cluster;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import de.mwvb.blockpuzzle.gamepiece.AbstractBPTouchListener;

/**
 * Move map position and also (de)select planet
 *
 * https://stackoverflow.com/a/15799372/3478021
 * https://stackoverflow.com/a/29933115/3478021
 */
public class ClusterViewTouchListener extends AbstractBPTouchListener {
    private static final int MAX_CLICK_DURATION = 1000;
    /** for move action */
    private final PointF start = new PointF();
    private final Bubble bubble;

    private long pressStartTime;
    private boolean stayedWithinClickDistance;

    public ClusterViewTouchListener(Bubble bubble, float density) {
        super(density);
        this.bubble = bubble;
    }

    @Override
    protected void down(View view, MotionEvent event) {
        super.down(view, event);
        start.set(view.getX(), view.getY());
        if (bubble.isVisible()) {
            bubble.hide();
            ((ClusterView) view).draw();
        }
        // for click:
        pressStartTime = System.currentTimeMillis();
        stayedWithinClickDistance = true;
    }

    @Override
    protected boolean move(View view, MotionEvent event) {
        if (((ClusterView) view).getClusterViewParent() == null) {
            return false;
        } else {
            moveMap(event, (ClusterView) view);
            // for click:
            if (stayedWithinClickDistance && distance(down.x, down.y, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                stayedWithinClickDistance = false;
            }
            return true;
        }
    }

    private void moveMap(MotionEvent event, ClusterView owner) {
        View parent = owner.getClusterViewParent();

        float newX = start.x + event.getX() - down.x;
        float right = newX + owner.getWidth();
        if (right < parent.getWidth()) {
            newX += parent.getWidth() - right;
        }
        if (newX > 0f) {
            newX = 0f;
        }
        owner.setX(newX);

        float newY = start.y + event.getY() - down.y;
        float bottom = newY + owner.getHeight();
        if (bottom < parent.getHeight()) {
            newY += parent.getHeight() - bottom;
        }
        if (newY > 0f) {
            newY = 0f;
        }
        owner.setY(newY);

        start.set(owner.getX(), owner.getY());
        bubble.setPlanet(null);
    }

    @Override
    protected void up(View view, MotionEvent event) {
        long pressDuration = System.currentTimeMillis() - pressStartTime;
        if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
            // Click event has occurred
            ((ClusterView) view).click(event.getX(), event.getY());
        }
    }
}
