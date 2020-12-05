package de.mwvb.blockpuzzle.cluster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.planet.GiantPlanet;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.Moon;
import de.mwvb.blockpuzzle.sound.SoundService;

/**
 * Map view of a star cluster
 *
 * Spielfeldgröße in Activity: 1500dp x 1500dp
 */
public class ClusterView extends View {
    private final SoundService soundService = new SoundService();
    /** Model */
    private ClusterViewModel model;
    /** for move action */
    private View clusterViewParent;
    private Button selectTargetButton;
    /** grid size */
    public static final int w = 40;
    private Paint planetPaint, giantPlanetPaint, moonPaint, linePaint, quadrantPaint, myPaint, spaceshipPaint;
    private Bubble bubble;

    public ClusterView(Context context) {
        super(context);
        init();
        soundService.init(getContext());
    }
    public ClusterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        soundService.init(getContext());
    }
    public ClusterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        soundService.init(getContext());
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
        initPaints();
        bubble = new Bubble(getResources().getColor(R.color.speechBubbleBackground), getResources().getColor(R.color.target), getResources().getDisplayMetrics().density);
        setOnTouchListener(new ClusterViewTouchListener(bubble, getResources().getDisplayMetrics().density));
    }
    private void initPaints() {
        moonPaint = new Paint();
        moonPaint.setColor(getResources().getColor(R.color.moon));
        moonPaint.setAntiAlias(true);
        planetPaint = new Paint();
        planetPaint.setColor(getResources().getColor(R.color.planet));
        planetPaint.setAntiAlias(true);
        giantPlanetPaint = new Paint();
        giantPlanetPaint.setColor(getResources().getColor(R.color.giantPlanet));
        giantPlanetPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.quadrant));
        linePaint.setStrokeWidth(4f);
        quadrantPaint = new Paint();
        quadrantPaint.setColor(getResources().getColor(R.color.quadrant));
        quadrantPaint.setTextSize(60f);
        myPaint = new Paint();
        myPaint.setColor(getResources().getColor(R.color.myPlanet));
        myPaint.setStrokeWidth(6f);
        spaceshipPaint = new Paint();
        spaceshipPaint.setColor(getResources().getColor(R.color.spaceship));
    }

    public void setClusterViewParent(View parent) {
        this.clusterViewParent = parent;
    }
    public View getClusterViewParent() {
        return clusterViewParent;
    }
    public void setSelectTargetButton(Button selectTargetButton) {
        this.selectTargetButton = selectTargetButton;
    }
    public void setModel(ClusterViewModel model) {
        this.model = model;
        bubble.setModel(model);
    }
    public ClusterViewModel getModel() {
        return model;
    }

    public void draw() {
        invalidate();
        requestLayout();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        final float f = getResources().getDisplayMetrics().density; // dp -> px factor

        // Quadrant lines
        canvas.drawLine(getWidth() / 2f, 0, getWidth() / 2f, getHeight(), linePaint);
        canvas.drawLine(0, getHeight() / 2f, getWidth(), getHeight() / 2f, linePaint);

        // Quadrant texts
        String alpha = getResources().getString(R.string.alpha);
        String gamma = getResources().getString(R.string.gamma);
        canvas.drawText(alpha, getWidth() / 2f - 30 - quadrantPaint.measureText(alpha), getHeight() / 2f + 70, quadrantPaint);
        canvas.drawText(gamma, getWidth() / 2f - 30 - quadrantPaint.measureText(gamma), getHeight() / 2f - 30, quadrantPaint);
        canvas.drawText(getResources().getString(R.string.delta), getWidth() / 2f + 30, getHeight() / 2f - 30, quadrantPaint);
        canvas.drawText(getResources().getString(R.string.beta), getWidth() / 2f + 30, getHeight() / 2f + 70, quadrantPaint);

        // Planets
        for (IPlanet planet : model.getPlanets()) {
            if (planet.isVisibleOnMap()) {
                float xx = planet.getX() * w * f;
                float yy = planet.getY() * w * f;
                float rr = planet.getRadius() * f;
                drawPlanet(canvas, xx, yy, rr, getPlanetPaint(planet));
                if (planet.isOwner()) {
                    drawOwnerMark(planet, canvas, f);
                }
            }
        }

        // aktuelle Raumschiffposition (erstmal nur ein Kreis, später ein Symbol oder ein Kreuz)
        float ssx = model.getCurrentPlanet().getX();
        float ssy = model.getCurrentPlanet().getY();
        ssx -= 0.3f;
        ssy += 0.3f;
        canvas.drawCircle(ssx * w * f, ssy * w * f, 5 * f, spaceshipPaint);

        // Speech bubble
        bubble.draw(canvas, selectTargetButton);
    }
    private void drawPlanet(Canvas canvas, float x, float y, float r, Paint paint) {
        canvas.drawCircle(x, y, r, paint);
    }
    private void drawOwnerMark(IPlanet planet, Canvas canvas, float f) {
        float ax = planet.getX() * w * f + planet.getRadius() * (planet instanceof Moon ? 1.33f : 1f) * f;
        float ay = planet.getY() * w * f - planet.getRadius() * .7f * f;
        float bx = ax + 5 * f;
        float by = ay + 5 * f;
        float cx = bx + 5 * f;
        float cy = ay - 10 * f;
        canvas.drawLine(ax, ay, bx, by, myPaint);
        canvas.drawLine(bx, by, cx, cy, myPaint);
    }
    private Paint getPlanetPaint(IPlanet planet) {
        Paint p;
        if (planet instanceof GiantPlanet) {
            p = giantPlanetPaint;
        } else if (planet instanceof Moon) {
            p = moonPaint;
        } else {
            p = planetPaint;
        }
        return p;
    }

    public void click(float ix, float iy) {
        final float wf = getResources().getDisplayMetrics().density * w;
        int kx = (int) (ix / wf + .5f);
        int ky = (int) (iy / wf + .5f);
        for (IPlanet p : model.getPlanets()) {
            int tolerance = p.getRadius() > 20 ? 1 : 0;
            if (Math.abs(p.getX() - kx) <= tolerance && Math.abs(p.getY() - ky) <= tolerance) {
                bubble.setPlanet(p);
                draw();
                return;
            }
        }
        // clicked on space (=no planet) -> hide bubble
        bubble.hide();
        draw();
    }

    public void selectTarget() {
        IPlanet planet = bubble.getPlanet();
        if (planet != null) {
            model.setCurrentPlanet(planet);
            soundService.targetSelected(); // Piepton als Bestätigung
        }
        draw();
    }
}
