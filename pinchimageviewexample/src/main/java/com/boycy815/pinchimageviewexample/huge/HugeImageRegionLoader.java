package com.boycy815.pinchimageviewexample.huge;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clifford on 16/4/6.
 */
public class HugeImageRegionLoader extends ImageRegionLoader {

    private static final String FILE_PREFIX = "file://";
    private static final String ASSET_PREFIX = FILE_PREFIX + "/android_asset/";
    private static final String RESOURCE_PREFIX = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

    private int mWidth;
    private int mHeight;
    private BitmapRegionDecoder mDecoder;

    private Context mContext;
    private Uri mUri;

    private boolean mIniting;
    private boolean mRecyled;

    public HugeImageRegionLoader(Context context, Uri uri) {
        mContext = context;
        mUri = uri;
    }

    @Override
    public void init() {
        if (!mIniting) {
            mIniting = true;
            try {
                String uriString = mUri.toString();
                if (uriString.startsWith(RESOURCE_PREFIX)) {
                    Resources res;
                    String packageName = mUri.getAuthority();
                    if (mContext.getPackageName().equals(packageName)) {
                        res = mContext.getResources();
                    } else {
                        PackageManager pm = mContext.getPackageManager();
                        res = pm.getResourcesForApplication(packageName);
                    }
                    int id = 0;
                    List<String> segments = mUri.getPathSegments();
                    int size = segments.size();
                    if (size == 2 && segments.get(0).equals("drawable")) {
                        String resName = segments.get(1);
                        id = res.getIdentifier(resName, "drawable", packageName);
                    } else if (size == 1 && TextUtils.isDigitsOnly(segments.get(0))) {
                        try {
                            id = Integer.parseInt(segments.get(0));
                        } catch (NumberFormatException ignored) {
                            ignored.printStackTrace();
                        }
                    }
                    (new InitTask()).execute(mContext.getResources().openRawResource(id));
                } else if (uriString.startsWith(ASSET_PREFIX)) {
                    String assetName = uriString.substring(ASSET_PREFIX.length());
                    (new InitTask()).execute(mContext.getAssets().open(assetName, AssetManager.ACCESS_RANDOM));
                } else if (uriString.startsWith(FILE_PREFIX)) {
                    (new InitTask()).execute(new FileInputStream(uriString.substring(FILE_PREFIX.length())));
                } else {
                    InputStream inputStream = null;
                    try {
                        ContentResolver contentResolver = mContext.getContentResolver();
                        inputStream = contentResolver.openInputStream(mUri);
                        (new InitTask()).execute(inputStream);
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class InitTask extends AsyncTask<InputStream, Void, BitmapRegionDecoder> {

        @Override
        protected BitmapRegionDecoder doInBackground(InputStream... params) {
            try {
                return BitmapRegionDecoder.newInstance(params[0], false);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    params[0].close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(BitmapRegionDecoder result) {
            if (!mRecyled) {
                mDecoder = result;
                if (mDecoder != null) {
                    mWidth = mDecoder.getWidth();
                    mHeight = mDecoder.getHeight();
                    dispatchInited();
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    private Map<String, Boolean> mRecycleCommands = new HashMap<String, Boolean>();
    private Map<String, Bitmap> mLoadedBitmaps = new HashMap<String, Bitmap>();

    private String genKey(int id, int sampleSize) {
        return sampleSize + ":" + id;
    }

    @Override
    public void loadRegion(int id, int sampleSize, Rect sampleRect) {
        if(mDecoder != null) {
            String key = genKey(id, sampleSize);
            if (mRecycleCommands.containsKey(key)) {
                mRecycleCommands.remove(key);
            }
            if (mLoadedBitmaps.containsKey(key)) {
                dispatchRegionLoad(id, sampleSize, sampleRect, mLoadedBitmaps.get(key));
            } else {
                (new LoadTask(id, sampleSize, sampleRect)).execute();
            }
        }
    }

    private class LoadTask extends AsyncTask<Void, Void, Bitmap> {

        private int mId;
        private int mSampleSize;
        private Rect mSampleRect;

        public LoadTask(int id, int sampleSize, Rect sampleRect) {
            mId = id;
            mSampleSize = sampleSize;
            mSampleRect = sampleRect;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mDecoder != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = mSampleSize;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                return mDecoder.decodeRegion(mSampleRect, options);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && mDecoder != null) {
                String key = genKey(mId, mSampleSize);
                if (!mRecycleCommands.containsKey(key)) {
                    if (mLoadedBitmaps.containsKey(key)) {
                        mLoadedBitmaps.get(key).recycle();
                    }
                    mLoadedBitmaps.put(key, result);
                    dispatchRegionLoad(mId, mSampleSize, mSampleRect, result);
                }
            }
        }
    }

    @Override
    public void recycleRegion(int id, int sampleSize, Rect sampleRect) {
        if (mDecoder != null) {
            String key = genKey(id, sampleSize);
            Bitmap bitmap = mLoadedBitmaps.get(key);
            if (bitmap != null) {
                mLoadedBitmaps.remove(key);
                bitmap.recycle();
            } else {
                mRecycleCommands.put(key, true);
            }
        }
    }

    @Override
    public void recycle() {
        mRecyled = true;
        if (mDecoder != null) {
            mDecoder.recycle();
            mDecoder = null;
        }
        for (Bitmap bitmap : mLoadedBitmaps.values()) {
            bitmap.recycle();
        }
        mLoadedBitmaps.clear();
    }
}