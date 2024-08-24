package com.boycy815.pinchimageviewexample.huge;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by clifford on 16/4/6.
 */
public abstract class ImageRegionLoader {

    abstract public void init();

    abstract public int getWidth();

    abstract public int getHeight();

    abstract public void loadRegion(int id, int sampleSize, Rect sampleRect);

    abstract public void recycleRegion(int id, int sampleSize, Rect sampleRect);

    abstract public void recycle();

    public interface RegionLoadCallback {
        void onInited();

        void onRegionLoad(int id, int sampleSize, Rect sampleRect, Bitmap bitmap);
    }

    private RegionLoadCallback mRegionLoadCallback;

    public void setRegionLoadCallback(RegionLoadCallback callback) {
        mRegionLoadCallback = callback;
    }

    protected void dispatchInited() {
        if (mRegionLoadCallback != null) {
            mRegionLoadCallback.onInited();
        }
    }

    protected void dispatchRegionLoad(int id, int sampleSize, Rect sampleRect, Bitmap bitmap) {
        if (mRegionLoadCallback != null) {
            mRegionLoadCallback.onRegionLoad(id, sampleSize, sampleRect, bitmap);
        }
    }
}