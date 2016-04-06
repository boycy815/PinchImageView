package com.boycy815.pinchimageviewexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.boycy815.pinchimageviewexample.huge.HugeActivity;
import com.boycy815.pinchimageviewexample.withviewpager.PagerActivity;
import com.boycy815.pinchimageviewexample.zoomtransition.ThumbViewActivity;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class IndexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        findViewById(R.id.btn_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, PagerActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, ThumbViewActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_huge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, HugeActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.view_github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/boycy815/PinchImageView")));
            }
        });
    }
}