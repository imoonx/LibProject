package com.imoonx.pdf;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imoonx.common.base.BaseActivity;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.http.builder.GetBuilder;
import com.imoonx.http.callback.FileCallBack;
import com.imoonx.http.request.RequestCall;
import com.imoonx.pdf.viewer.MuPDFCore;
import com.imoonx.pdf.viewer.PageAdapter;
import com.imoonx.pdf.viewer.PageView;
import com.imoonx.pdf.viewer.ReaderView;
import com.imoonx.util.XJavaBType;
import com.imoonx.util.XLog;
import com.imoonx.util.XMd5;

import java.io.File;
import java.util.Locale;

import okhttp3.Call;

/**
 * Created by 36238 on 2019/4/8 星期一
 * <p>
 * 加载pdf
 */
public abstract class BasePDFActivity extends BaseActivity implements CustomReaderView.OnPageListener {

    protected RelativeLayout mPageViewLinearLayout;
    protected EmptyLayout mEmptyLayout;
    protected String pdfUrl;

    protected TextView mPageNum;
    protected MuPDFCore muPDFCore;
    protected CustomReaderView mReaderView;
    protected int termid;
    protected String pdfPath;
    public static final String PDF_INFO_BUNDLE = "pdf_info";

    protected MuPDFCore openFile(String path) {
        try {
            return new MuPDFCore(path);
        } catch (Exception e) {
            XLog.e(this.getClass(), e);
            return null;
        }
    }

    public void createUI() {
        if (muPDFCore == null)
            return;
        mPageViewLinearLayout.removeAllViews();
        mReaderView = new CustomReaderView(this);
        mReaderView.setAdapter(new PageAdapter(this, muPDFCore));
        mReaderView.setOnPageListener(this);
        mPageViewLinearLayout.addView(mReaderView);
        mPageNum.setText(String.format(Locale.ROOT, "%d/%d", 1, muPDFCore.countPages()));
    }

    @Override
    protected void initWidget() {
        Bundle pdfInfo = getIntent().getBundleExtra(PDF_INFO_BUNDLE);
        if (null == pdfInfo) {
            XLog.i(this.getClass(), "数据传递失败");
            finish();
        } else {
            pdfUrl = pdfInfo.getString("pdf_url");
            termid = pdfInfo.getInt("id");
            XLog.i(this.getClass(), "pdfUrl=" + pdfUrl);

            mPageViewLinearLayout = findViewById(R.id.page_view);
            mPageNum = findViewById(R.id.page_num);
            mEmptyLayout = findViewById(R.id.empty_layout);
            mEmptyLayout.setNoDataContent("暂无信息");
            mEmptyLayout.setOnLayoutClickListener(this);
            initWeightChild(pdfInfo);
            getPDFile();
        }
    }

    protected void initWeightChild(Bundle pdfInfo) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_error_layout) {
            XLog.i(this.getClass(), "点击事件");
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            getPDFile();
        }
    }

    private void getPDFile() {
        if (TextUtils.isEmpty(pdfUrl) || (null != pdfUrl && pdfUrl.contains("null"))) {
            mEmptyLayout.setErrorType(EmptyLayout.NODATA);
            return;
        }
        GetBuilder builder = new GetBuilder();
        builder.url(pdfUrl);
        RequestCall build = builder.build();
        build.execute(new FileCallBack(getPDFFilePath(), XMd5.getMD5(XJavaBType.toStr(termid)) + ".pdf") {

            @Override
            public void progress(float progress) {
            }

            @Override
            public void onError(Call call, Exception e) {
                XLog.e(this.getClass(), e);
                mEmptyLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
            }

            @Override
            public void onResponse(File response) {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                pdfPath = response.getAbsolutePath();
                muPDFCore = openFile(pdfPath);
                createUI();
            }
        });
    }

    protected abstract String getPDFFilePath();

    @Override
    protected int getLayoutID() {
        return R.layout.activity_base_pdf;
    }

    @Override
    public void onMoveToChild(int i) {
        if (null == muPDFCore)
            return;
        mPageNum.setText(String.format(Locale.ROOT, "%d/%d", i + 1, muPDFCore.countPages()));
    }

    @Override
    public void onTapMainDocArea() {
    }

    @Override
    public void onDocMotion() {
    }

    public void onDestroy() {
        if (mReaderView != null) {
            mReaderView.applyToChildren(new ReaderView.ViewMapper() {
                public void applyToView(View view) {
                    ((PageView) view).releaseBitmaps();
                }
            });
        }
        if (muPDFCore != null)
            muPDFCore.onDestroy();
        muPDFCore = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (null == mReaderView || !mReaderView.popHistory())
            super.onBackPressed();
    }
}
