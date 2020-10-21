package de.mwvb.blockpuzzle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.logic.Game;
import de.mwvb.blockpuzzle.logic.Persistence;
import de.mwvb.blockpuzzle.logic.spielstein.BlockTypes;
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;

/**
 * Im unteren Bereich die View Komponente, die ein Spielstein (oder einen leeren Spielstein) enthält.
 * Aus der TeilView erfolgt die Drag-and-Drop Operation.
 * Die 4. TeilView ist das Parking Area zum vorübergehenden Ablegen eines Teil.
 * Teil ist der alte Name für Spielstein; daher TeilView.
 */
@SuppressLint("ViewConstructor")
public class GamePieceView extends View {
    private final int index;
    private final boolean parking;
    private final Persistence persistence;
    private final Paint p_parking = new Paint();
    private final BlockTypes blockTypes;
    private final IBlockDrawer greyBD;
    private final IBlockDrawer drehmodusBD;
    private GamePiece gamePiece = null;
    /**
     * grey wenn Teil nicht dem Quadrat hinzugefügt werden kann, weil kein Platz ist
     */
    private boolean grey = false; // braucht nicht zu persistiert werden
    private boolean drehmodus = false; // wird nicht persistiert
    private boolean dragMode = false; // wird nicht persistiert

    public GamePieceView(Context context, int index, boolean parking, Persistence persistence) {
        super(context);
        this.index = index;
        this.parking = parking;
        this.persistence = persistence;

        p_parking.setColor(getResources().getColor(R.color.colorParking));

        blockTypes = new BlockTypes(this);
        greyBD = ColorBlockDrawer.byRColor(this, R.color.colorGrey);
        drehmodusBD = ColorBlockDrawer.byRColor(this, R.color.colorDrehmodus);
    }

    public void setGamePiece(GamePiece v) {
        gamePiece = v;
        draw();
    }

    public GamePiece getGamePiece() {
        return gamePiece;
    }

    public int getIndex() {
        return index;
    }

    // Methode nicht löschen! Die wird als isGrey in MainActivty verwendet.
    public void setGrey(boolean v) {
        grey = v;
    }

    // Methode nicht löschen!
    public boolean isGrey() {
        return grey;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float f = getResources().getDisplayMetrics().density;
        int br = PlayingFieldView.w / Game.blocks; // 60px, auf Handy groß = 36
        if (!dragMode) {
            br /= 2;
        }
        float p = br * 0.1f;
        if (parking && !dragMode) {
            canvas.drawRect(0, 0, br * GamePiece.max * f, br * GamePiece.max * f, p_parking);
        }
        if (gamePiece != null) {
            // TODO Ist das doppelter Code zu SpielfeldView?
            for (int x = 0; x < GamePiece.max; x++) {
                for (int y = 0; y < GamePiece.max; y++) {
                    int blockType = gamePiece.getBlockType(x, y);
                    if (blockType >= 1) {
                        IBlockDrawer blockDrawer;
                        if (grey) {
                            blockDrawer = greyBD;
                        } else if (drehmodus) {
                            blockDrawer = drehmodusBD;
                        } else {
                            blockDrawer = blockTypes.getBlockDrawer(blockType);
                        }
                        blockDrawer.draw(canvas, x * br, y * br, p, br, f);
                    }
                }
            }
        }
        super.onDraw(canvas);
    }

    public void draw() {
        invalidate();
        requestLayout();
    }

    public void startDragMode() {
        dragMode = true;
        setVisibility(View.INVISIBLE);
    }

    public void endDragMode() {
        dragMode = false;
        setVisibility(View.VISIBLE);
    }

    public void setDrehmodus(boolean d) {
        drehmodus = d;
        draw();
    }

    public void rotate() {
        if (gamePiece != null) {
            gamePiece.rotateToRight();
            draw();
            write();
        }
    }

    @Override
    public boolean performClick() {
        // wegen Warning in MainActivity.initClickListener()
        return super.performClick();
    }

    public void write() {
        persistence.save(this);
    }

    public void read() {
        persistence.load(this);
        draw();
    }
}
