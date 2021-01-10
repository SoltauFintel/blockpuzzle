package de.mwvb.blockpuzzle.gamepiece;

import android.content.ClipData;
import android.content.res.Resources;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import de.mwvb.blockpuzzle.game.MyDragShadowBuilder;

/**
 * The user can drag the game piece to the playing field or parking area.
 * The user can also tap the game piece once to rotate it.
 */
public abstract class GamePieceTouchListener extends AbstractBPTouchListener {
    private final int index;
    private final Resources resources;

    public GamePieceTouchListener(int index, Resources resources) {
        super(resources.getDisplayMetrics().density);
        this.index = index;
        this.resources = resources;
    }

    @Override
    protected boolean move(View view, MotionEvent event) {
        // This IF(distance) prevents showing the drag image while player tries to rotate. (player taps too long)
        if (isDragAllowed() && distance(down.x, down.y, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
            ClipData data = ClipData.newPlainText("index", "" + index);
            GamePieceView tv = (GamePieceView) view;
            if (tv.getGamePiece() != null) {
                tv.startDragMode();
                MyDragShadowBuilder dragShadowBuilder = new MyDragShadowBuilder(tv, resources.getDisplayMetrics().density);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0 Nougat API level 24
                    view.startDragAndDrop(data, dragShadowBuilder, view, 0);
                } else { // for API level 19 (4.4. Kitkat)
                    view.startDrag(data, dragShadowBuilder, view, 0);
                }
            }
            return true;
        }
        return false;
    }

    protected abstract boolean isDragAllowed();
}
