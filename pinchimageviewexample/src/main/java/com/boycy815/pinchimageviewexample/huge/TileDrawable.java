package com.boycy815.pinchimageviewexample.huge;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by clifford on 16/4/6.
 */
public class TileDrawable extends Drawable {

    ////////////////////////////////overrides////////////////////////////////

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setFilterBitmap(boolean filter) {}

    @Override
    public void setDither(boolean dither) {}

    @Override
    public void setColorFilter(ColorFilter colorFilter) {}

    @Override
    public void setAlpha(int alpha) {}

    ////////////////////////////////init////////////////////////////////

    private String mFilePath;

    public TileDrawable(String filePath) {
        mFilePath = filePath;
    }

    ////////////////////////////////draw////////////////////////////////

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.save();
        canvas.clipRect(bounds);
        // TODO: 16/4/5
        canvas.restore();
    }

    ////////////////////////////////size////////////////////////////////

    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }
}