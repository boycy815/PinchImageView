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
             new FengNiaoImageSource("http://img2.fengniao.com/product/157/723/ceHjSq9Gi7rw.jpg", 800, 1200, "http://img2.fengniao.com/product/157_120x80/723/ceHjSq9Gi7rw.jpg", 53, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/725/ceuhOIF9Nu3gw.jpg", 823, 1200, "http://img2.fengniao.com/product/157_120x80/725/ceuhOIF9Nu3gw.jpg", 55, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/726/ce6fdSSnNDcE.jpg", 800, 1200, "http://img2.fengniao.com/product/157_120x80/726/ce6fdSSnNDcE.jpg", 53, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/728/ce5OWBfCvdUsg.jpg", 800, 1200, "http://img2.fengniao.com/product/157_120x80/728/ce5OWBfCvdUsg.jpg", 53, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/729/cet3Qy71akHxw.jpg", 920, 613, "http://img2.fengniao.com/product/157_120x80/729/cet3Qy71akHxw.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/731/ceQ1a6veUt14c.jpg", 920, 613, "http://img2.fengniao.com/product/157_120x80/731/ceQ1a6veUt14c.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/733/cenGy9PXZGD2c.jpg", 800, 1200, "http://img2.fengniao.com/product/157_120x80/733/cenGy9PXZGD2c.jpg", 53, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/735/ceJFal9LhuDcM.jpg", 920, 613, "http://img2.fengniao.com/product/157_120x80/735/ceJFal9LhuDcM.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/738/cev3KNFe3yEzc.jpg", 800, 1200, "http://img2.fengniao.com/product/157_120x80/738/cev3KNFe3yEzc.jpg", 53, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/741/cenKQCdeiDR.jpg", 800, 1200, "http://img2.fengniao.com/product/157_120x80/741/cenKQCdeiDR.jpg", 53, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/157/743/ceh3VUyMh2mrM.jpg", 800, 1200, "http://img2.fengniao.com/product/157_120x80/743/ceh3VUyMh2mrM.jpg", 53, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/687/ceBxNNZKz9FM.jpg", 1000, 667, "http://img2.fengniao.com/product/156_120x80/687/ceBxNNZKz9FM.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/701/ce8auRkGHKvZU.jpg", 1000, 667, "http://img2.fengniao.com/product/156_120x80/701/ce8auRkGHKvZU.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/702/ceHN1fr8pNK7Q.jpg", 1000, 667, "http://img2.fengniao.com/product/156_120x80/702/ceHN1fr8pNK7Q.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/703/ce3yD1kV5DD3U.jpg", 1000, 758, "http://img2.fengniao.com/product/156_120x80/703/ce3yD1kV5DD3U.jpg", 106, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/704/cecPlsMVQjdXg.jpg", 1000, 667, "http://img2.fengniao.com/product/156_120x80/704/cecPlsMVQjdXg.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/705/cerq31ixNQhNk.jpg", 1000, 667, "http://img2.fengniao.com/product/156_120x80/705/cerq31ixNQhNk.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/706/ce5CGTyLbenxU.jpg", 1000, 668, "http://img2.fengniao.com/product/156_120x80/706/ce5CGTyLbenxU.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/707/ceaaE3uUMnl8k.jpg", 1000, 667, "http://img2.fengniao.com/product/156_120x80/707/ceaaE3uUMnl8k.jpg", 120, 80)
            ,new FengNiaoImageSource("http://img2.fengniao.com/product/156/699/ceLQ6w6UxHcIw.jpg", 1000, 667, "http://img2.fengniao.com/product/156_120x80/699/ceLQ6w6UxHcIw.jpg", 120, 80)
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