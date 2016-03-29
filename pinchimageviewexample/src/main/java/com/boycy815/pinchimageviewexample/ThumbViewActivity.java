package com.boycy815.pinchimageviewexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class ThumbViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_thumb_view);

        final ViewGroup root = (ViewGroup) findViewById(R.id.root);
        int l = root.getChildCount();
        for (int i = 0; i < l; i++) {
            final int fi = i;
            Global.getImageLoader(getApplicationContext()).loadImage(Global.images[fi], new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Global.setCacheBitmap(Global.images[fi], bitmap);
                    final ImageView thumb = (ImageView) ((ViewGroup) root.getChildAt(fi)).getChildAt(0);
                    thumb.setImageBitmap(bitmap);
                    thumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ThumbViewActivity.this, PicViewActivity.class);
                            intent.putExtra("url", Global.images[fi]);
                            Rect rect = new Rect();
                            thumb.getGlobalVisibleRect(rect);
                            intent.putExtra("rect", rect);
                            intent.putExtra("scaleType", thumb.getScaleType());
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }
    }
}