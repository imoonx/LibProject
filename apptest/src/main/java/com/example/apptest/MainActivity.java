package com.example.apptest;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.imoonx.common.permissions.EasyPermissions;
import com.imoonx.common.permissions.PermissionCallbacks;
import com.imoonx.http.builder.GetBuilder;
import com.imoonx.http.callback.FileCallBack;
import com.imoonx.http.request.RequestCall;
import com.imoonx.util.XLog;

import java.io.File;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PermissionCallbacks
//        , SelectImageCallBack, PicturesPreviewer.OnPictureItemClickListener
{

//    private PicturesPreviewer mPicturesPreviewer;

    //        extends BaseActivity implements View.OnClickListener,PermissionCallbacks {
//
//    private MenuViewItem east;
//    private MenuViewItem west;
//    private MenuViewItem north;
//    private MenuViewItem south;
//    private MenuViewItem middle;
//    private int areaFlag;
//    private final int EAST = 1;
//    private final int WEST = 2;
//    private final int SOUTH = 3;
//    private final int NORTH = 4;
//    private final int MIDDLE = 5;
//
//    @Override
//    protected void initWidget() {
//        east = findViewById(R.id.east);
//        east.setOnClickListener(this);
//
//        west = findViewById(R.id.west);
//        west.setOnClickListener(this);
//
//        north = findViewById(R.id.north);
//        north.setOnClickListener(this);
//
//        south = findViewById(R.id.south);
//        south.setOnClickListener(this);
//
//        middle = findViewById(R.id.middle);
//        middle.setOnClickListener(this);
//
//        EasyPermissions.requestPermissions(this, "为保正程序正常使用,豫油通需要使用如下权限", 0x01,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//    }
//
//    @Override
//    protected int getLayoutID() {
//        return R.layout.activity_main;
//    }
//
//    public void setSelect(int selectPosition) {
//        areaFlag = selectPosition;
//        east.setEnabled(selectPosition != EAST);
//        west.setEnabled(selectPosition != WEST);
//        north.setEnabled(selectPosition != NORTH);
//        south.setEnabled(selectPosition != SOUTH);
//        middle.setEnabled(selectPosition != MIDDLE);
//    }
//
//    @Override
//    public void onClick(View v) {
////        int i = 2 / 0;
//        Logger logger = Logger.getLogger(MainActivity.class);
////        logger.info("当前打印日志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日" +
////                "志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日" +
////                "志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日志操作当前打印日"
////        );
//        switch (v.getId()) {
//            case R.id.east:
////                setSelect(EAST);
//                Intent intent = new Intent(this, PDFActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("pdf_url", "http://www.gov.cn/zhengce/pdfFile/2019_PDF.pdf");
//                bundle.putInt("id", 2019);
//                intent.putExtra(BasePDFActivity.PDF_INFO_BUNDLE, bundle);
//                startActivity(intent);
//                break;
//            case R.id.west:
////                setSelect(WEST);
//                Intent intent1 = new Intent(this, WebViewActivity.class);
//                Bundle bundle1 = new Bundle();
//                bundle1.putString("url", "https://www.jianshu.com/p/eee41ec17606");
//                intent1.putExtra(BaseWebViewActivity.ACTIVITY_BUNDLE, bundle1);
//                startActivity(intent1);
//                break;
//            case R.id.south:
//                new UpdateManagerApp(this, false).showUpdateInfo();
//                setSelect(SOUTH);
//                break;
//            case R.id.north:
//                setSelect(NORTH);
//                break;
//            case R.id.middle:
//                setSelect(MIDDLE);
//                break;
//            default:
//                break;
//        }
//    }
//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    public static String NAV_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "CSSI" + File.separator + "PDF" + File.separator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.showtoast).setOnClickListener(this);
//        mPicturesPreviewer = findViewById(R.id.evaluate_pp);
//        mPicturesPreviewer.setmSelectImageCallBack(this);
//        mPicturesPreviewer.setOnPictureItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Toast.showToast("数据加载中，请稍后...");
        downFile();
    }

    private void downFile() {
        GetBuilder builder = new GetBuilder();
        builder.url("http://www.gov.cn/zhengce/pdfFile/2019_PDF.pdf");
        RequestCall build = builder.build();
        build.execute(new FileCallBack(NAV_FILE_PATH, "123.pdf") {
            @Override
            public void progress(float progress) {
            }

            @Override
            public void onError(Call call, Exception e) {
                XLog.e(MainActivity.class, e);
            }

            @Override
            public void onResponse(File response) {
                XLog.e(MainActivity.class, "下载完成");
            }
        });
    }
//}

//    @Override
//    public void doSelected(String[] images) {
//        mPicturesPreviewer.set(images);
//    }
//
//    @Override
//    public <T> void doSelected(Map<String, T> map) {
//    }
//
//    @Override
//    public void doEmpty(int imageCount) {
//    }
//
//    @Override
//    public void imageClick(RecyclerView recyclerView, int position, String[] images) {
//        ImageGalleryActivity.show(this, images, position, false);
//    }
}
