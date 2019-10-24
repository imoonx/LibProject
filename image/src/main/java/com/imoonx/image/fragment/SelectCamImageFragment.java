package com.imoonx.image.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.imoonx.common.base.BaseFragment;
import com.imoonx.common.base.BaseRecyclerAdapter;
import com.imoonx.common.base.ImageGalleryActivity;
import com.imoonx.common.manager.AppOperator;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.image.CropActivity;
import com.imoonx.image.ImageUtil;
import com.imoonx.image.R;
import com.imoonx.image.adapter.ImageAdapter;
import com.imoonx.image.bean.Image;
import com.imoonx.image.bean.SelectOptions;
import com.imoonx.image.interf.ImageLoaderListener;
import com.imoonx.image.interf.SelectImageContractListener;
import com.imoonx.image.ui.SpaceGridItemDecoration;
import com.imoonx.util.Res;
import com.imoonx.util.Toast;
import com.imoonx.util.XLog;
import com.imoonx.util.XUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 只是用相机
 */
public class SelectCamImageFragment extends BaseFragment implements SelectImageContractListener.View, View.OnClickListener,
        ImageLoaderListener, BaseRecyclerAdapter.OnItemClickListener {

    private RecyclerView mContentView;
    //    private ImageView mSelectFolderIcon;
    private Button mDoneView;
    private Button mPreviewView;

    private EmptyLayout mErrorLayout;

    private ImageAdapter mImageAdapter;

    private List<Image> mSelectedImage;

    private String mCamImageName;
    private LoaderListener mCursorLoader = new LoaderListener();

    private SelectImageContractListener.Operator mOperator;
    private static SelectOptions mOption;

    public static SelectCamImageFragment newInstance(SelectOptions options) {
        SelectCamImageFragment fragment = new SelectCamImageFragment();
        mOption = options;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        // activity 传递
        this.mOperator = (SelectImageContractListener.Operator) context;
        this.mOperator.setDataView(this);
        super.onAttach(context);
    }

    @Override
    public int getLayoutID() {
        return R.layout.image_fragment_select_image;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_preview) {
            if (mSelectedImage.size() > 0) {
                ImageGalleryActivity.show(getActivity(), ImageUtil.toArray(mSelectedImage), 0, false);
            }
        } else if (id == R.id.btn_done) {
            onSelectComplete();
        }
    }

    @Override
    public void initWidget(View view) {

        mContentView = (RecyclerView) view.findViewById(R.id.rv_image);

        mDoneView = (Button) view.findViewById(R.id.btn_done);
        mPreviewView = (Button) view.findViewById(R.id.btn_preview);
        mErrorLayout = (EmptyLayout) view.findViewById(R.id.error_layout);

        mPreviewView.setOnClickListener(this);
        mDoneView.setOnClickListener(this);

        mContentView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mContentView.addItemDecoration(new SpaceGridItemDecoration(Res.dip2px(1)));
        mImageAdapter = new ImageAdapter(getContext(), this);
        // mImageFolderAdapter = new ImageFolderAdapter(getActivity());
        // mImageFolderAdapter.setLoader(this);
        mContentView.setAdapter(mImageAdapter);
        mContentView.setItemAnimator(null);
        mImageAdapter.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getLoaderManager().initLoader(0, null, mCursorLoader);
            }
        });
    }

    @Override
    public void initData() {
        mSelectedImage = new ArrayList<>();
        if (mOption.getSelectCount() > 1 && mOption.getSelectedImages() != null) {
            List<String> images = mOption.getSelectedImages();
            for (String s : images) {
                // checkShare file exists
                if (s != null && new File(s).exists()) {
                    Image image = new Image();
                    image.setSelect(true);
                    image.setPath(s);
                    mSelectedImage.add(image);
                }
            }
        }
        getLoaderManager().initLoader(0, null, mCursorLoader);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        if (mOption.isHasCam()) {
            if (position != 0) {
                handleSelectChange(position);
            } else {
                if (mSelectedImage.size() < mOption.getSelectCount()) {
                    mOperator.requestCamera();
                } else {
                    Toast.showToast("最多只能选择 " + mOption.getSelectCount() + " 张图片");
                }
            }
        } else {
            handleSelectChange(position);
        }
    }

    private void handleSelectSizeChange(int size) {
        if (size > 0) {
            mPreviewView.setEnabled(true);
            mDoneView.setEnabled(true);
            mDoneView.setText(String.format("%s(%s)", Res.getString(R.string.image_select_opt_done), size));
        } else {
            mPreviewView.setEnabled(false);
            mDoneView.setEnabled(false);
            mDoneView.setText(Res.getString(R.string.image_select_opt_done));
        }
    }

    private void handleSelectChange(int position) {
        Image image = mImageAdapter.getItem(position);
        // 如果是多选模式
        final int selectCount = mOption.getSelectCount();

        if (selectCount > 1) {
            if (image.isSelect()) {
                image.setSelect(false);
                mSelectedImage.remove(image);
                mImageAdapter.updateItem(position);
            } else {
                if (mSelectedImage.size() == selectCount) {
                    Toast.showToast("最多只能选择 " + selectCount + " 张照片");
                } else {
                    image.setSelect(true);
                    mSelectedImage.add(image);
                    mImageAdapter.updateItem(position);
                }
            }
            handleSelectSizeChange(mSelectedImage.size());
        } else {
            mSelectedImage.add(image);
            handleResult();
        }
    }

    // 通过接口传递数据
    private void handleResult() {
        if (mSelectedImage.size() != 0) {
            if (mOption.isCrop()) {
                List<String> selectedImage = mOption.getSelectedImages();
                selectedImage.clear();
                selectedImage.add(mSelectedImage.get(0).getPath());
                mSelectedImage.clear();
                CropActivity.show(this, mOption);
            } else {
                mOption.getCallback().doSelected(ImageUtil.toArray(mSelectedImage));
                if (null != getActivity())
                    getActivity().finish();
            }
        }
    }

    /**
     * 完成选择
     */
    public void onSelectComplete() {
        handleResult();
    }

    /**
     * 申请相机权限成功
     */
    @Override
    public void onOpenCameraSuccess() {
        toOpenCamera();
    }

    @Override
    public void onCameraPermissionDenied() {

    }

    /**
     * 打开相机
     */
    private void toOpenCamera() {
        // 判断是否挂载了SD卡
        mCamImageName = null;
        String savePath = "";
        if (XUtil.hasSDCard()) {
            savePath = XUtil.getCameraPath() + "/hn_zhixiao_manager/";
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            Toast.showToast("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        mCamImageName = XUtil.getSaveImageFullName();
        File out = new File(savePath, mCamImageName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(out);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 0x03);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, out.getAbsolutePath());
            Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 0x03);
        }
    }

    /**
     * 拍照完成通知系统添加照片
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case 0x03:
                    if (mCamImageName == null)
                        return;
                    Uri localUri = Uri.fromFile(new File(XUtil.getCameraPath() + "/hn_zhixiao_manager/" + this.mCamImageName));
                    Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                    if (null != getActivity())
                        getActivity().sendBroadcast(localIntent);
                    break;
                case 0x04:
                    if (data == null)
                        return;
                    mOption.getCallback().doSelected(new String[]{data.getStringExtra("crop_path")});
                    if (null != getActivity())
                        getActivity().finish();
                    break;
            }
        }
    }

    @Override
    public void displayImage(final ImageView iv, final String path) {
        // Load image
        getImageLoader().load(path).asBitmap().centerCrop().into(iv);
    }

    private class LoaderListener implements
            LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0) {
                // 数据库光标加载器
                XLog.e(SelectCamImageFragment.class, "123456789");
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            if (data != null) {
                AppOperator.runOnThread(new Runnable() {
                    public void run() {
                        final ArrayList<Image> images = new ArrayList<>();

                        int count = data.getCount();
                        if (count > 0) {
                            data.moveToFirst();
                            do {
                                String path = data.getString(data
                                        .getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                                if (path.contains("hn_zhixiao_manager")) {
                                    XLog.e(SelectCamImageFragment.class, "=" + path);

                                    String name = data.getString(data
                                            .getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                                    long dateTime = data.getLong(data
                                            .getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                                    int id = data.getInt(data
                                            .getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                                    String thumbPath = data.getString(data
                                            .getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                                    String bucket = data.getString(data
                                            .getColumnIndexOrThrow(IMAGE_PROJECTION[5]));

                                    Image image = new Image();
                                    image.setPath(path);
                                    image.setName(name);
                                    image.setDate(dateTime);
                                    image.setId(id);
                                    image.setThumbPath(thumbPath);
                                    image.setFolderName(bucket);
                                    images.add(image);
                                    if (mCamImageName != null) {
                                        if (mCamImageName.equals(image
                                                .getName())) {
                                            image.setSelect(true);
                                            mSelectedImage.add(image);
                                        }
                                    }
                                    if (mSelectedImage.size() > 0) {
                                        for (Image i : mSelectedImage) {
                                            if (i.getPath().equals(
                                                    image.getPath())) {
                                                image.setSelect(true);
                                            }
                                        }
                                    }
                                }
                            } while (data.moveToNext());
                        }
                        mRoot.post(new Runnable() {
                            public void run() {
                                addImagesToAdapter(images);
                                if (mSelectedImage.size() > 0) {
                                    List<Image> rs = new ArrayList<Image>();
                                    for (Image i : mSelectedImage) {
                                        File f = new File(i.getPath());
                                        if (!f.exists()) {
                                            rs.add(i);
                                        }
                                    }
                                    mSelectedImage.removeAll(rs);
                                }
                                if ((mOption.getSelectCount() == 1)
                                        && (mCamImageName != null)) {
                                    handleResult();
                                }

                                handleSelectSizeChange(mSelectedImage.size());
                                mErrorLayout.setErrorType(4);
                            }
                        });
                    }
                });
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void addImagesToAdapter(ArrayList<Image> images) {
        mImageAdapter.clear();
        if (mOption.isHasCam()) {
            Image cam = new Image();
            mImageAdapter.addItem(cam);
        }
        mImageAdapter.addAll(images);
    }
}
