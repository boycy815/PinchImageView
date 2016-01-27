package com.boycy815.pinchimageview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

/**
 * 支持多张图片切换的ImageView，可缩放、平移，用于大图预览
 * <p>
 * Author:李玉江[QQ:1032694760]
 * Email:liyujiang_tk@yeah.net
 * DateTime:2016/1/27 13:15
 * Builder:Android Studio
 */
public class SwitcherImageView extends PinchImageView implements SimpleFingerGestures.OnFingerGestureListener {
    private DisplayMetrics displayMetrics;
    private String[] urls;
    private int position;
    private OnSwitchListener onSwitchListener;

    /**
     * Instantiates a new Switcher image view.
     *
     * @param context the context
     */
    public SwitcherImageView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Switcher image view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public SwitcherImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Instantiates a new Switcher image view.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public SwitcherImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Sets data.
     *
     * @param urls             the urls
     * @param position         the position
     * @param onSwitchListener the on switch listener
     */
    public void setData(String[] urls, int position, OnSwitchListener onSwitchListener) {
        this.urls = urls;
        this.position = position;
        this.onSwitchListener = onSwitchListener;
        if (this.onSwitchListener != null) {
            this.onSwitchListener.onSwitched(this, this.urls[this.position]);
        }
    }

    private void init(final Context context) {
        displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        SimpleFingerGestures gestures = new SimpleFingerGestures();
        gestures.setOnFingerGestureListener(this);
        setOnTouchListener(gestures);
    }

    @Override
    public boolean onSwipeUp(int fingers, long gestureDuration, double gestureDistance) {
        return false;
    }

    @Override
    public boolean onSwipeDown(int fingers, long gestureDuration, double gestureDistance) {
        return false;
    }

    @Override
    public boolean onSwipeLeft(int fingers, long gestureDuration, double gestureDistance) {
        if (getImageBound().right > displayMetrics.widthPixels) {
            return false;//右边界大于屏幕宽，说明可以移动
        }
        position++;
        if (position > urls.length) {
            position = urls.length - 1;
            if (onSwitchListener != null) {
                onSwitchListener.onEnd();
            }
        } else {
            if (onSwitchListener != null) {
                onSwitchListener.onSwitched(this, urls[position]);
            }
            reset();
        }
        return true;
    }

    @Override
    public boolean onSwipeRight(int fingers, long gestureDuration, double gestureDistance) {
        if (getImageBound().left < 0) {
            return false;//左边界小于零，说明可以移动
        }
        position--;
        if (position < 0) {
            position = 0;
            if (onSwitchListener != null) {
                onSwitchListener.onBegin();
            }
        } else {
            if (onSwitchListener != null) {
                onSwitchListener.onSwitched(this, urls[position]);
            }
            reset();
        }
        return true;
    }

    @Override
    public boolean onPinch(int fingers, long gestureDuration, double gestureDistance) {
        return false;
    }

    @Override
    public boolean onUnpinch(int fingers, long gestureDuration, double gestureDistance) {
        return false;
    }

    @Override
    public boolean onDoubleTap(int fingers) {
        return false;
    }

    /**
     * The interface On switch listener.
     */
    public interface OnSwitchListener {
        /**
         * On begin.
         */
        void onBegin();

        /**
         * On switched.
         *
         * @param view the view
         * @param url  the url
         */
        void onSwitched(ImageView view, String url);

        /**
         * On end.
         */
        void onEnd();
    }

}
