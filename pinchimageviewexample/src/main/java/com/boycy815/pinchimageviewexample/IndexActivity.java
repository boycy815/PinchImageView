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
    }
}