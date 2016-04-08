package com.boycy815.pinchimageviewexample.huge;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.boycy815.pinchimageview.PinchImageView;
import com.boycy815.pinchimageviewexample.R;


public class HugeActivity extends Activity {

    private HugeImageRegionLoader mHugeImageRegionLoader;
    private PinchImageView mPinchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huge);
        mPinchImageView = (PinchImageView) findViewById(R.id.pic);
        mHugeImageRegionLoader = new HugeImageRegionLoader(getApplicationContext(), Uri.parse("file:///android_asset/card.png"), new HugeImageRegionLoader.InitCallback() {
            @Override
            public void onInit() {
                setupDrawable();
            }
        });
    }

    private void setupDrawable() {
        mPinchImageView.setImageDrawable(new TileDrawable(mHugeImageRegionLoader));
    }
}