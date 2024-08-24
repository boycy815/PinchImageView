package com.boycy815.pinchimageviewexample.images;

public class ImageSourceImpl implements ImageSource {

    private final ImageObject mThumb;
    private final ImageObject mOrigin;

    public ImageSourceImpl(ImageObject t, ImageObject o) {
        mThumb = t;
        mOrigin = o;
    }

    @Override
    public ImageObject getThumb() {
        return mThumb;
    }

    @Override
    public ImageObject getOrigin() {
        return mOrigin;
    }
}
