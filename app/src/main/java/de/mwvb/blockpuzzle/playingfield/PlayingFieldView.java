package de.mwvb.blockpuzzle.playingfield;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.block.BlockDrawParameters;
import de.mwvb.blockpuzzle.block.BlockDrawerStrategy;
import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.block.ColorBlockDrawer;
import de.mwvb.blockpuzzle.block.EmptyBlockDrawer;
import de.mwvb.blockpuzzle.block.IBlockDrawer;
import de.mwvb.blockpuzzle.game.GameEngineBuilder;
import de.mwvb.blockpuzzle.sound.SoundService;

/**
 * Das Spielfeld ist ein 10x10 großes Quadrat.
 * Im Spielfeld werden die Spielsteine abgelegt.
 * Ein Kästchen hat die Belegung 0=leer, 1=Block. Angedacht sind weitere Belegungen für Boni.
 * Werte ab 30 haben eine Sonderrolle für die Darstellung.
 * <p>
 * Das Spielfeld ist 300dp groß. Nach unten ist es 2 Reihen (60dp) größer, damit Drag&Drop
 * funktioniert.
 */
public class PlayingFieldView extends View implements IPlayingFieldView {
    public static final int w = 300; // dp
    private PlayingField playingField;
    private final Paint rectborder = new Paint();
    private final Paint rectline = new Paint();
    private final Paint mark = new Paint();
    private final SoundService soundService = new SoundService();
    private FilledRows filledRows;
    private int mode = 0;
    private final IBlockDrawer empty = new EmptyBlockDrawer(this);
    private IBlockDrawer grey;
    private IBlockDrawer bd30;
    private IBlockDrawer bd31;
    private IBlockDrawer bd32;
    private final BlockTypes blockTypes = new BlockTypes(this);
    private final BlockDrawParameters p = new BlockDrawParameters();

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
        soundService.init(context);

        rectborder.setStrokeWidth(3);
        rectborder.setColor(Color.parseColor("#a65726"));
        rectborder.setStyle(Paint.Style.STROKE);

        rectline.setStrokeWidth(1);
        rectline.setColor(rectborder.getColor());

        grey = ColorBlockDrawer.byRColor(this, R.color.colorGrey, R.color.colorGrey_i, R.color.colorGrey_ib);
        bd30 = new ColorBlockDrawer(this, getResources().getColor(R.color.explosion30));
        bd31 = new ColorBlockDrawer(this, getResources().getColor(R.color.explosion31));
        bd32 = new ColorBlockDrawer(this, getResources().getColor(R.color.explosion32));

        mark.setColor(Color.GRAY);
        mark.setStrokeWidth(3);
        mark.setStyle(Paint.Style.STROKE);

        initOnTouch();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOnTouch() {
        final int blocks = getBlocks();
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_UP) {
                    // ähnlicher Code zu MainActivity
                    float f = getResources().getDisplayMetrics().density;
                    float fx = ev.getX() / f; // px -> dp
                    float fy = ev.getY() / f;
                    // jetzt in Spielfeld Koordinaten umrechnen
                    float br = w / blocks;
                    fx /= br;
                    fy /= br;
                    int x = (int) fx;
                    int y = (int) fy;
                    if (y <= 9) {
                        playingField.onTouch(x, y);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void setPlayingField(PlayingField playingField) {
        this.playingField = playingField;
    }

    @Override
    public SoundService getSoundService() {
        return soundService;
    }

    @Override
    public void draw() {
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (playingField == null) {
            return;
        }
        setBackgroundColor(getResources().getColor(R.color.black));
        drawBlocks(canvas);
        super.onDraw(canvas);
    }

    private void drawBlocks(Canvas canvas) {
        final int blocks = getBlocks();
        p.setCanvas(canvas);
        p.setDragMode(true);
        p.setF(getResources().getDisplayMetrics().density);
        p.setBr(w / blocks); // 60px
        final BlockDrawerStrategy m = getMatrixGet();
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                m.get(x, y).draw(x * p.getBr(), y * p.getBr(), p);
            }
        }
    }

    private int getBlocks() {
        return GameEngineBuilder.blocks;
    }

    private BlockDrawerStrategy getMatrixGet() {
        if (playingField.isGameOver()) {
            return (x, y) -> playingField.get(x, y) > 0 ? grey : empty;
        }
        final BlockDrawerStrategy std = getStdMatrixGet();
        if (filledRows != null) { // row ausblenden Modus
            return (x, y) -> {
                if (!filledRows.getExclusions().contains(new QPosition(x, y)) && (filledRows.containsX(x) || filledRows.containsY(y))) {
                    switch (mode) {
                        case 30:
                            return bd30;
                        case 31:
                            return bd31;
                        case 32:
                            return bd32;
                        default:
                            return empty;
                    }
                } else {
                    return std.get(x, y);
                }
            };
        } else {
            return std;
        }
    }

    private BlockDrawerStrategy getStdMatrixGet() {
        return (x, y) -> {
            int b = playingField.get(x, y);
            return b >= 1 ? blockTypes.getBlockDrawer(b) : empty;
        };
    }

    @Override
    public void clearRows(final FilledRows filledRows, final Action action) {
        new RowExplosion().clearRows(filledRows, action, this);
    }

    // called by RowExplosion
    void setFilledRows(FilledRows f) {
        this.filledRows = f;
    }

    // called by RowExplosion
    void drawmode(int mode) {
        drawmode(mode, false, false);
    }

    // called by RowExplosion
    void drawmode(int mode, boolean playClearSound, boolean bigClearSound) {
        this.mode = mode;
        draw();
        if (playClearSound) {
            soundService.clear(bigClearSound);
        }
    }

    @Override
    public void oneColor() {
        soundService.oneColor();
    }

    @Override
    public void gravitation() {
        soundService.firstGravitation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        soundService.destroy();
    }
}
