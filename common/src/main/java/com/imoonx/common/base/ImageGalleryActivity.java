package com.imoonx.common.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.imoonx.common.R;
import com.imoonx.common.manager.AppOperator;
import com.imoonx.common.ui.ImagePreviewView;
import com.imoonx.common.ui.PicturesCompressor;
import com.imoonx.common.ui.PreviewerViewPager;
import com.imoonx.http.builder.GetBuilder;
import com.imoonx.http.callback.FileCallBack;
import com.imoonx.http.request.RequestCall;
import com.imoonx.util.Toast;

import java.io.File;
import java.util.concurrent.Future;

import okhttp3.Call;

/**
 * 图片预览
 */
public class ImageGalleryActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public final static String DEFAULT_SAVE_FILE_PATH = Environment.getExternalStorageDirectory()
            + File.separator + "imoonx" + File.separator + "download" + File.separator;
    public static final String KEY_IMAGE = "images";
    public static final String KEY_COOKIE = "cookie";
    public static final String KEY_POSITION = "position";
    public static final String KEY_NEED_SAVE = "save";
    private PreviewerViewPager mImagePager;
    private TextView mIndexText;
    private String[] mImageSources;
    private int mCurPosition;
    private boolean mNeedSaveLocal;

    public static void show(Context context, String images) {
        show(context, images, true);
    }

    public static void show(Context context, String images, boolean needSaveLocal) {
        if (images == null)
            return;
        show(context, new String[]{images}, 0, needSaveLocal);
    }

    public static void show(Context context, String images, boolean needSaveLocal, boolean needCookie) {
        if (images == null)
            return;
        show(context, new String[]{images}, 0, needSaveLocal, needCookie);
    }

    public static void show(Context context, String[] images, int position) {
        show(context, images, position, true);
    }

    public static void show(Context context, String[] images, int position, boolean needSaveLocal) {
        show(context, images, position, needSaveLocal, false);
    }

    public static void show(Context context, String[] images, int position, boolean needSaveLocal, boolean needCookie) {
        if (images == null || images.length == 0)
            return;
        Intent intent = new Intent(context, ImageGalleryActivity.class);
        intent.putExtra(KEY_IMAGE, images);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(KEY_NEED_SAVE, needSaveLocal);
        intent.putExtra(KEY_COOKIE, needCookie);
        context.startActivity(intent);
    }

    @Override
    public boolean initBundle(Bundle bundle) {
        mImageSources = bundle.getStringArray(KEY_IMAGE);
        mCurPosition = bundle.getInt(KEY_POSITION, 0);
        mNeedSaveLocal = bundle.getBoolean(KEY_NEED_SAVE, true);
        return mImageSources != null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.image_activity_image_gallery;
    }

    @Override
    public void initWidget() {
        setGone();
        mImagePager = findViewById(R.id.vp_image);
        mIndexText = findViewById(R.id.tv_index);
        if (mNeedSaveLocal) {
            findViewById(R.id.image_down).setVisibility(View.VISIBLE);
            findViewById(R.id.image_down).setOnClickListener(this);
        }
        mImagePager.addOnPageChangeListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        int len = mImageSources.length;
        if (mCurPosition < 0 || mCurPosition >= len)
            mCurPosition = 0;

        // If only one, we not need the text to show
        if (len == 1)
            mIndexText.setVisibility(View.GONE);

        mImagePager.setAdapter(new ViewPagerAdapter());
        mImagePager.setCurrentItem(mCurPosition);
        // First we call to init the TextView
        onPageSelected(mCurPosition);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_down) {
            if (null != mImageSources && !TextUtils.isEmpty(mImageSources[mCurPosition])) {
                GetBuilder builder = new GetBuilder();
                builder.url(mImageSources[mCurPosition]);
                RequestCall build = builder.build();
                build.execute(new FileCallBack(DEFAULT_SAVE_FILE_PATH, mImageSources[mCurPosition].substring
                        (mImageSources[mCurPosition].lastIndexOf("/") + 1)) {
                    @Override
                    public void progress(float progress) {
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(File response) {
                        Toast.showToast("保存成功");
                    }
                });
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurPosition = position;
        mIndexText.setText(String.format("%s/%s", (position + 1), mImageSources.length));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private Point mDisplayDimens;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @SuppressWarnings("deprecation")
    private synchronized Point getDisplayDimens() {
        if (mDisplayDimens != null) {
            return mDisplayDimens;
        }
        Point displayDimens;
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (null == windowManager)
            return mDisplayDimens;
        Display display = windowManager.getDefaultDisplay();
        displayDimens = new Point();
        display.getSize(displayDimens);

        // In this we can only get 85% width and 60% height
        // displayDimens.y = (int) (displayDimens.y * 0.60f);
        // displayDimens.x = (int) (displayDimens.x * 0.85f);

        mDisplayDimens = displayDimens;
        return mDisplayDimens;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class ViewPagerAdapter extends PagerAdapter implements ImagePreviewView.OnReachBorderListener {

        private View.OnClickListener mFinishClickListener;

        @Override
        public int getCount() {
            return mImageSources.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(
                    R.layout.image_lay_gallery_page_item_contener, container, false);
            ImagePreviewView previewView = view.findViewById(R.id.iv_preview);
            previewView.setOnReachBorderListener(this);
            ProgressBar loading = view.findViewById(R.id.loading);
            ImageView defaultView = view.findViewById(R.id.iv_default);

            loadImage(mImageSources[position], previewView, defaultView, loading);
            previewView.setOnClickListener(getListener());
            container.addView(view);
            return view;
        }

        private View.OnClickListener getListener() {
            if (mFinishClickListener == null) {
                mFinishClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                };
            }
            return mFinishClickListener;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onReachBorder(boolean isReached) {
            mImagePager.isInterceptable(isReached);
        }

        private <T> void loadImage(final T urlOrPath, final ImageView previewView, final ImageView defaultView,
                                   final ProgressBar loading) {

            loadImageDoDownAndGetOverrideSize(urlOrPath, new DoOverrideSizeCallback() {
                @SuppressWarnings("rawtypes")
                @Override
                public void onDone(int overrideW, int overrideH, boolean isTrue) {
                    DrawableRequestBuilder builder = getImageLoader()
                            .load(urlOrPath)
                            .listener(new RequestListener<T, GlideDrawable>() {
                                @Override
                                public boolean onException(
                                        Exception e,
                                        T model,
                                        Target<GlideDrawable> target,
                                        boolean isFirstResource) {
                                    if (e != null)
                                        e.printStackTrace();
                                    loading.setVisibility(View.GONE);
                                    defaultView
                                            .setVisibility(View.VISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(
                                        GlideDrawable resource,
                                        T model,
                                        Target<GlideDrawable> target,
                                        boolean isFromMemoryCache,
                                        boolean isFirstResource) {
                                    loading.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE);

                    // If download or get option error we not set
                    // override
                    if (isTrue && overrideW > 0 && overrideH > 0) {
                        builder = builder
                                .override(overrideW, overrideH);
                    }

                    builder.into(previewView);
                }
            });
        }

        private <T> void loadImageDoDownAndGetOverrideSize(final T urlOrPath, final DoOverrideSizeCallback callback) {
            // In this save max image size is source
            final Future<File> future = getImageLoader().load(urlOrPath)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File sourceFile = future.get();

                        BitmapFactory.Options options = PicturesCompressor.createOptions();
                        // First decode with inJustDecodeBounds=true to check
                        // dimensions
                        options.inJustDecodeBounds = true;
                        // First decode with inJustDecodeBounds=true to check
                        // dimensions
                        BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), options);

                        int width = options.outWidth;
                        int height = options.outHeight;

                        PicturesCompressor.resetOptions(options);

                        if (width > 0 && height > 0) {
                            // Get Screen
                            final Point point = getDisplayDimens();

                            // This max size
                            final int maxLen = Math.min(
                                    Math.min(point.y, point.x) * 5, 1366 * 3);

                            // Init override size
                            final int overrideW, overrideH;

                            if ((width / (float) height) > (point.x / (float) point.y)) {
                                overrideH = Math.min(height, point.y);
                                overrideW = Math.min(width, maxLen);
                            } else {
                                overrideW = Math.min(width, point.x);
                                overrideH = Math.min(height, maxLen);
                            }

                            // Call back on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(overrideW, overrideH, true);
                                }
                            });
                        } else {
                            // Call back on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(0, 0, false);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Call back on main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDone(0, 0, false);
                            }
                        });
                    }
                }
            });
        }

    }

    interface DoOverrideSizeCallback {
        void onDone(int overrideW, int overrideH, boolean isTrue);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
