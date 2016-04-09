package com.boycy815.pinchimageviewexample.huge;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.boycy815.pinchimageview.PinchImageView;

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

    ////////////////////////////////size////////////////////////////////

    @Override
    public int getIntrinsicWidth() {
        if (mImageRegionLoader != null) {
            return mImageRegionLoader.getWidth();
        }
        return 0;
    }

    @Override
    public int getIntrinsicHeight() {
        if (mImageRegionLoader != null) {
            return mImageRegionLoader.getHeight();
        }
        return 0;
    }

    ////////////////////////////////init////////////////////////////////

    public interface InitCallback {
        void onInit();
    }

    private ImageRegionLoader mImageRegionLoader;
    private Point mContainerSize;

    private InitCallback mInitCallback;

    private boolean mIsiniting;
    private boolean mIsInited;
    private boolean mIsRecyled;

    public TileDrawable() {
    }

    public void init(ImageRegionLoader loader, Point containerSize) {
        if (!mIsiniting && !mIsRecyled) {
            mIsiniting = true;
            mImageRegionLoader = loader;
            mImageRegionLoader.setRegionLoadCallback(mRegionLoadCallback);
            mContainerSize = containerSize;
            if (mImageRegionLoader.getWidth() > 0 && mImageRegionLoader.getHeight() > 0) {
                initBaseLayer();
            } else {
                mImageRegionLoader.init();
            }
        }
    }

    public void recycle() {
        mIsRecyled = true;
        if (mImageRegionLoader != null) {
            mImageRegionLoader.recycle();
            mImageRegionLoader = null;
        }
        recycleTiles();
    }

    private void onImageRegionLoaderInited() {
        initBaseLayer();
    }

    private void initBaseLayer() {
        if (!mIsRecyled) {
            initTiles(mContainerSize);
            requestBaseLayer();
        }
    }

    private void onBaseLayerInited() {
        if (!mIsRecyled && !mIsInited) {
            mIsInited = true;
            dispatchInitEvent();
        }
    }

    public boolean isInited() {
        return mIsInited;
    }

    public void setInitCallback(InitCallback callback) {
        mInitCallback = callback;
    }

    private void dispatchInitEvent() {
        if (mInitCallback != null) {
            mInitCallback.onInit();
        }
    }

    ////////////////////////////////tiles////////////////////////////////

    private static class Tile {

        public static final int STATUS_RELEASED = 0;
        public static final int STATUS_LOADING = 1;
        public static final int STATUS_LOADED = 2;

        public int mSampleSize;
        public Rect mSampleRect;

        public Bitmap mBitmap;
        public int mStatus;

        public Tile(int sampleSize, Rect sampleRect) {
            mSampleSize = sampleSize;
            mSampleRect = sampleRect;
        }

    }

    private Tile[][] mTiles;

    private ImageRegionLoader.RegionLoadCallback mRegionLoadCallback = new ImageRegionLoader.RegionLoadCallback() {
        @Override
        public void onRegionLoad(int id, int sampleSize, Rect sampleRect, Bitmap bitmap) {
            if (mTiles != null) {
                int i = log2(sampleSize);
                if (mTiles[i][id].mStatus == Tile.STATUS_LOADING) {
                    mTiles[i][id].mBitmap = bitmap;
                    mTiles[i][id].mStatus = Tile.STATUS_LOADED;
                    if (i == mTiles.length - 1) {
                        onBaseLayerInited();
                    }
                }
                invalidateSelf();
            }
        }

        @Override
        public void onInited() {
            onImageRegionLoaderInited();
        }
    };

    private void initTiles(Point container) {
        if (mTiles == null) {
            int iWidth = getIntrinsicWidth();
            int iHeight = getIntrinsicHeight();
            int sWidth = container.x;
            int sHeight = container.y;
            RectF scaledRect = new RectF();
            PinchImageView.MathUtils.calculateScaledRectInContainer(new RectF(0, 0, sWidth, sHeight), iWidth, iHeight, ImageView.ScaleType.FIT_CENTER, scaledRect);
            float scale = scaledRect.width() / (float) iWidth;
            int fullSample = findFitSampleSize(scale);
            int layers = log2(fullSample) + 1;
            mTiles = new Tile[layers][];
            mTiles[layers - 1] = new Tile[]{new Tile(fullSample, new Rect(0, 0, iWidth, iHeight))};
            for (int i = 0; i < layers - 1; i++) {
                int sample = 1 << i;
                int[] widthFragments = cutFragments(iWidth, sWidth * sample);
                int[] heightFragments = cutFragments(iHeight, sHeight * sample);
                Tile[] subTiles = new Tile[widthFragments.length * heightFragments.length];
                for (int h = 0; h < heightFragments.length; h++) {
                    for (int w = 0; w < widthFragments.length; w++) {
                        int left = 0;
                        if (w != 0) {
                            left = widthFragments[w - 1];
                        }
                        int top = 0;
                        if (h != 0) {
                            top = heightFragments[h - 1];
                        }
                        int right = widthFragments[w];
                        int bottom = heightFragments[h];
                        subTiles[h * widthFragments.length + w] = new Tile(sample, new Rect(left, top, right, bottom));
                    }
                }
                mTiles[i] = subTiles;
            }
        }
    }

    private void requestBaseLayer() {
        if (mTiles != null) {
            Tile baseLayer = mTiles[mTiles.length - 1][0];
            if (baseLayer.mStatus == Tile.STATUS_RELEASED) {
                baseLayer.mStatus = Tile.STATUS_LOADING;
                mImageRegionLoader.loadRegion(0, baseLayer.mSampleSize, baseLayer.mSampleRect);
            }
        }
    }

    private Handler mRequestCurrentTilesDelayHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (getCallback() != null && getCallback() instanceof PinchImageView) {
                requestCurrentTiles((PinchImageView) getCallback());

            }
            return true;
        }
    });

    private void requestCurrentTilesDelay(long delay) {
        mRequestCurrentTilesDelayHandler.sendEmptyMessageDelayed(0, delay);
    }

    private void requestCurrentTiles(PinchImageView pinchImageView) {
        if (mTiles != null && pinchImageView.getWidth() > 0 && pinchImageView.getHeight() > 0) {
            Matrix matrix = pinchImageView.getCurrentImageMatrix(null);
            RectF containerRect = new RectF(0, 0, pinchImageView.getWidth(), pinchImageView.getHeight());
            float scale = PinchImageView.MathUtils.getMatrixScale(matrix)[0];
            int sample = findFitSampleSize(scale);
            int sampleIndex = log2(sample);
            if (sampleIndex > mTiles.length - 1) {
                sampleIndex = mTiles.length - 1;
            }
            for (int i = 0; i < mTiles.length - 1; i++) {
                Tile[] layer = mTiles[i];
                if (i == sampleIndex) {
                    for (int j = 0; j < layer.length; j++) {
                        Tile tile = layer[j];
                        RectF rect = new RectF(tile.mSampleRect);
                        matrix.mapRect(rect);
                        if (hitTest(containerRect, rect)) {
                            if (tile.mStatus == Tile.STATUS_RELEASED) {
                                tile.mStatus = Tile.STATUS_LOADING;
                                mImageRegionLoader.loadRegion(j, tile.mSampleSize, tile.mSampleRect);
                            }
                        } else {
                            if (tile.mStatus != Tile.STATUS_RELEASED) {
                                tile.mStatus = Tile.STATUS_RELEASED;
                                mImageRegionLoader.recycleRegion(j, tile.mSampleSize, tile.mSampleRect);
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < layer.length; j++) {
                        Tile tile = layer[j];
                        if (tile.mStatus != Tile.STATUS_RELEASED) {
                            tile.mStatus = Tile.STATUS_RELEASED;
                            mImageRegionLoader.recycleRegion(j, tile.mSampleSize, tile.mSampleRect);
                        }
                    }
                }
            }
        }
    }

    private void drawTiles(Canvas canvas, Paint paint) {
        if (mTiles != null) {
            for (int i = mTiles.length - 1; i >= 0; i--) {
                for (Tile tile : mTiles[i]) {
                    if (tile.mStatus == Tile.STATUS_LOADED && tile.mBitmap != null) {
                        Matrix matrix = new Matrix();
                        PinchImageView.MathUtils.calculateRectTranslateMatrix(new RectF(0, 0, tile.mBitmap.getWidth(), tile.mBitmap.getHeight()), new RectF(tile.mSampleRect), matrix);
                        canvas.drawBitmap(tile.mBitmap, matrix, paint);
                    }
                }
            }
        }
    }

    private void recycleTiles() {
        mTiles = null;
    }

    ////////////////////////////////draw////////////////////////////////

    private Paint mBitmapPaint;

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.save();
        canvas.clipRect(bounds);
        requestCurrentTilesDelay(200);
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint();
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setFilterBitmap(true);
            mBitmapPaint.setDither(true);
        }
        drawTiles(canvas, mBitmapPaint);
        canvas.restore();
    }

    ////////////////////////////////utils////////////////////////////////

    private static int log2(int v) {
        switch (v) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 4:
                return 2;
            case 8:
                return 3;
            case 16:
                return 4;
            case 32:
                return 5;
            case 64:
                return 6;
            case 128:
                return 7;
            case 256:
                return 8;
            case 512:
                return 9;
            case 1024:
                return 10;
            default:
                return (int) Math.round(Math.log(v) / Math.log(2));
        }
    }

    private static int findFitSampleSize(float currentScale) {
        int x = (int) Math.round(Math.log(1 / currentScale) / Math.log(2));
        if (x < 0) {
            x = 0;
        }
        return 1 << x;
    }

    private static int[] cutFragments(int totalLength, int fragmentRequestLength) {
        int size = Math.round((float) totalLength / (float) fragmentRequestLength);
        if (size == 0) {
            size = 1;
        }
        int normalFragment = Math.round((float) totalLength / (float) size);
        int lastFragment = normalFragment + (totalLength - normalFragment * size);
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            int left = 0;
            if (i != 0) {
                left = result[i - 1];
            }
            if (i == size - 1) {
                result[i] = left + lastFragment;
            } else {
                result[i] = left + normalFragment;
            }
        }
        return result;
    }

    private static boolean hitTest(RectF rect1, RectF rect2) {
        return new RectF(rect1).intersect(rect2);
    }
}