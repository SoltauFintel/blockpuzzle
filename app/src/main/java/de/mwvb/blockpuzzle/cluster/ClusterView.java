package de.mwvb.blockpuzzle.cluster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import de.mwvb.blockpuzzle.GameState;
import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.gamedefinition.CleanerGameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.persistence.Persistence;
import de.mwvb.blockpuzzle.planet.AbstractPlanet;
import de.mwvb.blockpuzzle.planet.GiantPlanet;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.Moon;

/**
 * Map view of a star cluster
 *
 * Spielfeldgröße in Activity: 1500dp x 1500dp
 */
public class ClusterView extends View {
    /** grid size */
    public static final int w = 40;
    /** for move action */
    private View parent;
    private Button selectTargetButton;
    /** Model */
    private Cluster cluster;
    private Paint planetPaint, giantPlanetPaint, moonPaint, linePaint, quadrantPaint, myPaint, spaceshipPaint;
    /** for move action */
    private final PointF down = new PointF();
    /** for move action */
    private final PointF start = new PointF();
    /** for click action */
    private long startClickTime = 0;
    private Bubble bubble;
    /** for my phone 100 is better, 200 is better for emulators on PC */
    private long clickDurationLimit = 100;

    public ClusterView(Context context) {
        super(context);
        init();
    }
    public ClusterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ClusterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
        initPaints();
        bubble = new Bubble(getResources().getColor(R.color.speechBubbleBackground), getResources().getColor(R.color.target), getResources().getDisplayMetrics().density);
        initTouch();
        if (Build.FINGERPRINT.contains("generic")) { // Is emulator?
            clickDurationLimit = 200;
            System.out.println("RUNS ON EMULATOR");
        }
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
    private void initTouch() {
        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        down.set(event.getX(), event.getY());
                        start.set(getX(), getY());
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        if (bubble.isVisible()) {
                            bubble.hide();
                            draw();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (parent == null) return false;
                        float f = start.x + event.getX() - down.x;
                        float r = f + getWidth();
                        if (r < parent.getWidth()) {
                            f += parent.getWidth() - r;
                        }
                        if (f > 0f) f = 0f;
                        setX(f);
                        f = start.y + event.getY() - down.y;
                        r = f + getHeight();
                        if (r < parent.getHeight()) {
                            f += parent.getHeight() - r;
                        }
                        if (f > 0f) f = 0f;
                        setY(f);
                        start.set(getX(), getY());
                        bubble.setPlanet(null);
                        break;
                    case MotionEvent.ACTION_UP:
                        // https://stackoverflow.com/a/15799372/3478021
                        long now = Calendar.getInstance().getTimeInMillis();
                        long clickDuration = now - startClickTime;
                        if (clickDuration < clickDurationLimit) {
                            click(event.getX(), event.getY());
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void setParent(View parent) {
        this.parent = parent;
    }
    public void setSelectTargetButton(Button selectTargetButton) {
        this.selectTargetButton = selectTargetButton;
    }
    public Cluster getCluster() {
        return cluster;
    }
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
        IPersistence per = GameState.INSTANCE.getPersistence();
        for (IPlanet p0 : cluster.getPlanets()) {
            AbstractPlanet p = (AbstractPlanet) p0;

            per.setGameID(p0, 0);

            int n = p.getGameDefinitions().size();

            p.setInfoText1(getName(p) + p.getNumber());

            if (p.getGameDefinitions() != null && !p.getGameDefinitions().isEmpty()) {
                p.setInfoText2(getFirstGameDefinition(p).getClusterViewInfo());
            } else {
                p.setInfoText2("");
            }

            int score = per.loadScore();
            int moves = per.loadMoves();
            int ownerScore = per.loadOwnerScore();
            int ownerMoves = per.loadOwnerMoves();
            String i3 = "";
            if (n == 1 && getFirstGameDefinition(p).showMoves()) {
                if (moves > 0) {
                    i3 = "Moves: " + moves;
                }
                if (ownerMoves > 0) {
                    i3 = "Moves: " + ownerMoves + " " + per.loadOwnerName();
                }
            } else if (score > 0) {
                i3 = "Score: " + formatScore(score);
                if (ownerScore > 0) {
                    i3 = "Score: " + formatScore(ownerScore) + " " + per.loadOwnerName();
                }
            }
            p.setInfoText3(i3);
        }
    }

    private GameDefinition getFirstGameDefinition(IPlanet planet) {
        return planet.getGameDefinitions().get(0);
    }

    @NotNull
    private String getName(AbstractPlanet p) {
        String name = "Planet #";
        if (p instanceof Moon) {
            name = "Dwarf planet #";
        } else if (p instanceof GiantPlanet) {
            name = "Giant planet #";
        }
        return name;
    }

    private String formatScore(int score) {
        if (score <= 1000) {
            return "" + score;
        }
        return ((int) (score / 1000)) + "k";
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
        canvas.drawText("alpha", getWidth() / 2f - 30 - quadrantPaint.measureText("alpha"), getHeight() / 2f + 70, quadrantPaint);
        canvas.drawText("gamma", getWidth() / 2f - 30 - quadrantPaint.measureText("gamma"), getHeight() / 2f - 30, quadrantPaint);
        canvas.drawText("delta", getWidth() / 2f + 30, getHeight() / 2f - 30, quadrantPaint);
        canvas.drawText("beta", getWidth() / 2f + 30, getHeight() / 2f + 70, quadrantPaint);

        // Planets
        for (IPlanet planet : cluster.getPlanets()) {
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
        float ssx = GameState.INSTANCE.getPlanet().getX();
        float ssy = GameState.INSTANCE.getPlanet().getY();
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
        /* DEBUG mode:
        if (planet.getGameDefinitions().isEmpty()) {
            return giantPlanetPaint;
        }
        if (planet.getSelectedGame() == null) {
            planet.setSelectedGame(planet.getGameDefinitions().get(0));
        }
        if (planet.getSelectedGame() instanceof CleanerGameDefinition) {
            return moonPaint;
        }
        return planetPaint; */

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
        for (IPlanet p : cluster.getPlanets()) {
            int tolerance = p.getRadius() > 20 ? 1 : 0;
            if (Math.abs(p.getX() - kx) <= tolerance && Math.abs(p.getY() - ky) <= tolerance) {
                bubble.setPlanet(p);
                draw();
                return;
            }
        }
        // no planet -> hide bubble
        bubble.hide();
        draw();
    }

    public void selectTarget() {
        GameState.INSTANCE.setPlanet(bubble.getPlanet());
        draw();
    }
}
