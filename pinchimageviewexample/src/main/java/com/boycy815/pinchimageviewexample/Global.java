package com.boycy815.pinchimageviewexample;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by clifford on 16/3/8.
 */
public class Global {

    public static String[] images = new String[] {
            "http://ww2.sinaimg.cn/mw1024/6df127bfjw1esojfinxmxj20xc18gqfm.jpg"
            ,"http://ww2.sinaimg.cn/mw1024/6df127bfjw1esiveg31hwj20u00gvn18.jpg"
            ,"http://ww1.sinaimg.cn/mw1024/6df127bfjw1esivelw317j20u00gvq77.jpg"
            ,"http://ww4.sinaimg.cn/mw1024/6df127bfjw1esbuy81ovzj20ku04taah.jpg"
            ,"http://ww4.sinaimg.cn/mw1024/6df127bfjw1esaen9u5k8j20hs0nq75k.jpg"
            ,"http://ww3.sinaimg.cn/mw1024/6df127bfjw1es1ixs8uctj20hs0vkgpc.jpg"
            ,"http://ww2.sinaimg.cn/mw1024/6df127bfjw1erveujphhxj20xc18gwsy.jpg"
            ,"http://ww1.sinaimg.cn/mw1024/6df127bfjw1eroxgfbkopj216o0m543e.jpg"
            ,"http://ww4.sinaimg.cn/mw1024/6df127bfjw1erox2ywpn6j218g0xcwjp.jpg"
            ,"http://ww2.sinaimg.cn/mw1024/6df127bfjw1erovfvilebj20hs0vkacd.jpg"
            ,"http://ww3.sinaimg.cn/mw1024/6df127bfjw1erj3jcayb1j20qo0zkgrv.jpg"
            ,"http://ww3.sinaimg.cn/mw1024/6df127bfgw1erc5yiqciaj20ke0b0abg.jpg"
            ,"http://ww2.sinaimg.cn/mw1024/6df127bfjw1erbuxu8qa7j20ds0ct3zy.jpg"
            ,"http://ww2.sinaimg.cn/mw1024/6df127bfjw1er0erdh1jaj20qo0zkq9j.jpg"
            ,"http://ww3.sinaimg.cn/mw1024/6df127bfjw1er0enq1o3lj218g18gnbx.jpg"
            ,"http://ww2.sinaimg.cn/mw1024/6df127bfjw1er0gnhidtdj20hs0nogoi.jpg"
            ,"http://ww3.sinaimg.cn/mw1024/6df127bfjw1er0gqq4ff9j20hs0non07.jpg"
            ,"http://ww3.sinaimg.cn/mw1024/6df127bfjw1eqmfsasl7fj218g0p07a2.jpg"
            ,"http://ww2.sinaimg.cn/mw1024/6df127bfjw1eqmfuizagpj218g0r9dms.jpg"
    };

    public static ImageLoader getImageLoader(Context context) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        return imageLoader;
    }

    private static Map<String, Bitmap> sBitmapCache = new HashMap<String, Bitmap>();

    public static Bitmap getCacheBitmap(String key) {
        return sBitmapCache.get(key);
    }

    public static void setCacheBitmap(String key, Bitmap bitmap) {
        sBitmapCache.put(key, bitmap);
    }
}