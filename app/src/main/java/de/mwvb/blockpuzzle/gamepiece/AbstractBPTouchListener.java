package de.mwvb.blockpuzzle.gamepiece;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class AbstractBPTouchListener implements View.OnTouchListener {
    protected static final int MAX_CLICK_DISTANCE = 15;
    protected final PointF down = new PointF();
    private final float density;

    public AbstractBPTouchListener(float density) {
        this.density = density;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down(view, event);
                return true;
            case MotionEvent.ACTION_MOVE:
                try {
                    return move(view, event);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "FT: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return true;
                }
            case MotionEvent.ACTION_UP:
                up(view, event);
                return true;
            default:
                return false;
        }
    }

    protected void down(View view, MotionEvent event) {
        down.set(event.getX(), event.getY()); // also for click in ClusterViewTouchListener
    }

    protected abstract boolean move(View view, MotionEvent event);

    protected abstract void up(View view, MotionEvent event);

    protected final float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return distanceInPx / density; // px to dp
    }
}
