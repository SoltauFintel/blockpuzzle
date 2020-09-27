package de.mwvb.blockpuzzle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import de.mwvb.blockpuzzle.logic.Action;
import de.mwvb.blockpuzzle.logic.FilledRows;
import de.mwvb.blockpuzzle.logic.Game;
import de.mwvb.blockpuzzle.music.Music;

/**
 * Das Spielfeld ist ein 10x10 großes Quadrat.
 * Im Spielfeld werden die Spielsteine abgelegt.
 * Ein Kästchen hat die Belegung 0=leer, 1=Block. Angedacht sind weitere Belegungen für Boni.
 * Werte ab 30 haben eine Sonderrolle für die Darstellung.
 *
 * Das Spielfeld ist 300dp groß. Nach unten ist es 2 Reihen (60dp) größer, damit Drag&Drop
 * funktioniert.
 */
public class PlayingFieldView extends View {
    public static final int w = 300; // dp
    private final Paint rectborder = new Paint();
    private final Paint rectline = new Paint();
    private final Paint box0 = new Paint();
    private final Paint box1 = new Paint();
    private final Paint box1dead = new Paint();
    private final Paint box30 = new Paint();
    private final Paint box31 = new Paint();
    private final Paint box32 = new Paint();
    private final Paint mark = new Paint();
    private final Music music = new Music();
    private Game game;
    private FilledRows filledRows;
    private int mode = 0;

    public PlayingFieldView(Context context) {
        super(context);
        init(context);
    }

    public PlayingFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayingFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /* ??? - API level problem
    public SpielfeldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }*/

    private void init(Context context) {
        music.init(context);

        rectborder.setStrokeWidth(3);
        rectborder.setColor(Color.parseColor("#a65726"));
        rectborder.setStyle(Paint.Style.STROKE);

        rectline.setStrokeWidth(1);
        rectline.setColor(rectborder.getColor());

        box0.setColor(Color.WHITE);
        box1.setColor(Color.parseColor("#a65726"));
        box1dead.setColor(Color.parseColor("#888888"));

        box30.setColor(Color.parseColor("#ffdd00"));
        box31.setColor(Color.parseColor("#ff0000"));
        box32.setColor(Color.parseColor("#bbbbbb"));

        mark.setColor(Color.GRAY);
        mark.setStrokeWidth(3);
        mark.setStyle(Paint.Style.STROKE);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void draw() {
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPlayingField(canvas);
        drawBlocks(canvas);
        super.onDraw(canvas);
    }

    private void drawPlayingField(Canvas canvas) {
        final float f = getResources().getDisplayMetrics().density;
        // Rahmen
        canvas.drawRect(1 * f, 1 * f, w * f, w * f, rectborder);

        // Gitterlinien
        final int br = w / Game.blocks; // 60px, auf Handy groß = 36
        for (int i = 1; i < Game.blocks; i++) {
            float t = i * br;
            canvas.drawLine(t * f, 0, t * f, w * f, rectline);
            canvas.drawLine(1 * f, t * f, w * f, t * f, rectline);
        }
    }

    private void drawBlocks(Canvas canvas) {
        final float f = getResources().getDisplayMetrics().density;
        final int br = w / Game.blocks; // 60px
        final float p = br * 0.1f;
        final MatrixGet m = getMatrixGet();
        for (int x = 0; x < Game.blocks; x++) {
            for (int y = 0; y < Game.blocks; y++) {
                float tx = x * br;
                float ty = y * br;
                canvas.drawRect((tx + p) * f, (ty + p) * f,
                        (tx + br - p) * f, (ty + br - p) * f,
                        m.get(x, y));
            }
        }
    }

    private MatrixGet getMatrixGet() {
        if (game.isGameOver()) {
            return new MatrixGet() {
                @Override
                public Paint get(int x, int y) {
                    return game.get(x, y) == 1 ? box1dead : box0;
                }
            };
        } else if (filledRows != null) { // row ausblenden Modus
            final MatrixGet std = getStdMatrixGet();
            return new MatrixGet() {
                @Override
                public Paint get(int x, int y) {
                    if (filledRows.containsX(x) || filledRows.containsY(y)) {
                        switch (mode) {
                            case 30: return box30;
                            case 31: return box31;
                            case 32: return box32;
                            default: return box0;
                        }
                    } else {
                        return std.get(x, y);
                    }
                }
            };
        } else {
            return getStdMatrixGet();
        }
    }

    private MatrixGet getStdMatrixGet() {
        return new MatrixGet() {
            @Override
            public Paint get(int x, int y) {
                // später werden hier noch weitere Typen unterstützt
                return game.get(x, y) == 1 ? box1 : box0;
            }
        };
    }

    // TODO vielleicht eine Klasse daraus machen, evtl. kann man's auch kompakter schreiben
    public void clearRows(final FilledRows filledRows, Action action) {
        if (filledRows.getHits() == 0) {
            return;
        }
        this.filledRows = filledRows;
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                mode = 30;
                draw();
                music.playCrunchSound();
            }
        }, 50);

        handler.postDelayed(new Runnable() {
            public void run() {
                mode = 31;
                draw();
            }
        }, 200);

        handler.postDelayed(new Runnable() {
            public void run() {
                mode = 32;
                draw();
            }
        }, 350);

        handler.postDelayed(new Runnable() {
            public void run() {
                mode = 0;
                draw();
                PlayingFieldView.this.filledRows = null;
                action.execute();
            }
        }, 500);
    }

    public void playGameOverSound() {
        music.playCrunchSound();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                music.playCrunchSound();
            }
        }, 450);
    }

    public void playCrunchSound() {
        music.playCrunchSound();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        music.destroy();
    }
}
