package com.imoonx.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import com.imoonx.common.base.BaseActivity;
import com.imoonx.image.bean.SelectOptions;
import com.imoonx.image.ui.CropLayout;
import com.imoonx.util.XIOUtil;
import com.imoonx.util.XLog;

import java.io.FileOutputStream;

/**
 * 图片裁剪activity
 */
public class CropActivity extends BaseActivity {

    private CropLayout mCropLayout;
    private static SelectOptions mOption;

    public static void show(Fragment fragment, SelectOptions options) {
        Intent intent = new Intent(fragment.getActivity(), CropActivity.class);
        mOption = options;
        fragment.startActivityForResult(intent, 0x04);
    }

    @Override
    public int getLayoutID() {
        return R.layout.image_activity_crop;
    }

    @Override
    public void initWidget() {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mCropLayout = (CropLayout) findViewById(R.id.cropLayout);
    }

    @Override
    public void initData() {
        super.initData();
        getImageLoader().load(mOption.getSelectedImages().get(0)).into(mCropLayout.getImageView());
        mCropLayout.setCropWidth(mOption.getCropWidth());
        mCropLayout.setCropHeight(mOption.getCropHeight());
        mCropLayout.start();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_crop) {
            Bitmap bitmap = null;
            FileOutputStream os = null;
            try {
                bitmap = mCropLayout.cropBitmap();
                String fileName = System.currentTimeMillis() + ".jpg";
                String path = getFilesDir() + "/" + fileName;
                os = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();

                Intent intent = new Intent();
                intent.putExtra("crop_path", path);
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                XLog.e(CropActivity.class, e);
            } finally {
                if (bitmap != null)
                    bitmap.recycle();
                XIOUtil.close(os);
            }
        } else if (id == R.id.tv_cancel) {
            finish();
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

    @Override
    protected void onDestroy() {
        mOption = null;
        super.onDestroy();
    }
}
