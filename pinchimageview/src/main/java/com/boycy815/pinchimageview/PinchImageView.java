package com.boycy815.pinchimageview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 手势图片控件
 * @author clifford
 */
public class PinchImageView extends ImageView  {


    ////////////////////////////////配置参数////////////////////////////////

    //图片缩放动画时间
    public static final int SCALE_ANIMATOR_DURATION = 200;

    //惯性动画衰减参数
    public static final float FLING_DAMPING_FACTOR = 0.9f;

    //图片最大放大尺寸
    private static final float MAX_SCALE = 4f;


    ////////////////////////////////监听器////////////////////////////////

    //外界点击事件
    private OnClickListener mOnClickListener;

    //外界长按事件
    private OnLongClickListener mOnLongClickListener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        //默认的click会在任何点击情况下都会触发，所以搞成自己的
        mOnClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        //默认的long click会在任何长按情况下都会触发，所以搞成自己的
        mOnLongClickListener = l;
    }


    ////////////////////////////////公共状态获取////////////////////////////////

    //手势状态：自由状态
    public static final int PINCH_MODE_FREE = 0;

    //手势状态：单指滚动状态
    public static final int PINCH_MODE_SCROLL = 1;

    //手势状态：多指缩放状态
    public static final int PINCH_MODE_SCALE = 2;

    //外层变换矩阵，如果是单位矩阵，那么图片是fit center状态
    private Matrix mOuterMatrix = new Matrix();

    //矩形遮罩
    private RectF mMask;

    //手势状态，值为PINCH_MODE_FREE，PINCH_MODE_SCROLL，PINCH_MODE_SCALE
    private int mPinchMode = PINCH_MODE_FREE;

    //获取外部矩阵
    public Matrix getOuterMatrix() {
        return new Matrix(mOuterMatrix);
    }

    //获取内部矩阵，换了图之后如果图片大小不一样，会重新计算个新的从而保证fit center状态
    //返回的是copy值
    public Matrix getInnerMatrix() {
        Matrix result = new Matrix();
        if (getDrawable() != null) {
            //控件大小
            float displayWidth = getMeasuredWidth();
            float displayHeight = getMeasuredHeight();
            if (displayWidth > 0 && displayHeight > 0) {
                //原图大小
                float imageWidth = getDrawable().getIntrinsicWidth();
                float imageHeight = getDrawable().getIntrinsicHeight();
                if (imageWidth > 0 && imageHeight > 0) {
                    float scale;
                    //如果计算fit center状态所需的scale大小
                    if (imageWidth / imageHeight > displayWidth / displayHeight) {
                        scale = displayWidth / imageWidth;
                    } else {
                        scale = displayHeight / imageHeight;
                    }
                    //设置fit center状态的scale和位置
                    result.postScale(scale, scale, imageWidth / 2f, imageHeight / 2f);
                    result.postTranslate((displayWidth - imageWidth) / 2f, (displayHeight - imageHeight) / 2f);
                }
            }
        }
        return result;
    }

    //获取图片总变换矩阵
    public Matrix getCurrentImageMatrix() {
        Matrix result = getInnerMatrix();
        result.postConcat(mOuterMatrix);
        return result;
    }

    //获取当前图片变换后的矩形，如果没有图片则返回null
    public RectF getImageBound() {
        if (getDrawable() == null) {
            return null;
        } else {
            Matrix matrix = getCurrentImageMatrix();
            RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            matrix.mapRect(bound);
            return bound;
        }
    }

    //获取当前遮罩
    public RectF getMask() {
        if (mMask != null) {
            return new RectF(mMask);
        } else {
            return null;
        }
    }

    //获取当前手势状态
    public int getPinchMode() {
        return mPinchMode;
    }


    ////////////////////////////////公共状态设置////////////////////////////////

    //执行滚动动画,不会滚动到边界之外去,会导致当前所有手势和手势动画停止
    public void scrollImageTo(float x, float y, long duration) {
        float displayWidth = getMeasuredWidth();
        float displayHeight = getMeasuredHeight();
        if (displayWidth > 0 && displayHeight > 0  && getDrawable() != null) {
            RectF bound = getImageBound();
            if (x > 0) {
                if (bound.left < 0) {
                    if (bound.left + x > 0) {
                        bound.left = 0;
                    } else {
                        bound.left = bound.left + x;
                    }
                }
            } else if (x < 0) {
                if (bound.right > displayWidth) {
                    if (bound.right + x < displayWidth) {
                        bound.right = displayWidth;
                    } else {
                        bound.right = bound.right + x;
                    }
                }
            }
            if (y > 0) {
                if (bound.top < 0) {
                    if (bound.top + y > 0) {
                        bound.top = 0;
                    } else {
                        bound.top = bound.top + y;
                    }
                }
            } else if (y < 0) {
                if (bound.bottom > displayHeight) {
                    if (bound.bottom + y < displayHeight) {
                        bound.bottom = displayHeight;
                    } else {
                        bound.bottom = bound.bottom + y;
                    }
                }
            }
            zoomImageTo(bound, duration);
        }
    }

    //执行缩放动画,会停止当前所有的手势和手势动画
    public void zoomImageTo(RectF imageRect, long duration) {
        float displayWidth = getMeasuredWidth();
        float displayHeight = getMeasuredHeight();
        if (displayWidth > 0 && displayHeight > 0
                && getDrawable() != null && getDrawable().getIntrinsicWidth() > 0 && getDrawable().getIntrinsicHeight() > 0
                && imageRect != null) {
            Matrix innerMatrix = getInnerMatrix();
            RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            innerMatrix.mapRect(bound);
            Matrix startMatrix = new Matrix(mOuterMatrix);
            Matrix endMatrix = MathUtils.calculateRectTranslateMatrix(bound, imageRect);
            mPinchMode = PINCH_MODE_FREE;
            cancelAllAnimator();
            if (duration <= 0) {
                mOuterMatrix.set(endMatrix);
                dispatchOuterMatrixChanged();
                invalidate();
            } else {
                mScaleAnimator = new ScaleAnimator(startMatrix, endMatrix, duration);
                mScaleAnimator.start();
            }
        }
    }

    //执行缩放动画,会停止当前所有的手势和手势动画
    public void outerMatrixTo(Matrix matrix, long duration) {
        Matrix startMatrix = new Matrix(mOuterMatrix);
        Matrix endMatrix = new Matrix(matrix);
        mPinchMode = PINCH_MODE_FREE;
        cancelAllAnimator();
        if (duration <= 0) {
            mOuterMatrix.set(endMatrix);
            dispatchOuterMatrixChanged();
            invalidate();
        } else {
            mScaleAnimator = new ScaleAnimator(startMatrix, endMatrix, duration);
            mScaleAnimator.start();
        }
    }

    //执行mask动画
    public void zoomMaskTo(RectF mask, long duration) {
        if (mask != null) {
            if (mMaskAnimator != null) {
                mMaskAnimator.cancel();
                mMaskAnimator = null;
            }
            if (duration <= 0 || mMask == null) {
                if (mMask == null) {
                    mMask = new RectF();
                }
                mMask.set(mask);
                invalidate();
            } else {
                mMaskAnimator = new MaskAnimator(mMask, mask, duration);
                mMaskAnimator.start();
            }
        }
    }

    //停止所有动画，重置位置到fit center状态
    public void reset() {
        mOuterMatrix.set(new Matrix());
        dispatchOuterMatrixChanged();
        mMask = null;
        mPinchMode = PINCH_MODE_FREE;
        if (mMaskAnimator != null) {
            mMaskAnimator.cancel();
            mMaskAnimator = null;
        }
        mLastMovePoint.set(0, 0);
        mScaleCenter.set(0, 0);
        mScaleBase = 0;
        cancelAllAnimator();
        invalidate();
    }


    ////////////////////////////////对外广播事件////////////////////////////////

    //事件listener
    public interface OuterMatrixChangedListener {
        //当图片外部矩阵发生变化就触发
        void onOuterMatrixChanged(PinchImageView pinchImageView);
    }

    private List<OuterMatrixChangedListener> mOuterMatrixChangedListeners;
    private List<OuterMatrixChangedListener> mOuterMatrixChangedListenersCopy;
    private int mDispatchOuterMatrixChangedLock;

    public void addOuterMatrixChangedListener(OuterMatrixChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListeners == null) {
                mOuterMatrixChangedListeners = new ArrayList<OuterMatrixChangedListener>();
            }
            mOuterMatrixChangedListeners.add(listener);
        } else {
            if (mOuterMatrixChangedListenersCopy == null) {
                if (mOuterMatrixChangedListeners != null) {
                    mOuterMatrixChangedListenersCopy = new ArrayList<OuterMatrixChangedListener>(mOuterMatrixChangedListeners);
                } else {
                    mOuterMatrixChangedListenersCopy = new ArrayList<OuterMatrixChangedListener>();
                }
            }
            mOuterMatrixChangedListenersCopy.add(listener);
        }
    }

    public void removeOuterMatrixChangedListener(OuterMatrixChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListeners != null) {
                mOuterMatrixChangedListeners.remove(listener);
            }
        } else {
            if (mOuterMatrixChangedListenersCopy == null) {
                if (mOuterMatrixChangedListeners != null) {
                    mOuterMatrixChangedListenersCopy = new ArrayList<OuterMatrixChangedListener>(mOuterMatrixChangedListeners);
                }
            }
            if (mOuterMatrixChangedListenersCopy != null) {
                mOuterMatrixChangedListenersCopy.remove(listener);
            }
        }
    }

    private void dispatchOuterMatrixChanged() {
        if (mOuterMatrixChangedListeners == null) {
            return;
        }
        mDispatchOuterMatrixChangedLock++;
        for (OuterMatrixChangedListener listener : mOuterMatrixChangedListeners) {
            listener.onOuterMatrixChanged(this);
        }
        mDispatchOuterMatrixChangedLock--;
        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListenersCopy != null) {
                mOuterMatrixChangedListeners = mOuterMatrixChangedListenersCopy;
                mOuterMatrixChangedListenersCopy = null;
            }
        }
    }


    ////////////////////////////////用于重载定制////////////////////////////////

    //获取图片最大可放大的比例，如果放大大于这个比例则不被允许
    protected float getMaxScale() {
        return MAX_SCALE;
    }

    //计算双击之后图片应该被缩放的比例，如果值大于getMaxScale或者小于fit center尺寸，则取边界值
    protected float calculateNextScale(float innerScale, float outerScale) {
        float currentScale = innerScale * outerScale;
        if (currentScale < MAX_SCALE) {
            return MAX_SCALE;
        } else {
            return innerScale;
        }
    }


    ////////////////////////////////初始化////////////////////////////////

    public PinchImageView(Context context) {
        super(context);
        initView();
    }

    public PinchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PinchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        //强制设置图片scaleType为matrix
        super.setScaleType(ScaleType.MATRIX);
    }

    //不允许设置scaleType，只能用内部设置的matrix
    @Override
    public void setScaleType(ScaleType scaleType) {}


    ////////////////////////////////绘制////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        //在绘制前设置变换矩阵
        if (getDrawable() != null) {
            setImageMatrix(getCurrentImageMatrix());
        }
        //对图像做遮罩处理
        if (mMask != null) {
            canvas.save();
            canvas.clipRect(mMask);
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }


    ////////////////////////////////mask动画处理////////////////////////////////

    //mask修改的动画,和图片的动画相互独立
    private MaskAnimator mMaskAnimator;

    //mask动画
    private class MaskAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        private float[] mStart = new float[4];
        private float[] mEnd = new float[4];

        public MaskAnimator(RectF start, RectF end, long duration) {
            super();
            setFloatValues(0, 1);
            setDuration(duration);
            addUpdateListener(this);
            mStart[0] = start.left;
            mStart[1] = start.top;
            mStart[2] = start.right;
            mStart[3] = start.bottom;
            mEnd[0] = end.left;
            mEnd[1] = end.top;
            mEnd[2] = end.right;
            mEnd[3] = end.bottom;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (Float) animation.getAnimatedValue();
            float[] result = new float[4];
            for (int i = 0; i < 4; i++) {
                result[i] = mStart[i] + (mEnd[i] - mStart[i]) * value;
            }
            if (mMask == null) {
                mMask = new RectF();
            }
            mMask.set(result[0], result[1], result[2], result[3]);
            invalidate();
        }
    }


    ////////////////////////////////手势动画处理////////////////////////////////

    //在单指模式下是上次手指触碰的点
    //在多指模式下两个缩放控制点的中点
    private PointF mLastMovePoint = new PointF();

    //缩放模式下图片的缩放中点，这个点是在原图进行内层变换后的点
    private PointF mScaleCenter = new PointF();

    //缩放模式下的缩放比例，为 外层缩放值 / 开始缩放时两指距离
    private float mScaleBase = 0;

    //矩阵动画，缩放模式把图片的位置大小超出限制之后触发；双击图片放大或缩小时触发
    private ScaleAnimator mScaleAnimator;

    //滑动产生的惯性动画
    private FlingAnimator mFlingAnimator;

    //程序触发的滚动动画
    private ScaleAnimator mScrollAnimator;

    //点击，双击，长按，滑动等手势处理
    private GestureDetector mGestureDetector = new GestureDetector(PinchImageView.this.getContext(), new GestureDetector.SimpleOnGestureListener() {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mPinchMode == PINCH_MODE_FREE && !(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                fling(velocityX, velocityY);
            }
            return true;
        }

        public void onLongPress(MotionEvent e) {
            if (mOnLongClickListener != null) {
                mOnLongClickListener.onLongClick(PinchImageView.this);
            }
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (mPinchMode == PINCH_MODE_SCROLL && !(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                doubleTap(e.getX(), e.getY());
            }
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(PinchImageView.this);
            }
            return true;
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        //最后一个点抬起或者取消，结束所有模式
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (mPinchMode == PINCH_MODE_SCALE) {
                scaleEnd();
            }
            mPinchMode = PINCH_MODE_FREE;
        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            if (mPinchMode == PINCH_MODE_SCALE) {
                //抬起的点如果大于2，那么缩放模式还有效，但是有可能初始点变了，重新测量初始点
                if (event.getPointerCount() > 2) {
                    //如果还没结束缩放模式，但是第一个点抬起了，那么让第二个点和第三个点作为缩放控制点
                    if (event.getAction() >> 8 == 0) {
                        saveScaleContext(event.getX(1), event.getY(1), event.getX(2), event.getY(2));
                        //如果还没结束缩放模式，但是第二个点抬起了，那么让第一个点和第三个点作为缩放控制点
                    } else if (event.getAction() >> 8 == 1) {
                        saveScaleContext(event.getX(0), event.getY(0), event.getX(2), event.getY(2));
                    }
                }
            }
            //第一个点按下，开启滚动模式，记录开始滚动的点
        } else if (action == MotionEvent.ACTION_DOWN) {
            //在矩阵动画过程中不允许启动滚动模式
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                //停止所有动画
                cancelAllAnimator();
                mPinchMode = PINCH_MODE_SCROLL;
                mLastMovePoint.set(event.getX(), event.getY());
            }
            //非第一个点按下，关闭滚动模式，开启缩放模式，记录缩放模式的一些初始数据
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            //停止所有动画
            cancelAllAnimator();
            mPinchMode = PINCH_MODE_SCALE;
            saveScaleContext(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                //在滚动模式下移动
                if (mPinchMode == PINCH_MODE_SCROLL) {
                    //每次移动产生一个差值累积到图片位置上
                    scrollBy(event.getX() - mLastMovePoint.x, event.getY() - mLastMovePoint.y);
                    //记录新的移动点
                    mLastMovePoint.set(event.getX(), event.getY());
                    //在缩放模式下移动
                } else if (mPinchMode == PINCH_MODE_SCALE && event.getPointerCount() > 1) {
                    //两个缩放点间的距离
                    float distance = MathUtils.getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    //保存缩放点中点
                    float[] lineCenter = MathUtils.getCenterPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    mLastMovePoint.set(lineCenter[0], lineCenter[1]);
                    //处理缩放
                    scale(mScaleCenter, mScaleBase, distance, mLastMovePoint);
                }
            }
        }
        //无论如何都处理各种外部手势
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    //让图片移动一段距离，返回是否真的移动了
    private boolean scrollBy(float xDiff, float yDiff) {
        if (getDrawable() == null) {
            return false;
        }
        //原图方框
        RectF bound = getImageBound();
        //控件大小
        float displayWidth = getMeasuredWidth();
        float displayHeight = getMeasuredHeight();
        //如果当前图片宽度小于控件宽度，则不能移动
        if (bound.right - bound.left < displayWidth) {
            xDiff = 0;
            //如果图片左边在移动后超出控件左边
        } else if (bound.left + xDiff > 0) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (bound.left < 0) {
                xDiff = -bound.left;
                //否则无法移动
            } else {
                xDiff = 0;
            }
            //如果图片右边在移动后超出控件右边
        } else if (bound.right + xDiff < displayWidth) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (bound.right > displayWidth) {
                xDiff = displayWidth - bound.right;
                //否则无法移动
            } else {
                xDiff = 0;
            }
        }
        //以下同理
        if (bound.bottom - bound.top < displayHeight) {
            yDiff = 0;
        } else if (bound.top + yDiff > 0) {
            if (bound.top < 0) {
                yDiff = -bound.top;
            } else {
                yDiff = 0;
            }
        } else if (bound.bottom + yDiff < displayHeight) {
            if (bound.bottom > displayHeight) {
                yDiff = displayHeight - bound.bottom;
            } else {
                yDiff = 0;
            }
        }
        //应用移动变换
        mOuterMatrix.postTranslate(xDiff, yDiff);
        dispatchOuterMatrixChanged();
        //触发重绘
        invalidate();
        //检查是否有变化
        if (xDiff != 0 || yDiff != 0) {
            return true;
        } else {
            return false;
        }
    }

    //记录缩放前的一些信息
    private void saveScaleContext(float x1, float y1, float x2, float y2) {
        mScaleBase = MathUtils.getMatrixScale(mOuterMatrix)[0] / MathUtils.getDistance(x1, y1, x2, y2);
        //获取缩放缩放点中点在第一层变换后的图片上的坐标
        float[] center = MathUtils.inverseMatrixPoint(MathUtils.getCenterPoint(x1, y1, x2, y2), mOuterMatrix);
        mScaleCenter.set(center[0], center[1]);
    }

    /**
     * 对图片进行缩放
     * @param scaleCenter 图片的缩放中心，是一层变换后的左边
     * @param scaleBase 缩放比例
     * @param distance 新的缩放点距离
     * @param lineCenter 缩放点中心
     */
    private void scale(PointF scaleCenter, float scaleBase, float distance, PointF lineCenter) {
        if (getDrawable() == null) {
            return;
        }
        //计算第二层缩放值
        float scale = scaleBase * distance;
        Matrix matrix = new Matrix();
        //按照图片缩放中心缩放，并且让缩放中心在缩放点中点上
        matrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y);
        matrix.postTranslate(lineCenter.x - scaleCenter.x, lineCenter.y - scaleCenter.y);
        //应用变换
        mOuterMatrix.set(matrix);
        dispatchOuterMatrixChanged();
        //重绘
        invalidate();
    }

    //双击后放大或者缩小
    //当当前缩放比例大于等于1，那么双击放大到MaxScale
    //当当前缩放比例小于1，双击放大到1
    //当当前缩放比例等于MaxScale，双击缩小到屏幕大小
    private void doubleTap(float x, float y) {
        if (getDrawable() == null) {
            return;
        }
        //获取第一层变换矩阵
        Matrix innerMatrix = getInnerMatrix();
        //当前总的缩放比例
        float innerScale = MathUtils.getMatrixScale(innerMatrix)[0];
        float outerScale = MathUtils.getMatrixScale(mOuterMatrix)[0];
        float currentScale = innerScale * outerScale;
        //控件大小
        float displayWidth = getMeasuredWidth();
        float displayHeight = getMeasuredHeight();
        //最大放大大小
        float maxScale = getMaxScale();
        //接下来要放大的大小
        float nextScale = calculateNextScale(innerScale, outerScale);
        //如果接下来放大大于最大值或者小于fit center值，则取边界
        if (nextScale < innerScale) {
            nextScale = innerScale;
        } else if (nextScale > maxScale) {
            nextScale = maxScale;
        }
        //缩放动画初始矩阵为当前矩阵值
        Matrix animStart = new Matrix(mOuterMatrix);
        //开始计算缩放动画的结果矩阵
        Matrix animEnd = new Matrix(mOuterMatrix);
        //计算还需缩放的倍数
        animEnd.postScale(nextScale / currentScale, nextScale / currentScale, x, y);
        //将放大点移动到控件中心
        animEnd.postTranslate(displayWidth / 2 - x, displayHeight / 2 - y);
        //得到放大之后的图片方框
        Matrix testMatrix = new Matrix(innerMatrix);
        testMatrix.postConcat(animEnd);
        RectF testBound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        testMatrix.mapRect(testBound);
        //修正位置
        float postX = 0;
        float postY = 0;
        if (testBound.right - testBound.left < displayWidth) {
            postX = displayWidth / 2 - (testBound.right + testBound.left) / 2;
        } else if (testBound.left > 0) {
            postX = -testBound.left;
        } else if (testBound.right < displayWidth) {
            postX = displayWidth - testBound.right;
        }
        if (testBound.bottom - testBound.top < displayHeight) {
            postY = displayHeight / 2 - (testBound.bottom + testBound.top) / 2;
        } else if (testBound.top > 0) {
            postY = -testBound.top;
        } else if (testBound.bottom < displayHeight) {
            postY = displayHeight - testBound.bottom;
        }
        //应用修正位置
        animEnd.postTranslate(postX, postY);
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        //启动矩阵动画
        mScaleAnimator = new ScaleAnimator(animStart, animEnd);
        mScaleAnimator.start();
    }

    //当缩放操作结束如果不在正确位置用动画恢复
    private void scaleEnd() {
        if (getDrawable() == null) {
            return;
        }
        //是否修正了位置
        boolean change = false;
        //获取图片整体的变换矩阵
        Matrix currentMatrix = getCurrentImageMatrix();
        //整体缩放比例
        float currentScale = MathUtils.getMatrixScale(currentMatrix)[0];
        //第二层缩放比例
        float outerScale = MathUtils.getMatrixScale(mOuterMatrix)[0];
        //控件大小
        float displayWidth = getMeasuredWidth();
        float displayHeight = getMeasuredHeight();
        float maxScale = getMaxScale();
        //比例修正
        float scalePost = 1;
        //位置修正
        float postX = 0;
        float postY = 0;
        //如果整体缩放比例大于最大比例，进行缩放修正
        if (currentScale > maxScale) {
            scalePost = maxScale / currentScale;
        }
        //如果缩放修正后整体导致第二层缩放小于1（就是图片比fit center状态还小），重新修正缩放
        if (outerScale * scalePost < 1) {
            scalePost = 1 / outerScale;
        }
        //如果修正不为1，说明进行了修正
        if (scalePost != 1) {
            change = true;
        }
        //尝试根据缩放点进行缩放修正
        Matrix testMatrix = new Matrix(currentMatrix);
        testMatrix.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
        RectF testBound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        //获取缩放修正后的图片方框
        testMatrix.mapRect(testBound);
        //检测缩放修正后位置有无超出，如果超出进行位置修正
        if (testBound.right - testBound.left < displayWidth) {
            postX = displayWidth / 2 - (testBound.right + testBound.left) / 2;
        } else if (testBound.left > 0) {
            postX = -testBound.left;
        } else if (testBound.right < displayWidth) {
            postX = displayWidth - testBound.right;
        }
        if (testBound.bottom - testBound.top < displayHeight) {
            postY = displayHeight / 2 - (testBound.bottom + testBound.top) / 2;
        } else if (testBound.top > 0) {
            postY = -testBound.top;
        } else if (testBound.bottom < displayHeight) {
            postY = displayHeight - testBound.bottom;
        }
        //如果位置修正不为0，说明进行了修正
        if (postX != 0 || postY != 0) {
            change = true;
        }
        //只有有执行修正才执行动画
        if (change) {
            //动画开始矩阵
            Matrix animStart = new Matrix(mOuterMatrix);
            //计算结束矩阵
            Matrix animEnd = new Matrix(mOuterMatrix);
            animEnd.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
            animEnd.postTranslate(postX, postY);
            //清理当前可能正在执行的动画
            cancelAllAnimator();
            //启动矩阵动画
            mScaleAnimator = new ScaleAnimator(animStart, animEnd);
            mScaleAnimator.start();
        }
    }

    private void fling(float vx, float vy) {
        if (getDrawable() == null) {
            return;
        }
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        mFlingAnimator = new FlingAnimator(new float[]{vx / 1000 * 16, vy / 1000 * 16});
        mFlingAnimator.start();
    }

    private void cancelAllAnimator() {
        if (mScaleAnimator != null) {
            mScaleAnimator.cancel();
            mScaleAnimator = null;
        }
        if (mFlingAnimator != null) {
            mFlingAnimator.cancel();
            mFlingAnimator = null;
        }
        if (mScrollAnimator != null) {
            mScrollAnimator.cancel();
            mScrollAnimator = null;
        }
    }

    //惯性动画
    private class FlingAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        private float[] mVector;

        public FlingAnimator(float[] vector) {
            super();
            setFloatValues(0, 1);
            setDuration(1000000);
            addUpdateListener(this);
            mVector = vector;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            boolean result = scrollBy(mVector[0], mVector[1]);
            mVector[0] *= FLING_DAMPING_FACTOR;
            mVector[1] *= FLING_DAMPING_FACTOR;
            if (!result || MathUtils.getDistance(0, 0, mVector[0], mVector[1]) < 1) {
                animation.cancel();
            }
        }
    }

    //缩放动画
    private class ScaleAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        private float[] mStart = new float[9];
        private float[] mEnd = new float[9];

        public ScaleAnimator(Matrix start, Matrix end) {
            this(start, end, SCALE_ANIMATOR_DURATION);
        }

        public ScaleAnimator(Matrix start, Matrix end, long duration) {
            super();
            setFloatValues(0, 1);
            setDuration(duration);
            addUpdateListener(this);
            start.getValues(mStart);
            end.getValues(mEnd);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (Float) animation.getAnimatedValue();
            float[] result = new float[9];
            for (int i = 0; i < 9; i++) {
                result[i] = mStart[i] + (mEnd[i] - mStart[i]) * value;
            }
            mOuterMatrix.setValues(result);
            dispatchOuterMatrixChanged();
            invalidate();
        }
    }


    ////////////////////////////////数学计算工具类////////////////////////////////

    public static class MathUtils {

        //获取两点距离
        public static float getDistance(float x1, float y1, float x2, float y2) {
            float x = x1 - x2;
            float y = y1 - y2;
            return (float) Math.sqrt(x * x + y * y);
        }

        //获取两点中间点
        public static float[] getCenterPoint(float x1, float y1, float x2, float y2) {
            return new float[]{(x1 + x2) / 2f, (y1 + y2) / 2f};
        }

        //获取矩阵的缩放值
        public static float[] getMatrixScale(Matrix matrix) {
            if (matrix != null) {
                float[] value = new float[9];
                matrix.getValues(value);
                return new float[]{value[0], value[4]};
            } else {
                return new float[2];
            }
        }

        //计算点除以矩阵之后的值
        public static float[] inverseMatrixPoint(float[] point, Matrix matrix) {
            if (point != null && matrix != null) {
                float[] dst = new float[2];
                Matrix inverse = new Matrix();
                matrix.invert(inverse);
                inverse.mapPoints(dst, point);
                return dst;
            } else {
                return new float[2];
            }
        }

        //计算两个矩形之间的变换矩阵
        public static Matrix calculateRectTranslateMatrix(RectF from, RectF to) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(-from.left, -from.top);
            matrix.postScale(to.width() / from.width(), to.height() / from.height());
            matrix.postTranslate(to.left, to.top);
            return matrix;
        }

        public static RectF calculateScaledRectInContainer(RectF container, float srcWidth, float srcHeight, ScaleType scaleType) {
            if (ScaleType.FIT_XY.equals(scaleType)) {
                RectF result = new RectF();
                result.set(container);
                return result;
            } else if (ScaleType.CENTER.equals(scaleType)) {
                RectF result = new RectF();
                Matrix matrix = new Matrix();
                matrix.setTranslate((container.width() - srcWidth) * 0.5f, (container.height() - srcHeight) * 0.5f);
                matrix.mapRect(result, new RectF(0, 0, srcWidth, srcHeight));
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
                return result;
            } else if (ScaleType.CENTER_CROP.equals(scaleType)) {
                RectF result = new RectF();
                Matrix matrix = new Matrix();
                float scale;
                float dx = 0;
                float dy = 0;
                if (srcWidth * container.height() > container.width() * srcHeight) {
                    scale = container.height() / srcHeight;
                    dx = (container.width() - srcWidth * scale) * 0.5f;
                } else {
                    scale = container.width() / srcWidth;
                    dy = (container.height() - srcHeight * scale) * 0.5f;
                }
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                matrix.mapRect(result, new RectF(0, 0, srcWidth, srcHeight));
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
                return result;
            } else if (ScaleType.CENTER_INSIDE.equals(scaleType)) {
                RectF result = new RectF();
                Matrix matrix = new Matrix();
                float scale;
                float dx;
                float dy;
                if (srcWidth <= container.width() && srcHeight <= container.height()) {
                    scale = 1.0f;
                } else {
                    scale = Math.min(container.width() / srcWidth, container.height() / srcHeight);
                }
                dx = (container.width() - srcWidth * scale) * 0.5f;
                dy = (container.height() - srcHeight * scale) * 0.5f;
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                matrix.mapRect(result, new RectF(0, 0, srcWidth, srcHeight));
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
                return result;
            } else if (ScaleType.FIT_CENTER.equals(scaleType)) {
                RectF result = new RectF();
                Matrix matrix = new Matrix();
                RectF tempSrc = new RectF(0, 0, srcWidth, srcHeight);
                RectF tempDst = new RectF(0, 0, container.width(), container.height());
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
                matrix.mapRect(result, new RectF(0, 0, srcWidth, srcHeight));
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
                return result;
            } else if (ScaleType.FIT_START.equals(scaleType)) {
                RectF result = new RectF();
                Matrix matrix = new Matrix();
                RectF tempSrc = new RectF(0, 0, srcWidth, srcHeight);
                RectF tempDst = new RectF(0, 0, container.width(), container.height());
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.START);
                matrix.mapRect(result, new RectF(0, 0, srcWidth, srcHeight));
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
                return result;
            } else if (ScaleType.FIT_END.equals(scaleType)) {
                RectF result = new RectF();
                Matrix matrix = new Matrix();
                RectF tempSrc = new RectF(0, 0, srcWidth, srcHeight);
                RectF tempDst = new RectF(0, 0, container.width(), container.height());
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.END);
                matrix.mapRect(result, new RectF(0, 0, srcWidth, srcHeight));
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
                return result;
            } else {
                RectF result = new RectF();
                result.set(container);
                return result;
            }
        }
    }
}