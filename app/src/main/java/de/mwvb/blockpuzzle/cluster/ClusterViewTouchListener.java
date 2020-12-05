package de.mwvb.blockpuzzle.cluster;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

/**
 * Move map position and also (de)select planet
 */
public class ClusterViewTouchListener implements View.OnTouchListener {
    /** for move action */
    private final PointF down = new PointF();
    /** for move action */
    private final PointF start = new PointF();
    private final Bubble bubble;
    private final long clickDurationLimit;

    /** for click action */
    private long startClickTime = 0;

    public ClusterViewTouchListener(Bubble bubble) {
        this.bubble = bubble;
        if (Build.FINGERPRINT.contains("generic")) { // Is emulator?
            clickDurationLimit = 200;
            System.out.println("RUNS ON EMULATOR");
        } else {
            clickDurationLimit = 100; // for my phone 100 is better, 200 is better for emulators on PC
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        ClusterView owner = (ClusterView) view;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down(event, owner);
                break;
            case MotionEvent.ACTION_MOVE:
                if (owner.getClusterViewParent() == null) {
                    return false;
                } else {
                    move(event, owner);
                }
                break;
            case MotionEvent.ACTION_UP:
                up(event, owner);
                break;
        }
        return true;
    }

    private void down(MotionEvent event, ClusterView owner) {
        down.set(event.getX(), event.getY());
        start.set(owner.getX(), owner.getY());
        startClickTime = Calendar.getInstance().getTimeInMillis();
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
        // https://stackoverflow.com/a/15799372/3478021
        long now = Calendar.getInstance().getTimeInMillis();
        long clickDuration = now - startClickTime;
        if (clickDuration < clickDurationLimit) {
            owner.click(event.getX(), event.getY());
        }
    }
}
