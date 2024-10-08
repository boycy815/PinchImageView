package com.boycy815.pinchimageviewexample;

import android.content.Context;

import com.boycy815.pinchimageviewexample.images.ImageObject;
import com.boycy815.pinchimageviewexample.images.ImageSource;
import com.boycy815.pinchimageviewexample.images.ImageSourceImpl;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by clifford on 16/3/8.
 */
public class Global {

    private static final ImageSource[] sTestImages = new ImageSource[]{
            new ImageSourceImpl(
                    new ImageObject("https://gw.alicdn.com/imgextra/i2/2211014865515/O1CN01CovDNr1qbvFbWPB4D_!!0-item_pic.jpg_240x10000Q75.jpg", 240, 320)
                    , new ImageObject("https://gw.alicdn.com/imgextra/i2/2211014865515/O1CN01CovDNr1qbvFbWPB4D_!!0-item_pic.jpg", 1200, 1600)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://gw.alicdn.com/imgextra/i2/2211014865515/O1CN01OEkTUc1qbvFd4FSBy_!!2211014865515.jpg_240x10000Q75.jpg", 240, 320)
                    , new ImageObject("https://gw.alicdn.com/imgextra/i2/2211014865515/O1CN01OEkTUc1qbvFd4FSBy_!!2211014865515.jpg", 1200, 1600)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://gw.alicdn.com/imgextra/i1/2211014865515/O1CN01oOpn301qbvFgAhTMa_!!2211014865515.jpg_240x10000Q75.jpg", 240, 320)
                    , new ImageObject("https://gw.alicdn.com/imgextra/i1/2211014865515/O1CN01oOpn301qbvFgAhTMa_!!2211014865515.jpg", 1200, 1600)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://img.alicdn.com/imgextra/i4/2635590370/O1CN01OpR4Dg1EbVWGcLnII_!!2635590370.jpg_240x10000Q75.jpg", 240, 575)
                    , new ImageObject("https://img.alicdn.com/imgextra/i4/2635590370/O1CN01OpR4Dg1EbVWGcLnII_!!2635590370.jpg", 750, 1796)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://img.alicdn.com/imgextra/i1/2635590370/O1CN01DwIP0s1EbVVyBItDr_!!2635590370.jpg_240x10000Q75.jpg", 240, 288)
                    , new ImageObject("https://img.alicdn.com/imgextra/i1/2635590370/O1CN01DwIP0s1EbVVyBItDr_!!2635590370.jpg", 1800, 2160)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://img.alicdn.com/imgextra/i1/2635590370/O1CN016l4UE11EbVVyV8BwT_!!2635590370.jpg_240x10000Q75.jpg", 240, 288)
                    , new ImageObject("https://img.alicdn.com/imgextra/i1/2635590370/O1CN016l4UE11EbVVyV8BwT_!!2635590370.jpg", 1800, 2160)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://img.alicdn.com/imgextra/i1/2635590370/O1CN016l4UE11EbVVyV8BwT_!!2635590370.jpg_240x10000Q75.jpg", 240, 288)
                    , new ImageObject("https://img.alicdn.com/imgextra/i1/2635590370/O1CN016l4UE11EbVVyV8BwT_!!2635590370.jpg", 1800, 2160)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://img.alicdn.com/imgextra/i4/2635590370/O1CN01dgbTFB1EbVVyV6a9T_!!2635590370.jpg_240x10000Q75.jpg", 240, 288)
                    , new ImageObject("https://img.alicdn.com/imgextra/i4/2635590370/O1CN01dgbTFB1EbVVyV6a9T_!!2635590370.jpg", 1800, 2160)
            )
            , new ImageSourceImpl(
                    new ImageObject("https://img.alicdn.com/imgextra/i2/2635590370/O1CN01Kbhx6j1EbVW0LcVqW_!!2635590370.jpg_240x10000Q75.jpg", 240, 288)
                    , new ImageObject("https://img.alicdn.com/imgextra/i2/2635590370/O1CN01Kbhx6j1EbVW0LcVqW_!!2635590370.jpg", 1800, 2160)
            )
    };

    public static ImageSource getTestImage(int i) {
        if (i >= 0 && i < sTestImages.length) {
            return sTestImages[i];
        }
        return null;
    }

    public static int getTestImagesCount() {
        return sTestImages.length;
    }

    public static ImageLoader getImageLoader(Context context) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        return imageLoader;
    }
}