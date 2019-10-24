package com.imoonx.common.base;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.imoonx.common.R;
import com.imoonx.common.interf.OnBackCallBackListener;
import com.imoonx.common.ui.dialog.DialogHelper;
import com.imoonx.common.ui.dialog.WaitDialog;
import com.imoonx.http.callback.StringCallback;
import com.imoonx.util.Res;
import com.imoonx.util.TDevice;
import com.imoonx.util.XLog;

import okhttp3.Call;


/**
 * activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, OnBackCallBackListener {

    /**
     * 全局背景
     */
    private LinearLayout mBackground;
    private boolean _isVisible;
    /**
     * toolbar
     */
    private Toolbar mToolBar;
    /**
     * 标题
     */
    private TextView mTitle;
    /**
     * 提示框
     */
    private WaitDialog mWaitDialog;
    /**
     * 状态栏
     */
    private ImageView mStatus;
    /**
     * toolbar 编辑框
     */
    private EditText mEditText;
    private AppBarLayout mAppBarLayout;
    /**
     * 输入框布局
     */
    private LinearLayout mEditTextLinearLayout;
    /**
     * 搜索按钮
     */
    private ImageView mSearchButton;

    /**
     * 设置toolbar阴影
     *
     * @param shadeValue 阴影值
     */
    protected void setShade(int shadeValue) {
        if (null != mAppBarLayout && Build.VERSION.SDK_INT >= 21) {
            mAppBarLayout.setStateListAnimator(null);
            mAppBarLayout.setElevation(shadeValue);
        }
    }

    /**
     * 设置背景透明度
     *
     * @param alpha 0-1
     */
    protected void setBackageAlpha(float alpha) {
        try {
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.alpha = alpha;
            window.setAttributes(attributes);
        } catch (Exception e) {
            XLog.e(this.getClass(), e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        if (initBundle(getIntent().getExtras())) {
            setContentView(R.layout.activity_base);

            initPermission();

            initActionBar();

            ViewStub stub = findViewById(R.id.lay_content);

            mBackground = findViewById(R.id.base_root_view);
            mStatus = findViewById(R.id.status);

            if (getLayoutID() == 0) {
                throw new NullPointerException("View Not null");
            }
            stub.setLayoutResource(getLayoutID());

            stub.inflate();

            initWidget();

            initData();

            _isVisible = true;
        } else {
            finish();
        }
    }


    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 初始化控件
     */
    protected abstract void initWidget();

    /**
     * 初始化权限
     */
    protected void initPermission() {
    }

    /**
     * 初始化布局
     *
     * @return layoutID
     */
    protected abstract int getLayoutID();

    /**
     * 初始化ActionBar
     */
    protected void initActionBar() {
        mAppBarLayout = findViewById(R.id.appbar_layout);
        mToolBar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.toolbar_title);
        mEditTextLinearLayout = findViewById(R.id.toolbar_edittext_ll);
        mSearchButton = findViewById(R.id.search);
        mEditText = findViewById(R.id.toolbar_edittext);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
//                    TDevice.hideSoftKeyboard(mEditText);
                    // 搜索，进行自己要的操作...
                    //这里是我要做的操作！
                    searchByKeyWord();
                    return true;
                }
                return false;
            }
        });
        if (mToolBar != null) {
            mToolBar.setVisibility(isShowToolbar() ? View.VISIBLE : View.GONE);
            setSupportActionBar(this.mToolBar);
            ActionBar actionBar = getSupportActionBar();
            if (null != actionBar) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
            }
            mTitle.setText(getTitleDesc());
            if (isCanSccroll()) {
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolBar.getLayoutParams();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                mToolBar.setLayoutParams(params);
            }
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    XLog.e(BaseActivity.class, "isKeyboardShown(mBackground.getRootView())=" + TDevice.isKeyboardShown(mBackground.getRootView()));
                    if (TDevice.isKeyboardShown(v.getRootView())) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                    } else {
                        onBackPressed();
                    }
                }
            });
        } else {
            XLog.e(getClass(), "actionBar  null");
        }
    }

    protected void setCanScroll() {
        if (null != mToolBar) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolBar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            mToolBar.setLayoutParams(params);
        }
    }

    /**
     * actionbar 是否可以滚动
     *
     * @return true false
     */
    protected boolean isCanSccroll() {
        return false;
    }

    protected void searchByKeyWord() {
        TDevice.hideSoftKeyboard(mEditText);
    }

    @Override
    public void onBackPressed() {
        if (null == mFragment || !mFragment.onBackPressed())
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
    }


    /**
     * 初始化 传递的参数
     *
     * @param extras 参数
     * @return boolean
     */
    protected boolean initBundle(Bundle extras) {
        return true;
    }

    public WaitDialog showWaitDialog() {
        return showWaitDialog(R.string.date_is_loading);
    }

    public WaitDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    public WaitDialog showWaitDialog(String message) {
        return showWaitDialog(message, true);
    }

    public WaitDialog showWaitDialog(String message, boolean isCancel) {
        if (_isVisible) {
            if (mWaitDialog == null) {
                mWaitDialog = DialogHelper.getWaitDialog(this, message);
            }
            if (mWaitDialog != null) {
                mWaitDialog.setMessage(message);
                if (!isCancel) {
                    mWaitDialog
                            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                public boolean onKey(DialogInterface dialog,
                                                     int keyCode, KeyEvent event) {
                                    return (keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0);
                                }
                            });
                }
                mWaitDialog.show();
            }
            return mWaitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if ((_isVisible) && (mWaitDialog != null)) {
            try {
                mWaitDialog.dismiss();
                mWaitDialog = null;
            } catch (Exception e) {
                XLog.e(this.getClass(), "waitdialog hide fail" + e);
            }
        }
    }

    protected Class getClassName() {
        return this.getClass();
    }

    protected StringCallback mCallback = new StringCallback() {
        public void onError(Call call, Exception e) {
            XLog.e(getClassName(), "onError" + e);
            onLoadError(e);
        }

        public void onResponse(String response) {
            XLog.i(getClassName(), "onResponse" + response);
            onParse(response);
        }
    };


    protected void onParse(String response) {
    }

    protected void onLoadError(Exception e) {
    }

    public void onClick(View v) {
    }

    protected LinearLayout getBackground() {
        if (mBackground == null)
            return null;
        return mBackground;
    }

    public EditText getEditTextView() {
        if (mEditText == null)
            return null;
        return mEditText;
    }

    protected Toolbar getToolBar() {
        if (mToolBar == null)
            return null;
        return mToolBar;
    }

    protected TextView getTextView() {
        if (mTitle == null)
            return null;
        return mTitle;
    }

    protected ImageView getStatus() {
        if (mStatus == null)
            return null;
        return mStatus;
    }

    protected AppBarLayout getAppBarLayout() {
        if (mAppBarLayout == null)
            return null;
        return mAppBarLayout;
    }

    private RequestManager mImageLoader;

    protected synchronized RequestManager getImageLoader() {
        if (this.mImageLoader == null) {
            this.mImageLoader = Glide.with(this);
        }
        return this.mImageLoader;
    }

    protected void onStart() {
        if (this.mImageLoader != null) {
            this.mImageLoader.onStart();
        }
        super.onStart();
    }

    protected void onStop() {
        if (this.mImageLoader != null) {
            this.mImageLoader.onStop();
        }
        super.onStop();
    }

    protected void onDestroy() {
        hideWaitDialog();
        if (this.mImageLoader != null) {
            this.mImageLoader.onDestroy();
        }
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuRes() != 0) {
            getMenuInflater().inflate(getMenuRes(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        getMenuItemClick(item);
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置menu 菜单
     *
     * @return 菜单id
     */
    protected int getMenuRes() {
        return 0;
    }

    /**
     * 设置菜单项点击事件
     *
     * @param item menuitem
     */
    protected void getMenuItemClick(MenuItem item) {
    }

    /**
     * 设置搜索按钮背景色
     *
     * @param res 背景id
     */
    protected void setSerchButtonRes(int res) {
        if (null != mSearchButton && mSearchButton.getVisibility() == View.VISIBLE)
            mSearchButton.setImageResource(res);
    }

    /**
     * 获取搜索按钮
     *
     * @return ImageView 异常返回null
     */
    protected ImageView getSerchButton() {
        if (null == mSearchButton)
            return null;
        return mSearchButton;
    }

    /**
     * 获取搜索框布局
     *
     * @return LinearLayout 异常返回空
     */
    protected LinearLayout getEditTextLinearLayout() {
        if (null == mEditTextLinearLayout)
            return null;
        return mEditTextLinearLayout;
    }


    /**
     * 设置edittext 可见
     */
    protected void setEditTextLinearLayoutVisibilily() {
        if (null != mTitle && mTitle.getVisibility() == View.VISIBLE)
            mTitle.setVisibility(View.GONE);
        if (null != mEditTextLinearLayout && mEditTextLinearLayout.getVisibility() == View.GONE)
            mEditTextLinearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取输入的值
     *
     * @return edittext值
     */
    protected String getEditText() {
        if (null != mEditText && mEditText.getVisibility() == View.VISIBLE) {
            return mEditText.getText().toString().trim();
        }
        return "";
    }

    /**
     * 设置 deittext hite 文字
     *
     * @param hintId 提示信息id
     */
    protected void setEditHint(int hintId) {
        setEditHint(Res.getString(hintId));
    }

    /**
     * 设置 deittext hite 文字
     *
     * @param hint 提示信息
     */
    protected void setEditHint(String hint) {
        if (null != mEditText && hint != null && mEditText.getVisibility() == View.VISIBLE)
            mEditText.setHint(hint);
    }

    /**
     * 设置 deittext hite 颜色
     *
     * @param colorID 颜色id
     */
    protected void setEditHintTextColor(int colorID) {
        if (null != mEditText && colorID != 0 && mEditText.getVisibility() == View.VISIBLE)
            mEditText.setHintTextColor(Res.getColor(colorID));
    }

    /**
     * 设置edittext 字体颜色
     *
     * @param colorID 颜色id
     */
    protected void setEditTextColor(int colorID) {
        if (null != mEditText && colorID != 0 && mEditText.getVisibility() == View.VISIBLE)
            mEditText.setTextColor(Res.getColor(colorID));
    }

    /**
     * 设置edittext 字体大小
     *
     * @param size 字体大小
     */
    protected void setEditTextSize(int size) {
        if (null != mEditText && size != 0 && mEditText.getVisibility() == View.VISIBLE)
            mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * 设置edittext 背景
     *
     * @param colorId 颜色id
     */
    protected void setEditTextLinearLayoutBgColor(int colorId) {
        if (null != mEditTextLinearLayout && colorId != 0 && mEditTextLinearLayout.getVisibility() == View.VISIBLE)
            mEditTextLinearLayout.setBackgroundColor(colorId);
    }

    /**
     * 设置edittext 背景
     *
     * @param resID 资源id
     */
    protected void setEditTextLinearLayoutBgRes(int resID) {
        if (null != mEditTextLinearLayout && resID != 0 && mEditTextLinearLayout.getVisibility() == View.VISIBLE)
            mEditTextLinearLayout.setBackgroundResource(resID);
    }

    /**
     * 设置标题颜色
     *
     * @param colorID 颜色id
     */
    protected void setTextColor(int colorID) {
        if (null != mTitle && colorID != 0 && mTitle.getVisibility() == View.VISIBLE)
            mTitle.setTextColor(Res.getColor(colorID));
    }

    /**
     * 设置标题字体大小
     *
     * @param size 字体大小
     */
    protected void setTextSize(int size) {
        if (null != mTitle && size != 0 && mTitle.getVisibility() == View.VISIBLE)
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * 设置标题
     *
     * @param titleID 标题id
     */
    public void setTitleDesc(int titleID) {
        setTitleDesc(Res.getString(titleID));
    }

    /**
     * 设置标题
     *
     * @param title 标题内容
     */
    public void setTitleDesc(String title) {
        if (null != mTitle && title != null && mTitle.getVisibility() == View.VISIBLE)
            mTitle.setText(title);
    }

    /**
     * 设置状态栏 背景
     *
     * @param resId 背景id
     */
    protected void setStatusRes(int resId) {
        if (null != mStatus && mStatus.getVisibility() == View.VISIBLE)
            mStatus.setBackgroundResource(resId);
    }

    /**
     * 设置toolbar隐藏
     */
    protected void setGone() {
        if (null != mToolBar && mToolBar.getVisibility() == View.VISIBLE)
            mToolBar.setVisibility(View.GONE);
        if (null != mStatus && mStatus.getVisibility() == View.VISIBLE)
            mStatus.setVisibility(View.GONE);
    }

    /**
     * 设置状态栏 背景
     *
     * @param colorId 颜色id
     */
    protected void setStatusColor(int colorId) {
        if (null != mStatus && mStatus.getVisibility() == View.VISIBLE)
            mStatus.setBackgroundColor(Res.getColor(colorId));
    }

    /**
     * 设置全局背景色
     *
     * @param resId 资源id
     */
    protected void setBackgroundRes(int resId) {
        if (null != mAppBarLayout && Build.VERSION.SDK_INT >= 21) {
            mAppBarLayout.setStateListAnimator(null);
            mAppBarLayout.setElevation(0);
        }
        if (null != mToolBar && mToolBar.getVisibility() == View.VISIBLE)
            mToolBar.setBackgroundResource(R.drawable.transparent_bg);
        if (null != mStatus && mStatus.getVisibility() == View.VISIBLE)
            mStatus.setBackgroundResource(R.drawable.transparent_bg);
        if (null != mBackground && mBackground.getVisibility() == View.VISIBLE)
            mBackground.setBackgroundResource(resId);
    }

    /**
     * 设置背景色
     *
     * @param colorId 颜色id
     */
    protected void setBackgroundColor(int colorId) {
        if (null != mAppBarLayout && Build.VERSION.SDK_INT >= 21) {
            mAppBarLayout.setStateListAnimator(null);
            mAppBarLayout.setElevation(0);
        }
        if (null != mToolBar && mToolBar.getVisibility() == View.VISIBLE)
            mToolBar.setBackgroundResource(R.drawable.transparent_bg);
        if (null != mStatus && mStatus.getVisibility() == View.VISIBLE)
            mStatus.setBackgroundResource(R.drawable.transparent_bg);
        if (null != mBackground && mBackground.getVisibility() == View.VISIBLE)
            mBackground.setBackgroundColor(Res.getColor(colorId));
    }

    /**
     * 设置Toolbar背景
     *
     * @param resId 背景id
     */
    protected void setToolBarRes(int resId) {
        if (null != mToolBar && mToolBar.getVisibility() == View.VISIBLE)
            mToolBar.setBackgroundResource(resId);
    }

    /**
     * 设置Toolbar背景
     *
     * @param colorId 颜色id
     */
    protected void setToolBarColor(int colorId) {
        if (null != mToolBar && mToolBar.getVisibility() == View.VISIBLE)
            mToolBar.setBackgroundColor(Res.getColor(colorId));
    }

    /**
     * 获取页面大标题
     *
     * @return 标题
     */
    protected int getTitleDesc() {
        return R.string.default_title_desc;
    }

    /**
     * 是否显示toolbar
     *
     * @return true false
     */
    protected boolean isShowToolbar() {
        return true;
    }

    private BaseFragment mFragment;

    @Override
    public void onSelectedFragment(BaseFragment selectedFragment) {
        this.mFragment = selectedFragment;
    }

}
