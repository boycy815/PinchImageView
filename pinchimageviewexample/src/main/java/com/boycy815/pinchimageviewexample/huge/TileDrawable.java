package com.boycy815.pinchimageviewexample.huge;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

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
        return mImageRegionLoader.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mImageRegionLoader.getHeight();
    }

    ////////////////////////////////init////////////////////////////////

    private ImageRegionLoader mImageRegionLoader;

    public TileDrawable(ImageRegionLoader loader) {
        mImageRegionLoader = loader;
        mImageRegionLoader.setRegionLoadCallback(mRegionLoadCallback);
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

                System.out.println("asdasdasasda:onRegionLoad:id:" + id + ":sampleSize:" + sampleSize + ":sampleRect:" + sampleRect.left + " " + sampleRect.top + " " + sampleRect.right + " " + sampleRect.bottom);

                int i = log2(sampleSize);
                if (mTiles[i][id].mStatus == Tile.STATUS_LOADING) {
                    mTiles[i][id].mBitmap = bitmap;
                    mTiles[i][id].mStatus = Tile.STATUS_LOADED;
                }

                invalidateSelf();
            }
        }
    };

    private void tryInitTiles(PinchImageView pinchImageView) {
        if (mTiles == null) {
            if (pinchImageView.getWidth() > 0 && pinchImageView.getHeight() > 0) {
                Matrix matrix = pinchImageView.getInnerMatrix(null);
                float scale = PinchImageView.MathUtils.getMatrixScale(matrix)[0];
                int fullSample = findFitSampleSize(scale);
                int layers = log2(fullSample) + 1;
                int iWidth = getIntrinsicWidth();
                int iHeight = getIntrinsicHeight();
                int sWidth = pinchImageView.getWidth();
                int sHeight = pinchImageView.getHeight();
                mTiles = new Tile[layers][];
                mTiles[layers - 1] = new Tile[]{new Tile(fullSample, new Rect(0, 0, iWidth, iHeight))};
                for (int i = 0; i < layers - 1; i++) {
                    int sample = 1 << i;
                    int widthAfterSample = Math.round((float) iWidth / (float) sample);
                    int heightAfterSample = Math.round((float) iHeight / (float) sample);
                    int[] widthFragments = cutFragments(widthAfterSample, sWidth);
                    int[] heightFragments = cutFragments(heightAfterSample, sHeight);
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
            Tile baseLayer = mTiles[mTiles.length - 1][0];
            if (baseLayer.mStatus == Tile.STATUS_RELEASED) {
                baseLayer.mStatus = Tile.STATUS_LOADING;
                mImageRegionLoader.loadRegion(0, baseLayer.mSampleSize, baseLayer.mSampleRect);
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
                                mImageRegionLoader.recycleRegion(j, tile.mSampleSize, tile.mSampleRect, tile.mBitmap);
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < layer.length; j++) {
                        Tile tile = layer[j];
                        if (tile.mStatus != Tile.STATUS_RELEASED) {
                            tile.mStatus = Tile.STATUS_RELEASED;
                            mImageRegionLoader.recycleRegion(j, tile.mSampleSize, tile.mSampleRect, tile.mBitmap);
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

    ////////////////////////////////draw////////////////////////////////

    private Paint mBitmapPaint;

    @Override
    public void draw(Canvas canvas) {
        if (getCallback() != null && getCallback() instanceof PinchImageView) {
            Rect bounds = getBounds();
            canvas.save();
            canvas.clipRect(bounds);
            tryInitTiles((PinchImageView) getCallback());
            requestCurrentTiles((PinchImageView) getCallback());
            if (mBitmapPaint == null) {
                mBitmapPaint = new Paint();
                mBitmapPaint.setAntiAlias(true);
                mBitmapPaint.setFilterBitmap(true);
                mBitmapPaint.setDither(true);
            }
            drawTiles(canvas, mBitmapPaint);
            canvas.restore();
        }
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
        if ((rect1.left < rect2.left && rect1.right > rect2.left) || (rect1.left < rect2.right && rect1.right > rect2.right)) {
            if ((rect1.top < rect2.top && rect1.bottom > rect2.top) || (rect1.top < rect2.bottom && rect1.bottom > rect2.bottom)) {
                return true;
            }
        }
        return false;
    }
}