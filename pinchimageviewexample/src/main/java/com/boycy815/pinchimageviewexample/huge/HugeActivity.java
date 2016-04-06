package com.boycy815.pinchimageviewexample.huge;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.boycy815.pinchimageview.PinchImageView;
import com.boycy815.pinchimageviewexample.R;


public class HugeActivity extends Activity {

    private PinchImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huge);
        PinchImageView pinchImageView = (PinchImageView) findViewById(R.id.pic);
        Drawable tileDrawable = new TileDrawable("file:///android_asset/card.png");
        pinchImageView.setImageDrawable(tileDrawable);
    }
}