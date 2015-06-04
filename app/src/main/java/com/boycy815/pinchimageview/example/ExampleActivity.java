package com.boycy815.pinchimageview.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.boycy815.pinchimageview.PinchImageView;
import com.boycy815.pinchimageview.R;


public class ExampleActivity extends Activity {

    private int[] mImageResIds = new int[]{
             R.drawable.test_image_0
            ,R.drawable.test_image_1
            ,R.drawable.test_image_2
            ,R.drawable.test_image_3
            ,R.drawable.test_image_4
            ,R.drawable.test_image_5
            ,R.drawable.test_image_6
            ,R.drawable.test_image_7
            ,R.drawable.test_image_8
            ,R.drawable.test_image_9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        final PhotoViewPager pager = (PhotoViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImageResIds.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PinchImageView piv = new PinchImageView(ExampleActivity.this);
                piv.setImageResource(mImageResIds[position]);
                container.addView(piv);
                return piv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                pager.setMainPinchImageView((PinchImageView) object);
            }
        });
    }
}