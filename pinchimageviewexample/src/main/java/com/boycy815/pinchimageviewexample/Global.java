package com.boycy815.pinchimageviewexample;

import android.content.Context;

import com.boycy815.pinchimageviewexample.images.FengNiaoImageSource;
import com.boycy815.pinchimageviewexample.images.ImageSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by clifford on 16/3/8.
 */
public class Global {

    private static ImageSource[] sTestImages = new ImageSource[] {
             new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/a3d12f4355dde74483e323022866c27c.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/821ee769c9965094bd2d4d3567253eb4.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/a6c04be8a7c070f90707ee346180e664.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/18925552b84b6462f041fdac7533111b.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/dfd2eb58639be0c55be4802b99b50fb6.jpg", 3840, 5760)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/70ef420ace8179b68523701d784e772c.jpg", 3840, 5760)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/82acb0052d63633324da3aa2eddf9610.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/f37e7606bc900a7b0a4dbf42f82d8147.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/f48fc28caecc588fadf5ab71d5a3215c.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/fcaaa048799d44f48ce015d11ba6e565.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/110401232c7f779e647251b7445a0635.jpg", 5760, 3840)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/2263e35507b57b5c1ddcd989d45c05b7.jpg", 3840, 5760)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/ac5ce73f98a43c7c9263974a93c1c39b.jpg", 3840, 5760)
            ,new FengNiaoImageSource("https://bbs.qn.img-space.com/201803/1/c2776f84eef6f0350666b1b37d87d9d3.jpg", 5760, 3840)
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