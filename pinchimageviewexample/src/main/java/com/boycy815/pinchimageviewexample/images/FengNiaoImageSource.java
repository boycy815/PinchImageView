package com.boycy815.pinchimageviewexample.images;

import android.graphics.RectF;
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

    private String mOriginUrl;
    private int mOriginWidth;
    private int mOriginHeight;

    public FengNiaoImageSource(String originUrl, int originWidth, int originHeight) {
        mOriginUrl = originUrl;
        mOriginWidth = originWidth;
        mOriginHeight = originHeight;
    }

    @Override
    public ImageObject getThumb(int width, int height) {
        ImageObject imageObject = new ImageObject();
        imageObject.url = mOriginUrl + "?imageView2/2/w/" + width + "/h/" + height;
        RectF result = new RectF();
        PinchImageView.MathUtils.calculateScaledRectInContainer(new RectF(0, 0, width, height), mOriginWidth, mOriginHeight, ImageView.ScaleType.FIT_CENTER, result);
        imageObject.width = (int) result.width();
        imageObject.height = (int) result.height();
        return imageObject;
    }

    @Override
    public ImageObject getOrigin() {
        ImageObject imageObject = new ImageObject();
        imageObject.url = mOriginUrl;
        imageObject.width = mOriginWidth;
        imageObject.height = mOriginHeight;
        return imageObject;
    }
}