package com.boycy815.pinchimageview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 手势图片控件
 * @author clifford
 */
public class PinchImageView extends ImageView  {

    //图片最大可缩放倍数
    private static final float MAX_SCALE = 4;

    //外层变换矩阵，如果是单位矩阵，那么图片是inside center状态
    private Matrix mMatrixBaseFit = new Matrix();

    //手势逻辑
    private GestureImageViewTouchListener mGestureImageViewTouchListener = new GestureImageViewTouchListener();

    //外界点击事件
    private OnClickListener mOnClickListener;

    //外界长按事件
    private OnLongClickListener mOnLongClickListener;

    public PinchImageView(Context context) {
        super(context);
        //强制设置图片scaleType为matrix
        super.setScaleType(ScaleType.MATRIX);
    }

    public PinchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //强制设置图片scaleType为matrix
        super.setScaleType(ScaleType.MATRIX);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //在绘制前设置变换矩阵
        if (getDrawable() != null) {
            Matrix matrix = getImageFitMatrix();
            //图片的变换由内部矩阵（负责让图片inside center），和外部矩阵（负责让图片放大平移）叠加
            matrix.postConcat(mMatrixBaseFit);
            setImageMatrix(matrix);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //将图片触摸事件通知到手势逻辑里面去
        return mGestureImageViewTouchListener.onTouch(this, event);
    }

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

    @Override
    protected void onDetachedFromWindow() {
        reset();
        super.onDetachedFromWindow();
    }

    //不允许设置scaleType，只能用内部设置的matrix
    @Override
    public void setScaleType(ScaleType scaleType) {}

    //获取外部矩阵
    public Matrix getMatrixBaseFit() {
        return mMatrixBaseFit;
    }

    //设置外部矩阵
    public void setMatrixBaseFit(Matrix value) {
        mMatrixBaseFit = value;
    }

    //获取内部矩阵，换了图之后如果图片大小不一样，会重新计算个新的从而保证inside center状态
    //返回的是copy值
    public Matrix getImageFitMatrix() {
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
                    float scale = 1;
                    //如果计算inside center状态所需的scale大小
                    if (imageWidth / imageHeight > displayWidth / displayHeight) {
                        scale = displayWidth / imageWidth;
                    } else {
                        scale = displayHeight / imageHeight;
                    }
                    //设置inside center状态的scale和位置
                    result.postTranslate(-imageWidth / 2f, -imageHeight / 2f);
                    result.postScale(scale, scale);
                    result.postTranslate(displayWidth / 2f, displayHeight / 2f);
                }
            }
        }
        return result;
    }

    //是否允许父容器向右滑动
    //即是否左边已经到边缘并且没有在执行动画
    public boolean canParentDragRight() {
        //缩放手势状态下不允许父容器滚动
        if (mGestureImageViewTouchListener.mScaleMode) {
            return false;
        } else {
            //没有drawable允许滚动
            if (getDrawable() == null) {
                return true;
            } else {
                //检测是否到左边缘
                Matrix matrix = getImageFitMatrix();
                matrix.postConcat(mMatrixBaseFit);
                RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
                matrix.mapRect(bound);
                return bound.left >= 0;
            }
        }
    }

    //是否允许夫容器向左滑动
    //即是否右边已经到边缘并且没有在执行动画
    public boolean canParentDragLeft() {
        //缩放手势状态下不允许父容器滚动
        if (mGestureImageViewTouchListener.mScaleMode) {
            return false;
        } else {
            //没有drawable允许滚动
            if (getDrawable() == null) {
                return true;
            } else {
                //检测是否到右边缘
                Matrix matrix = getImageFitMatrix();
                matrix.postConcat(mMatrixBaseFit);
                RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
                matrix.mapRect(bound);
                return bound.right <= getMeasuredWidth();
            }
        }
    }

    //停止所有动画，重置位置到center inside状态
    public void reset() {
        mMatrixBaseFit = new Matrix();
        mGestureImageViewTouchListener.reset();
        invalidate();
    }

    //手势以及动画逻辑
    private class GestureImageViewTouchListener implements OnTouchListener {

        //滚动模式，在单手指移动时会进入
        private boolean mScrollMode = false;

        //缩放模式，在多手指移动时会进入
        private boolean mScaleMode = false;

        //在单指模式下是上次手指触碰的点
        //在多指模式下两个缩放控制点的中点
        private PointF mLastMovePoint = new PointF();

        //缩放模式下图片的缩放中点，这个点是在原图进行内层变换后的点
        private PointF mScaleCenter = new PointF();

        //缩放模式下的缩放比例，为 外层缩放值 / 开始缩放时两指距离
        private float mScaleBase = 0;

        //矩阵动画，缩放模式把图片的位置大小超出限制之后触发；双击图片放大或缩小时触发
        private ValueAnimator mMatrixAnimator;

        //是否正在执行矩阵动画，当正在执行矩阵动画不允许打断
        private boolean mInMatrixAnimator;

        //滑动产生的惯性动画
        private ValueAnimator mFlingAnimator;

        //点击，双击，长按，滑动等手势处理
        private GestureDetector mGestureDetector = new GestureDetector(PinchImageView.this.getContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                fling(velocityX, velocityY);
                return true;
            }

            public void onLongPress(MotionEvent e) {
                if (mOnLongClickListener != null) {
                    mOnLongClickListener.onLongClick(PinchImageView.this);
                }
            }

            public boolean onDoubleTap(MotionEvent e) {
                doubleTap(e.getX(), e.getY());
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
        public boolean onTouch(View v, MotionEvent event) {
            //无论如何都处理各种外部手势
            mGestureDetector.onTouchEvent(event);
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            //最后一个点抬起或者取消，结束所有模式
            if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mScrollMode = false;
                if (mScaleMode) {
                    scaleEnd();
                }
                mScaleMode = false;
            } else if (action == MotionEvent.ACTION_POINTER_UP) {
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
            //第一个点按下，开启滚动模式，记录开始滚动的点
            } else if (action == MotionEvent.ACTION_DOWN) {
                //在矩阵动画过程中不允许启动滚动模式
                if (!mInMatrixAnimator) {
                    //停止惯性滚动
                    if (mFlingAnimator != null) {
                        mFlingAnimator.cancel();
                        mFlingAnimator = null;
                    }
                    mScrollMode = true;
                    mLastMovePoint.set(event.getX(), event.getY());
                }
            //非第一个点按下，关闭滚动模式，开启缩放模式，记录缩放模式的一些初始数据
            } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                //停止惯性滚动
                if (mFlingAnimator != null) {
                    mFlingAnimator.cancel();
                    mFlingAnimator = null;
                }
                //停止矩阵动画
                if (mMatrixAnimator != null) {
                    mMatrixAnimator.cancel();
                    mMatrixAnimator = null;
                }
                mScrollMode = false;
                mScaleMode = true;
                saveScaleContext(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (!mInMatrixAnimator) {
                    //在滚动模式下移动
                    if (mScrollMode) {
                        //每次移动产生一个差值累积到图片位置上
                        scrollBy(event.getX() - mLastMovePoint.x, event.getY() - mLastMovePoint.y);
                        //记录新的移动点
                        mLastMovePoint.set(event.getX(), event.getY());
                        //在缩放模式下移动
                    } else if (mScaleMode && event.getPointerCount() > 1) {
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
            return true;
        }

        //重置手势所有状态
        private void reset() {
            mScrollMode = false;
            mScaleMode = false;
            mLastMovePoint = new PointF();
            mScaleCenter = new PointF();
            mScaleBase = 0;
            if (mMatrixAnimator != null) {
                mMatrixAnimator.cancel();
                mMatrixAnimator = null;
            }
            mInMatrixAnimator = false;
            if (mFlingAnimator != null) {
                mFlingAnimator.cancel();
                mFlingAnimator = null;
            }
        }

        //让图片移动一段距离，返回是否真的移动了
        private boolean scrollBy(float xDiff, float yDiff) {
            if (getDrawable() == null) {
                return false;
            }
            //对图片变化的矩阵
            Matrix current = getImageFitMatrix();
            current.postConcat(getMatrixBaseFit());
            //原图方框
            RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            //获取变换后图片的方框
            current.mapRect(bound);
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
            getMatrixBaseFit().postTranslate(xDiff, yDiff);
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
            //记录缩放比例
            mScaleBase = MathUtils.getMatrixScale(getMatrixBaseFit())[0] / MathUtils.getDistance(x1, y1, x2, y2);
            //获取缩放缩放点中点在第一层变换后的图片上的坐标
            float[] center = MathUtils.inverseMatrixPoint(MathUtils.getCenterPoint(x1, y1, x2, y2), getMatrixBaseFit());
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
            matrix.postTranslate(-scaleCenter.x, -scaleCenter.y);
            matrix.postScale(scale, scale);
            matrix.postTranslate(lineCenter.x, lineCenter.y);
            //应用变换
            setMatrixBaseFit(matrix);
            //重绘
            invalidate();
        }

        //双击后放大或者缩小
        private void doubleTap(float x, float y) {
            //不允许动画过程中再触发
            if (mInMatrixAnimator || getDrawable() == null) {
                return;
            }
            //获取第一层变换矩阵
            Matrix fitMatrix = getImageFitMatrix();
            //获取第一层变换缩放比例
            float fitScale = MathUtils.getMatrixScale(fitMatrix)[0];
            //获取第二层变换缩放比例
            float fitBaseScale = MathUtils.getMatrixScale(getMatrixBaseFit())[0];
            //控件大小
            float displayWidth = getMeasuredWidth();
            float displayHeight = getMeasuredHeight();
            //当第一层缩放比例已经大于最大等于缩放比例，将无法再放大缩小
            if (fitScale >= MAX_SCALE) {
                return;
            }
            //缩放动画初始矩阵为当前矩阵值
            Matrix animStart = new Matrix();
            animStart.set(getMatrixBaseFit());
            //开始计算缩放动画的结果矩阵
            Matrix animEnd = new Matrix();
            //如果fitBaseScale值小于等于1，说明需要双击放大，否则结果矩阵就是单位矩阵（inside center）
            if (fitBaseScale <= 1) {
                //以双击的地方为放大点
                animEnd.postScale(MAX_SCALE / fitScale, MAX_SCALE / fitScale, x, y);
                //将放大点移动到控件中心
                animEnd.postTranslate(displayWidth / 2 - x, displayHeight / 2 - y);
                //得到放大之后的图片方框
                Matrix current = new Matrix();
                current.set(fitMatrix);
                current.postConcat(animEnd);
                RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
                current.mapRect(bound);
                //修正位置
                float postX = 0;
                float postY = 0;
                if (bound.right - bound.left < displayWidth) {
                    postX = displayWidth / 2 - (bound.right + bound.left) / 2;
                } else if (bound.left > 0) {
                    postX = -bound.left;
                } else if (bound.right < displayWidth) {
                    postX = displayWidth - bound.right;
                }
                if (bound.bottom - bound.top < displayHeight) {
                    postY = displayHeight / 2 - (bound.bottom + bound.top) / 2;
                } else if (bound.top > 0) {
                    postY = -bound.top;
                } else if (bound.bottom < displayHeight) {
                    postY = displayHeight - bound.bottom;
                }
                //应用修正位置
                animEnd.postTranslate(postX, postY);
            }
            //如果正在执行惯性动画，则取消掉
            if (mFlingAnimator != null) {
                mFlingAnimator.cancel();
                mFlingAnimator = null;
            }
            //启动矩阵动画
            mInMatrixAnimator = true;
            mMatrixAnimator = ValueAnimator.ofFloat(0, 1);
            mMatrixAnimator.addUpdateListener(new MatrixAnimator(animStart, animEnd));
            mMatrixAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mInMatrixAnimator = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mInMatrixAnimator = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mMatrixAnimator.setDuration(200);
            mMatrixAnimator.start();
        }

        //当缩放操作结束如果不在正确位置用动画恢复
        private void scaleEnd() {
            //不允许动画过程中再触发
            if (mInMatrixAnimator || getDrawable() == null) {
                return;
            }
            //是否修正了位置
            boolean change = false;
            Matrix matrix = new Matrix();
            //获取图片整体的变换矩阵
            Matrix current = getImageFitMatrix();
            current.postConcat(getMatrixBaseFit());
            //整体缩放比例
            float currentScale = MathUtils.getMatrixScale(current)[0];
            //第二层缩放比例
            float baseFitScale = MathUtils.getMatrixScale(getMatrixBaseFit())[0];
            //控件大小
            float displayWidth = getMeasuredWidth();
            float displayHeight = getMeasuredHeight();
            //原图方框
            RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            //比例修正
            float scalePost = 1;
            //位置修正
            float postX = 0;
            float postY = 0;
            //如果整体缩放比例大于最大比例，进行缩放修正
            if (currentScale > MAX_SCALE) {
                scalePost = scalePost * MAX_SCALE / currentScale;
            }
            //如果缩放修正后整体导致第二层缩放小于1（就是图片比inside center状态还小），重新修正缩放
            if (baseFitScale * scalePost < 1) {
                scalePost = scalePost * 1 / (baseFitScale * scalePost);
            }
            //如果修正不为1，说明进行了修正
            if (scalePost != 1) {
                change = true;
            }
            //尝试根据缩放点进行缩放修正
            current.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
            //获取缩放修正后的图片方框
            current.mapRect(bound);
            //检测缩放修正后位置有无超出，如果超出进行位置修正
            if (bound.right - bound.left < displayWidth) {
                postX = displayWidth / 2 - (bound.right + bound.left) / 2;
            } else if (bound.left > 0) {
                postX = -bound.left;
            } else if (bound.right < displayWidth) {
                postX = displayWidth - bound.right;
            }
            if (bound.bottom - bound.top < displayHeight) {
                postY = displayHeight / 2 - (bound.bottom + bound.top) / 2;
            } else if (bound.top > 0) {
                postY = -bound.top;
            } else if (bound.bottom < displayHeight) {
                postY = displayHeight - bound.bottom;
            }
            //如果位置修正不为0，说明进行了修正
            if (postX != 0 || postY != 0) {
                change = true;
            }
            //只有有执行修正才执行动画
            if (change) {
                //如果up的时候触发惯性，这里需要取消掉，以修正动画为主
                if (mFlingAnimator != null) {
                    mFlingAnimator.cancel();
                    mFlingAnimator = null;
                }
                //产生修正矩阵
                matrix.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
                matrix.postTranslate(postX, postY);
                //动画开始举证
                Matrix animStart = new Matrix();
                animStart.set(getMatrixBaseFit());
                //计算结束举证
                Matrix animEnd = new Matrix();
                animEnd.set(getMatrixBaseFit());
                animEnd.postConcat(matrix);
                //启动矩阵动画
                mInMatrixAnimator = true;
                mMatrixAnimator = ValueAnimator.ofFloat(0, 1);
                mMatrixAnimator.addUpdateListener(new MatrixAnimator(animStart, animEnd));
                mMatrixAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mInMatrixAnimator = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mInMatrixAnimator = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                mMatrixAnimator.setDuration(200);
                mMatrixAnimator.start();
            }
        }

        private void fling(float vx, float vy) {
            //以修正动画为大，遇到修正动画正在执行，就不执行惯性动画
            if (!mInMatrixAnimator && getDrawable() != null) {
                //创建一个滑动惯性动画，由于触发fling前必须down，那时候已经杀掉原动画了，所以这里不需要再杀
                mFlingAnimator = ValueAnimator.ofFloat(0, 1);
                mFlingAnimator.addUpdateListener(new FlingAnimator(new float[]{vx / 1000 * 16, vy / 1000 * 16}));
                mFlingAnimator.setDuration(1000000);
                mFlingAnimator.start();
            }
        }

        //惯性动画
        private class FlingAnimator implements ValueAnimator.AnimatorUpdateListener {

            private float[] mVector;

            public FlingAnimator(float[] vector) {
                mVector = vector;
            }

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                boolean result = scrollBy(mVector[0], mVector[1]);
                mVector[0] *= 0.9;
                mVector[1] *= 0.9;
                if (!result || MathUtils.getDistance(0, 0, mVector[0], mVector[1]) < 1) {
                    if (mFlingAnimator != null) {
                        mFlingAnimator.cancel();
                        mFlingAnimator = null;
                    }
                }
            }
        }

        //矩阵动画
        private class MatrixAnimator implements ValueAnimator.AnimatorUpdateListener {

            private float[] mStart = new float[9];
            private float[] mEnd = new float[9];

            public MatrixAnimator(Matrix start, Matrix end) {
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
                getMatrixBaseFit().setValues(result);
                invalidate();
            }
        }
    }

    //数学计算工具类
    private static class MathUtils {

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
    }
}
