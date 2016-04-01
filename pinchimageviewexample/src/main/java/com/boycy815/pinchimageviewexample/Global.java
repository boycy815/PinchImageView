package com.boycy815.pinchimageviewexample;

import android.content.Context;
import android.graphics.Bitmap;

import com.boycy815.pinchimageviewexample.images.FengNiaoImageSource;
import com.boycy815.pinchimageviewexample.images.ImageSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by clifford on 16/3/8.
 */
public class Global {

    private static ImageSource[] sTestImages = new ImageSource[] {
             new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT34yIS3XIAAGvCCMjfnoAAP8ZwHTjjoAAa8g566.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT35eIC6AWAAN9cEMMMQsAAP8ZwLLBvoAA32I562.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT34qIJMntAAHM2HI3G_wAAP8ZwHDMOUAAczw239.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT35KIDWtMAAHgwQp3DEEAAP8ZwJv_ZUAAeDZ609.jpg", 950, 633)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT35KIb_Q_AAEunpNOlukAAP8ZwJ-m4wAAS62276.jpg", 427, 640)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT346IQ-KxAAYmpx0z07EAAP8ZwI0C20ABia_647.jpg", 919, 613)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT34-IO-EeAAJpdd7b8uoAAP8ZwJE-SgAAmmN623.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT35SIIc8yAAU0VnR12yIAAP8ZwKQlhoABTRu859.jpg", 920, 613)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT35SILHlOAAKqd9iBUTAAAP8ZwKgNhYAAqqP126.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT35SIeSLUAAPax0D_3bMAAP8ZwKnGl0AA9rf268.jpg", 920, 613)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT35iIfug3AAg6bEu4SKEAAP8ZwLXTH4ACDqE284.jpg", 489, 734)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT35CIPbr-AALALZZpIMoAAP8ZwJWhgEAAsBF567.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT35WIBE24AAWPmzfoYl0AAP8ZwKxqkIABY-z251.jpg", 919, 625)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT35aIccnmAAFR2yc86jYAAP8ZwK5VgsAAVHz519.jpg", 950, 577)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT35aIdxleAAKaEqcSgaIAAP8ZwK9yVwAApoq028.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT35iILh8vAAXn5BT-E38AAP8ZwLRTKgABef8184.jpg", 489, 734)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT35KIfBVQAADoduSDMGAAAP8ZwJr4_cAAOiO276.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT35GIdVLgAANThREJFDsAAP8ZwJky6MAA1Od301.jpg", 950, 634)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4rFbT342ILO1kAAIkYOJUyRgAAP8ZwHnWHsAAiR4224.jpg", 950, 687)
            ,new FengNiaoImageSource("/g1/M00/06/51/Cg-4q1bT34-IBtFHAAJVg76zRMgAAP8ZwJCeycAAlWb315.jpg", 950, 634)
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