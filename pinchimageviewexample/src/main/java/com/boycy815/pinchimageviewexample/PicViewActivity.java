package com.boycy815.pinchimageviewexample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

import com.boycy815.pinchimageview.PinchImageView;


public class PicViewActivity extends Activity {

    private static final long ANIM_TIME = 200;

    private RectF mThumbMask;
    private Matrix mThumbMatrix;

    private ObjectAnimator mBackgroundAnimator;
    private ValueAnimator mImageAnimator;

    private View mBackground;
    private PinchImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra("url");
        final Rect rect = getIntent().getParcelableExtra("rect");

        setContentView(R.layout.activity_pic_view);
        mImageView = (PinchImageView) findViewById(R.id.pic);
        mBackground = findViewById(R.id.background);
        final Bitmap bitmap = Global.getCacheBitmap(url);
        mImageView.setImageBitmap(bitmap);

        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mBackgroundAnimator = ObjectAnimator.ofFloat(mBackground, "alpha", 0f, 1f);
                mBackgroundAnimator.setDuration(ANIM_TIME);
                mBackgroundAnimator.start();

                //status bar高度修正
                Rect tempRect = new Rect();
                mImageView.getGlobalVisibleRect(tempRect);
                rect.top = rect.top - tempRect.top;
                rect.bottom = rect.bottom - tempRect.top;

                mThumbMask = new RectF(rect.left, rect.top, rect.right, rect.bottom);
                final RectF bigMask = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());

                mThumbMatrix = new Matrix();
                RectF startRect = new RectF();
                if (((float) bitmap.getWidth()) / ((float) bitmap.getHeight()) > ((float) rect.width()) / ((float) rect.height())) {
                    float scale = ((float) rect.width()) / ((float) bitmap.getWidth());
                    float width = ((float) bitmap.getWidth()) * scale;
                    float height = ((float) bitmap.getHeight()) * scale;
                    startRect.left = rect.left;
                    startRect.top = rect.top + (((float) rect.height()) - height) / 2f;
                    startRect.right = startRect.left + width;
                    startRect.bottom = startRect.top + height;
                } else {
                    float scale = ((float) rect.height()) / ((float) bitmap.getHeight());
                    float width = ((float) bitmap.getWidth()) * scale;
                    float height = ((float) bitmap.getHeight()) * scale;
                    startRect.left = rect.left + (((float) rect.width()) - width) / 2f;
                    startRect.top = rect.top;
                    startRect.right = startRect.left + width;
                    startRect.bottom = startRect.top + height;
                }
                RectF bigRect = mImageView.getImageBound();
                mThumbMatrix.postTranslate(-bigRect.left, -bigRect.top);
                float scale = startRect.width() / bigRect.width();
                mThumbMatrix.postScale(scale, scale);
                mThumbMatrix.postTranslate(startRect.left, startRect.top);
                final Matrix bigMatrix = new Matrix();

                mImageView.setAlpha(1f);
                mImageAnimator = ValueAnimator.ofFloat(0, 1);
                mImageAnimator.setDuration(ANIM_TIME);
                mImageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        float[] maskResult = interpolation(convertRectFToArray(mThumbMask), convertRectFToArray(bigMask), value);
                        mImageView.setMask(new RectF(maskResult[0], maskResult[1], maskResult[2], maskResult[3]));
                        Matrix matrixResult = new Matrix();
                        matrixResult.setValues(interpolation(convertMatrixToArray(mThumbMatrix), convertMatrixToArray(bigMatrix), value));
                        mImageView.setOuterMatrix(matrixResult);
                    }
                });
                mImageAnimator.start();
            }
        });
    }

    private float[] convertRectFToArray(RectF rect) {
        return new float[] {rect.left, rect.top, rect.right, rect.bottom};
    }

    private float[] convertMatrixToArray(Matrix matrix) {
        float[] result = new float[9];
        matrix.getValues(result);
        return result;
    }

    private float[] interpolation(float[] start, float[] end, float process) {
        int l = start.length;
        float[] result = new float[l];
        for (int i = 0; i < l; i++) {
            result[i] = start[i] + (end[i] - start[i]) * process;
        }
        return result;
    }

    @Override
    public void finish() {
        if ((mBackgroundAnimator != null && mBackgroundAnimator.isRunning()) || (mImageAnimator != null && mImageAnimator.isRunning())) {
            return;
        }

        mBackgroundAnimator = ObjectAnimator.ofFloat(mBackground, "alpha", mBackground.getAlpha(), 0f);
        mBackgroundAnimator.setDuration(ANIM_TIME);
        mBackgroundAnimator.start();

        mImageAnimator = ValueAnimator.ofFloat(0, 1);
        mImageAnimator.setDuration(ANIM_TIME);
        final RectF bigMask = mImageView.getImageBound();
        final Matrix bigMatrix = mImageView.getOuterMatrix();
        mImageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                float[] maskResult = interpolation(convertRectFToArray(bigMask), convertRectFToArray(mThumbMask), value);
                mImageView.setMask(new RectF(maskResult[0], maskResult[1], maskResult[2], maskResult[3]));
                Matrix matrixResult = new Matrix();
                matrixResult.setValues(interpolation(convertMatrixToArray(bigMatrix), convertMatrixToArray(mThumbMatrix), value));
                mImageView.setOuterMatrix(matrixResult);
            }
        });
        mImageAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                PicViewActivity.super.finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mImageAnimator.start();
    }
}