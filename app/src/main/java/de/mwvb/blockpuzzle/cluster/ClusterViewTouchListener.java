package de.mwvb.blockpuzzle.cluster;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

/**
 * Move map position and also (de)select planet
 *
 * https://stackoverflow.com/a/15799372/3478021
 * https://stackoverflow.com/a/29933115/3478021
 */
public class ClusterViewTouchListener implements View.OnTouchListener {
    private static final int MAX_CLICK_DURATION = 1000;
    private static final int MAX_CLICK_DISTANCE = 15;

    /** for move action */
    private final PointF down = new PointF();
    /** for move action */
    private final PointF start = new PointF();
    private final Bubble bubble;
    private float density;

    private long pressStartTime;
    private boolean stayedWithinClickDistance;

    public ClusterViewTouchListener(Bubble bubble, float density) {
        this.bubble = bubble;
        this.density = density;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        ClusterView owner = (ClusterView) view;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down(event, owner);
                // for click:
                pressStartTime = System.currentTimeMillis();
                stayedWithinClickDistance = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (owner.getClusterViewParent() == null) {
                    return false;
                } else {
                    move(event, owner);
                    // for click:
                    if (stayedWithinClickDistance && distance(down.x, down.y, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                        stayedWithinClickDistance = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // for click:
                up(event, owner);
                break;
        }
        return true;
    }

    private void down(MotionEvent event, ClusterView owner) {
        down.set(event.getX(), event.getY()); // also for click
        start.set(owner.getX(), owner.getY());
        if (bubble.isVisible()) {
            bubble.hide();
            owner.draw();
        }
    }

    private void move(MotionEvent event, ClusterView owner) {
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

    private void up(MotionEvent event, ClusterView owner) {
        long pressDuration = System.currentTimeMillis() - pressStartTime;
        if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
            // Click event has occurred
            owner.click(event.getX(), event.getY());
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return distanceInPx / density; // px to dp
    }
}
