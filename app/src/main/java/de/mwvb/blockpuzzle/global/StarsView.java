package de.mwvb.blockpuzzle.global;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Random;

public class StarsView extends View {
    private final Random r = new Random(System.currentTimeMillis());
    private Paint paint;

    public StarsView(Context context) {
        super(context);
        init();
    }

    public StarsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StarsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StarsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth();
        final int h = getHeight();

        for (int i = 0; i < 100; i++) {
            int x = r.nextInt(w);
            int y = r.nextInt(h);
            canvas.drawCircle(x, y, 3, paint);
        }
    }
}
