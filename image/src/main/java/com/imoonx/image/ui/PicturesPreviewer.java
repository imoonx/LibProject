package com.imoonx.image.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.imoonx.image.R;
import com.imoonx.image.SelectImageActivity;
import com.imoonx.image.adapter.SelectImageAdapter;
import com.imoonx.image.bean.SelectOptions;
import com.imoonx.image.interf.SelectImageCallBack;

import java.util.Map;

/**
 * @author 36238
 * TweetPicturesPreviewer
 * 提供图片预览/图片操作 返回选中图片等功能
 * 2016年12月28日 下午4:53:34
 */

public class PicturesPreviewer extends RecyclerView implements SelectImageAdapter.Callback, SelectImageAdapter.OnImageItemClickListener {

    private SelectImageAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;
    private SelectImageCallBack mSelectImageCallBack;

    private static final int IMAGE_COW_COUNT = 3;
    private static final int IMAGE_MAX_SIZE = 9;
    private int mPictureMaxSize;
    private OnPictureItemClickListener mOnPictureItemClickListener;

    public SelectImageCallBack getmSelectImageCallBack() {
        return mSelectImageCallBack;
    }

    public void setmSelectImageCallBack(SelectImageCallBack mSelectImageCallBack) {
        this.mSelectImageCallBack = mSelectImageCallBack;
    }

    public OnPictureItemClickListener getOnPictureItemClickListener() {
        return mOnPictureItemClickListener;
    }

    public void setOnPictureItemClickListener(OnPictureItemClickListener listener) {
        this.mOnPictureItemClickListener = listener;
    }

    public PicturesPreviewer(Context context) {
        // super(context);
        this(context, null);
    }

    public PicturesPreviewer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        // init(context);
    }

    public PicturesPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @SuppressLint("InlinedApi")
    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PicturesPreviewer, defStyle, 0);
        int mPictureCount = a.getInteger(R.styleable.PicturesPreviewer_picture_count, IMAGE_COW_COUNT);
        mPictureMaxSize = a.getInteger(R.styleable.PicturesPreviewer_picture_max_size, IMAGE_MAX_SIZE);
        int mPictureId = a.getResourceId(R.styleable.PicturesPreviewer_picture_def_id, R.drawable.image_ic_add);
        boolean mIsNeedPicture = a.getBoolean(R.styleable.PicturesPreviewer_is_need_picture, true);
        a.recycle();

        mImageAdapter = new SelectImageAdapter(this, mPictureMaxSize, mPictureId, mIsNeedPicture, this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), mPictureCount);
        this.setLayoutManager(layoutManager);
        this.setAdapter(mImageAdapter);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ItemTouchHelper.Callback callback = new PicturesPreviewerItemTouchCallback(mImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public void set(String[] paths) {
        mImageAdapter.clear();
        for (String path : paths) {
            mImageAdapter.add(path);
        }
        mImageAdapter.notifyDataSetChanged();
    }

    public void set(String[] paths, boolean isNotDelete) {
        mImageAdapter.clear();
        mImageAdapter.setNotDelete(isNotDelete);
        for (String path : paths) {
            mImageAdapter.add(path);
        }
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreClick() {
        if (mSelectImageCallBack == null) {
            SelectImageActivity.show(getContext(), new SelectOptions.Builder().setHasCam(true)
                    .setSelectCount(mPictureMaxSize)
                    .setSelectedImages(mImageAdapter.getPaths())
                    .setCallback(new SelectImageCallBack() {
                        @Override
                        public void doSelected(String[] images) {
                            set(images);
                        }

                        @Override
                        public <T> void doSelected(Map<String, T> map) {

                        }

                        @Override
                        public void doEmpty(int isImage) {

                        }
                    }).build());
        } else {
            SelectImageActivity.show(getContext(), new SelectOptions.Builder().setHasCam(true)
                    .setSelectCount(mPictureMaxSize)
                    .setSelectedImages(mImageAdapter.getPaths())
                    .setCallback(mSelectImageCallBack).build());
        }

    }

    @Override
    public RequestManager getImgLoader() {
        if (mCurImageLoader == null) {
            mCurImageLoader = Glide.with(getContext());
        }
        return mCurImageLoader;
    }

    @Override
    public void onStartDrag(ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public String[] getPaths() {
        return mImageAdapter.getPaths();
    }

    public void clearImage() {
        mImageAdapter.clear();
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEmpty(int count) {
        if (mSelectImageCallBack != null) {
            mSelectImageCallBack.doEmpty(count);
        }
    }

    @Override
    public void imageClick(View view) {
        if (null != view && null != mOnPictureItemClickListener) {
            int position = getChildAdapterPosition(view);
            mOnPictureItemClickListener.imageClick(this, position, getPaths());
        }
    }

    public interface OnPictureItemClickListener {
        void imageClick(RecyclerView recyclerView, int position, String[] images);
    }

}
