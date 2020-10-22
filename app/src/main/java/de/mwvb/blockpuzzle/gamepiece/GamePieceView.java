package de.mwvb.blockpuzzle.gamepiece;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.Game;
import de.mwvb.blockpuzzle.playingfield.PlayingFieldView;
import de.mwvb.blockpuzzle.block.ColorBlockDrawer;
import de.mwvb.blockpuzzle.block.IBlockDrawer;

/**
 * Im unteren Bereich die View Komponente, die ein Spielstein (oder einen leeren Spielstein) enthält.
 * Aus der TeilView erfolgt die Drag-and-Drop Operation.
 * Die 4. TeilView ist das Parking Area zum vorübergehenden Ablegen eines Teil.
 * Teil ist der alte Name für Spielstein; daher TeilView.
 */
@SuppressLint("ViewConstructor")
public class GamePieceView extends View implements IGamePieceView {
    // Stammdaten
    private final int index;
    private final boolean parking;
    private final BlockTypes blockTypes;

    // Zustand    TODO den will ich hier loswerden
    private GamePiece gamePiece = null;
    /** grey wenn Teil nicht dem Quadrat hinzugefügt werden kann, weil kein Platz ist */
    private boolean grey = false; // braucht nicht zu persistiert werden
    private boolean drehmodus = false; // wird nicht persistiert
    private boolean dragMode = false; // wird nicht persistiert

    // Services
    private final IBlockDrawer greyBD;
    private final IBlockDrawer drehmodusBD;

    // Paints
    private final Paint p_parking = new Paint();

    public GamePieceView(Context context, int index, boolean parking) {
        super(context);
        this.index = index;
        this.parking = parking;

        p_parking.setColor(getResources().getColor(R.color.colorParking));

        blockTypes = new BlockTypes(this);
        greyBD = ColorBlockDrawer.byRColor(this, R.color.colorGrey);
        drehmodusBD = ColorBlockDrawer.byRColor(this, R.color.colorDrehmodus);
    }

    @Override
    public void setGamePiece(GamePiece v) {
        endDragMode();
        grey = false;
        gamePiece = v;
        draw();
    }

    @Override
    public GamePiece getGamePiece() {
        return gamePiece;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setGrey(boolean v) {
        grey = v;
        draw();
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

    @Override
    public void draw() {
        invalidate();
        requestLayout();
    }

    @Override
    public void startDragMode() {
        dragMode = true;
        setVisibility(View.INVISIBLE);
    }

    @Override
    public void endDragMode() {
        dragMode = false;
        setVisibility(View.VISIBLE);
    }

    @Override
    public void setDrehmodus(boolean d) {
        drehmodus = d;
        draw();
    }

    @Override
    public boolean performClick() {
        // wegen Warning in MainActivity.initClickListener()
        return super.performClick();
    }
}
