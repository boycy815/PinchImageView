package com.boycy815.pinchimageviewexample.huge;

import android.app.Activity;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import com.boycy815.pinchimageview.PinchImageView;
import com.boycy815.pinchimageviewexample.R;


public class HugeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huge);
        final PinchImageView pinchImageView = (PinchImageView) findViewById(R.id.pic);
        pinchImageView.post(new Runnable() {
            @Override
            public void run() {
                final TileDrawable tileDrawable = new TileDrawable();
                tileDrawable.setInitCallback(new TileDrawable.InitCallback() {
                    @Override
                    public void onInit() {
                        pinchImageView.setImageDrawable(tileDrawable);
                    }
                });
                tileDrawable.init(new HugeImageRegionLoader(HugeActivity.this, Uri.parse("file:///android_asset/card.png")), new Point(pinchImageView.getWidth(), pinchImageView.getHeight()));
            }
        });
    }
}