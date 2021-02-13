package de.mwvb.blockpuzzle.gamepiece;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.block.BlockDrawParameters;
import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.block.ColorBlockDrawer;
import de.mwvb.blockpuzzle.block.IBlockDrawer;
import de.mwvb.blockpuzzle.game.GameEngineBuilder;
import de.mwvb.blockpuzzle.playingfield.PlayingFieldView;

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
    private final BlockDrawParameters p = new BlockDrawParameters();

    // Zustand
    private GamePiece gamePiece = null;
    /** grey wenn Teil nicht dem Quadrat hinzugefügt werden kann, weil kein Platz ist */
    private boolean grey = false; // braucht nicht zu persistiert werden
    private boolean dragMode = false; // wird nicht persistiert

    // Services
    private final IBlockDrawer greyBD;

    // Paints
    private final Paint p_parking = new Paint();

    public GamePieceView(Context context, int index, boolean parking) {
        super(context);
        this.index = index;
        this.parking = parking;

        initParkingAreaColor();

        blockTypes = new BlockTypes(this);
        greyBD = ColorBlockDrawer.byRColor(this, R.color.colorGrey, R.color.colorGrey, R.color.colorGrey);
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
        p.setCanvas(canvas);
        p.setDragMode(dragMode);
        final float f = getResources().getDisplayMetrics().density;
        p.setF(f);
        int br = PlayingFieldView.w / GameEngineBuilder.blocks; // 60px, auf Handy groß = 36
        if (!dragMode) {
            br /= 2;
        }
        p.setBr(br);
        if (parking && !dragMode) {
            canvas.drawRect(0, 0, br * GamePiece.max * f, br * GamePiece.max * f, p_parking);
        }
        if (gamePiece != null) {
            for (int x = 0; x < GamePiece.max; x++) {
                for (int y = 0; y < GamePiece.max; y++) {
                    int blockType = gamePiece.getBlockType(x, y);
                    if (blockType < 1) {
                        continue;
                    }
                    IBlockDrawer blockDrawer;
                    if (grey) {
                        blockDrawer = greyBD;
                    } else {
                        blockDrawer = blockTypes.getBlockDrawer(blockType);
                    }
                    blockDrawer.draw(x * br, y * br, p);
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

    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean performClick() {
        // because of warning in MainActivity.initClickListener()
        return super.performClick();
    }

    public void onDragEnter() {
        if (gamePiece == null) {
            p_parking.setColor(getResources().getColor(R.color.colorParkingHover));
            draw();
        }
    }

    public void onDragLeave() {
        initParkingAreaColor();
        draw();
    }

    private void initParkingAreaColor() {
        p_parking.setColor(getResources().getColor(R.color.colorParking));
    }
}
