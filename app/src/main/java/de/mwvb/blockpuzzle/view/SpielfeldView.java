package de.mwvb.blockpuzzle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.logic.FilledRows;
import de.mwvb.blockpuzzle.logic.Game;
import de.mwvb.blockpuzzle.musik.Musik;

/**
 * Quadrat 10x10
 * Im Quadrat werden die Teile abgelegt.
 * Ein Kästchen hat die Belegung 0=leer, 1=Block. Angedacht sind weitere Belegungen für Boni.
 */
public class SpielfeldView extends View {
    private final Paint rectborder = new Paint();
    private final Paint rectline = new Paint();
    private final Paint box0 = new Paint();
    private final Paint box1 = new Paint();
    private final Paint box1dead = new Paint();
    private final Paint box30 = new Paint();
    private final Paint box31 = new Paint();
    private final Paint box32 = new Paint();
    private final Paint p_mark = new Paint();
    private final Musik musik = new Musik();
    private Game game;
    private int w;
    private int h;
    private FilledRows filledRows;
    private int mode = 0;

    public SpielfeldView(Context context) {
        super(context);
        init(context);
    }

    public SpielfeldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpielfeldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SpielfeldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        musik.init(context);

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

        p_mark.setColor(Color.GRAY);
        p_mark.setStrokeWidth(3);
        p_mark.setStyle(Paint.Style.STROKE);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    /** just for getting size */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w; // 600px
        this.h = h - 1 - 120; // 120 für Drag-Drop-Bereich am unteren Rand, da Drop-Cursor 120 tiefer
    }

    public void draw() {
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSpielfeld(canvas);
        drawKaestchen(canvas);
        super.onDraw(canvas);
    }

    private void drawSpielfeld(Canvas canvas) {
        // Rahmen
        canvas.drawRect(1, 1, w, h, rectborder);

        // Gitterlinien
        final int br = w / Game.blocks; // 60px
        for (int i = 1; i < Game.blocks; i++) {
            float t = i * br;
            canvas.drawLine(t, 0,t, h, rectline);
            canvas.drawLine(1, t, w, t, rectline);
        }
    }

    private void drawKaestchen(Canvas canvas) {
        final int br = w / Game.blocks; // 60px
        final float p = br * 0.1f;
        final MatrixGet m = getMatrixGet();
        for (int x = 0; x < Game.blocks; x++) {
            for (int y = 0; y < Game.blocks; y++) {
                float tx = x * br;
                float ty = y * br;
                canvas.drawRect(tx + p, ty + p,
                        tx + br - p, ty + br - p,
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
                    if (filledRows.enthaltenX(x) || filledRows.enthaltenY(y)) {
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

    public void clearRows(final FilledRows filledRows) {
        if (filledRows.getTreffer() == 0) {
            return;
        }
        this.filledRows = filledRows;
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                mode = 30;
                draw();
                musik.playCrunchSound();
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
                SpielfeldView.this.filledRows = null;
            }
        }, 500);
    }

    public void playGameOverSound() {
        musik.playCrunchSound();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                musik.playCrunchSound();
            }
        }, 450);
    }
}
