package com.boycy815.pinchimageviewexample;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.boycy815.pinchimageview.PinchImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.LinkedList;


public class PagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        final LinkedList<PinchImageView> viewCache = new LinkedList<PinchImageView>();

        final PinchImageViewPager pager = (PinchImageViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Global.images.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PinchImageView piv;
                if (viewCache.size() > 0) {
                    piv = viewCache.remove();
                    piv.reset();
                } else {
                    piv = new PinchImageView(PagerActivity.this);
                }
                DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).build();
                Global.getImageLoader(getApplicationContext()).displayImage(Global.images[position], piv, options);
                container.addView(piv);
                return piv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                PinchImageView piv = (PinchImageView) object;
                container.removeView(piv);
                viewCache.add(piv);
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                pager.setMainPinchImageView((PinchImageView) object);
            }
        });
    }
}