package de.mwvb.blockpuzzle.cluster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;
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
    private Paint linePaint, line2Paint, quadrantPaint, spaceshipPaint;
    private SpaceObjectPaints spaceObjectPaints;
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
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.quadrant));
        linePaint.setStrokeWidth(4f);
        line2Paint = new Paint();
        line2Paint.setColor(getResources().getColor(R.color.colorGrey));
        line2Paint.setStrokeWidth(2f);
        line2Paint.setPathEffect(new DashPathEffect(new float[] {4f, 27f}, 0f));
        quadrantPaint = new Paint();
        quadrantPaint.setColor(getResources().getColor(R.color.quadrant));
        quadrantPaint.setTextSize(60f);
        spaceshipPaint = new Paint();
        spaceshipPaint.setColor(getResources().getColor(R.color.spaceship));

        spaceObjectPaints = new SpaceObjectPaints(getContext());
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

        // weitere Rasterlinien
        for (int rx = 1; rx < 35; rx += 5) {
            float k = rx * w * f;
            canvas.drawLine(k, 0, k, getHeight(), line2Paint);
        }
        for (int ry = 1; ry < 35; ry += 5) {
            float k = ry * w * f;
            canvas.drawLine(0, k, getWidth(), k, line2Paint);
        }

        // Quadrant texts
        String alpha = getResources().getString(R.string.alpha);
        String gamma = getResources().getString(R.string.gamma);
        canvas.drawText(alpha, getWidth() / 2f - 30 - quadrantPaint.measureText(alpha), getHeight() / 2f + 70, quadrantPaint);
        canvas.drawText(gamma, getWidth() / 2f - 30 - quadrantPaint.measureText(gamma), getHeight() / 2f - 30, quadrantPaint);
        canvas.drawText(getResources().getString(R.string.delta), getWidth() / 2f + 30, getHeight() / 2f - 30, quadrantPaint);
        canvas.drawText(getResources().getString(R.string.beta), getWidth() / 2f + 30, getHeight() / 2f + 70, quadrantPaint);

        // Planets
        spaceObjectPaints.prepare();
        for (ISpaceObject spaceObject : model.getSpaceObjects()) {
            if (model.getInfo().isVisibleOnMap(spaceObject)) {
                spaceObject.draw(canvas, f, model.getInfo());
            }
        }
        spaceObjectPaints.cleanup();

        // aktuelle Raumschiffposition (erstmal nur ein Kreis, später ein Symbol oder ein Kreuz)
        float ssx = model.getCurrentPlanet().getX();
        float ssy = model.getCurrentPlanet().getY();
        ssx -= 0.3f;
        ssy += 0.3f;
        canvas.drawCircle(ssx * w * f, ssy * w * f, 5 * f, spaceshipPaint);

        // Speech bubble
        bubble.draw(canvas, selectTargetButton);
    }

    public void click(float ix, float iy) {
        final float wf = getResources().getDisplayMetrics().density * w;
        int kx = (int) (ix / wf + .5f);
        int ky = (int) (iy / wf + .5f);
        for (ISpaceObject p : model.getSpaceObjects()) {
            if (p.isSelectable() && p instanceof IPlanet) {
                int tolerance = p.getRadius() > 20 ? 1 : 0;
                if (Math.abs(p.getX() - kx) <= tolerance && Math.abs(p.getY() - ky) <= tolerance) {
                    bubble.setPlanet((IPlanet) p);
                    draw();
                    return;
                }
            }
        }
        // clicked on space (=no planet) -> hide bubble
        bubble.hide();
        draw();
    }

    public void selectTarget() {
        IPlanet planet = bubble.getPlanet();
        if (planet != null) {
            if (model.getCurrentPlanet() == null || model.getRoute(model.getCurrentPlanet().getNumber(), planet.getNumber()).travel()) {
                model.setCurrentPlanet(planet);
                soundService.targetSelected(); // Piepton als Bestätigung
            }
        }
        draw();
    }
}
