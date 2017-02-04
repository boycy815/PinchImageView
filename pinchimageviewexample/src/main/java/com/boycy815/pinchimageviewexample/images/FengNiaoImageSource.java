package com.boycy815.pinchimageviewexample.images;

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
    private String mThumbUrl;
    private int mThumbWidth;
    private int mThumbHeight;

    public FengNiaoImageSource(String originUrl, int originWidth, int originHeight, String thumbUrl, int thumbWidth, int thumbHeight) {
        mOriginUrl = originUrl;
        mOriginWidth = originWidth;
        mOriginHeight = originHeight;
        mThumbUrl = thumbUrl;
        mThumbWidth = thumbWidth;
        mThumbHeight = thumbHeight;
    }

    @Override
    public ImageObject getThumb(int width, int height) {
        ImageObject imageObject = new ImageObject();
        imageObject.url = mThumbUrl;
        imageObject.width = mThumbWidth;
        imageObject.height = mThumbHeight;
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