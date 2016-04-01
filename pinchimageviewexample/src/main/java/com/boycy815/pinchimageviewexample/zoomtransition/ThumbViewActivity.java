package com.boycy815.pinchimageviewexample.zoomtransition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.boycy815.pinchimageviewexample.Global;
import com.boycy815.pinchimageviewexample.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;


public class ThumbViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_thumb_view);

        final DisplayImageOptions thumbOptions = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        final ImageLoader imageLoader = Global.getImageLoader(getApplicationContext());

        final ViewGroup root = (ViewGroup) findViewById(R.id.root);
        int l = root.getChildCount();
        for (int i = 0; i < l; i++) {
            final int fi = i;
            final ImageView thumb = (ImageView) ((ViewGroup) root.getChildAt(i)).getChildAt(0);
            final ImageViewAware thumbAware = new ImageViewAware(thumb);
            final String url = Global.getTestImage(i).getUrl(100, 100);
            imageLoader.displayImage(url, thumbAware, thumbOptions);
            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ThumbViewActivity.this, PicViewActivity.class);
                    intent.putExtra("image", Global.getTestImage(fi));
                    ImageSize targetSize = new ImageSize(thumbAware.getWidth(), thumbAware.getHeight());
                    String memoryCacheKey = MemoryCacheUtils.generateKey(url, targetSize);
                    intent.putExtra("cache_key", memoryCacheKey);
                    Rect rect = new Rect();
                    thumb.getGlobalVisibleRect(rect);
                    intent.putExtra("rect", rect);
                    intent.putExtra("scaleType", thumb.getScaleType());
                    startActivity(intent);
                }
            });
        }
    }
}