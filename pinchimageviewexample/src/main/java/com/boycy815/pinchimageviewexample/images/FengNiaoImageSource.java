package com.boycy815.pinchimageviewexample.images;

import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;
import android.widget.ImageView;

import com.boycy815.pinchimageview.PinchImageView;

/**
 * 蜂鸟网的图片素材
 *
 * http://www.fengniao.com/
 *
 * @author clifford
 */
public class FengNiaoImageSource implements ImageSource {

    private String mMediaId;
    private int mOriginWidth;
    private int mOriginHeight;

    public FengNiaoImageSource(String mediaId, int originWidth, int originHeight) {
        mMediaId = mediaId;
        mOriginWidth = originWidth;
        mOriginHeight = originHeight;
    }

    @Override
    public String getUrl(int width, int height) {
        if (!TextUtils.isEmpty(mMediaId)) {
            return "http://cms.fn.img-space.com/t_s" + width + "x" + height + mMediaId;
        }
        return null;
    }

    @Override
    public Point getSize(int requestWidth, int requestHeight) {
        RectF container = new RectF(0 ,0, requestWidth, requestHeight);
        RectF rectResult = new RectF();
        PinchImageView.MathUtils.calculateScaledRectInContainer(container, mOriginWidth, mOriginHeight, ImageView.ScaleType.FIT_CENTER, rectResult);
        return new Point(Math.round(rectResult.width()), Math.round(rectResult.height()));
    }

    @Override
    public int getOriginWidth() {
        return mOriginWidth;
    }

    @Override
    public int getOriginHeight() {
        return mOriginHeight;
    }
}
