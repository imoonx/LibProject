package com.imoonx.common.base;

import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imoonx.common.R;
import com.imoonx.util.Res;


/**
 * BaseTitleFragment
 */
public abstract class BaseTitleFragment extends BaseFragment {

    private Toolbar mToolBar;
    private ImageView mStatus;
    private LinearLayout mBackground;
    private EditText mEditText;
    private TextView mTitle;
    private TextView mRightBadge;
    private TextView mLeftBadge;
    private AppBarLayout mAppBarLayout;
    private ImageButton mRightIv;
    private ImageButton mLeftIv;

    protected void setShade(int shadeValue) {
        if (null != mAppBarLayout && Build.VERSION.SDK_INT >= 21) {
            mAppBarLayout.setStateListAnimator(null);
            mAppBarLayout.setElevation(shadeValue);
        }
    }

    @Override
    protected void initWidget(View root) {
        mBackground = root.findViewById(R.id.base_root_view);
        mStatus = root.findViewById(R.id.status);
        mAppBarLayout = root.findViewById(R.id.appbar_layout);
        mToolBar = root.findViewById(R.id.toolbar);
        if (isCanSccroll()) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolBar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            mToolBar.setLayoutParams(params);
        }

        FrameLayout toolbar_button_left = root.findViewById(R.id.toolbar_button_left);
        FrameLayout toolbar_button_right = root.findViewById(R.id.toolbar_button_right);

        mLeftIv = toolbar_button_left.findViewById(R.id.iv_icon);
        mLeftIv.setImageResource(getLeftBackgroundResource());
        mLeftBadge = toolbar_button_left.findViewById(R.id.tv_badge);

        mRightIv = toolbar_button_right.findViewById(R.id.iv_icon);
        mRightIv.setImageResource(getRightBackgroundResource());
        mRightBadge = toolbar_button_right.findViewById(R.id.tv_badge);

        View.OnClickListener listener = getIconClickListener();

        toolbar_button_left.setOnClickListener(listener);
        toolbar_button_right.setOnClickListener(listener);

        mEditText = root.findViewById(R.id.toolbar_edittext);
        mTitle = root.findViewById(R.id.toolbar_title);

        mTitle.setText(getTitleDesc());

        ViewStub stub = root.findViewById(R.id.lay_content);

        if (getLayoutID() == 0) {
            throw new NullPointerException("View Not null");
        }
        stub.setLayoutResource(getContentLayoutId());

        stub.inflate();

    }

    protected boolean isCanSccroll() {
        return false;
    }

    /**
     * 获取页面大标题
     *
     * @return 标题
     */
    protected int getTitleDesc() {
        return R.string.default_title_desc;
    }

    protected View.OnClickListener getIconClickListener() {
        return null;
    }

    protected abstract
    @LayoutRes
    int getContentLayoutId();


    protected void setLeftBadge(int i) {
        if (null != mLeftBadge) {
            mLeftBadge.setVisibility(i);
        }
    }

    protected void setRightBadge(int i) {
        if (null != mLeftBadge) {
            mRightBadge.setVisibility(i);
        }
    }

    protected int getRightBackgroundResource() {
        return 0;
    }

    protected int getLeftBackgroundResource() {
        return 0;
    }

    protected void setRightBackgroundResource(int res) {
        if (null != mRightIv)
            mRightIv.setImageResource(res);
    }

    protected void setLeftBackgroundResource(int res) {
        if (null != mLeftIv)
            mLeftIv.setImageResource(res);
    }

    /**
     * 设置edittext 可见
     */
    protected void setEditTextVisibilily() {
        if (null != mTitle && mTitle.getVisibility() == View.VISIBLE)
            mTitle.setVisibility(View.GONE);
        if (null != mEditText && mEditText.getVisibility() == View.GONE)
            mTitle.setVisibility(View.VISIBLE);
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
     * 设置 deittext hite 颜色
     *
     * @param colorID 颜色id
     */
    protected void setEditHintTextColor(int colorID) {
        if (null != mEditText && colorID != 0 && mEditText.getVisibility() == View.VISIBLE)
            mEditText.setHintTextColor(Res.getColor(colorID));
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
     * @param colorId 背景id
     */
    protected void setEditTextBgColor(int colorId) {
        if (null != mEditText && colorId != 0 && mEditText.getVisibility() == View.VISIBLE)
            mEditText.setBackgroundColor(colorId);
    }

    /**
     * 设置edittext 背景
     *
     * @param resID 资源id
     */
    protected void setEditTextBgRes(int resID) {
        if (null != mEditText && resID != 0 && mEditText.getVisibility() == View.VISIBLE)
            mEditText.setBackgroundResource(resID);
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
    protected void setTitleDesc(int titleID) {
        setTitleDesc(Res.getString(titleID));
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    protected void setTitleDesc(String title) {
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
     * 设置隐藏s
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
        if (resId == 0) {
            if (null != mAppBarLayout && Build.VERSION.SDK_INT >= 21) {
                mAppBarLayout.setElevation(Res.getDimens(R.dimen.space_2));
            }
            if (null != mToolBar && mToolBar.getVisibility() == View.VISIBLE)
                mToolBar.setBackgroundColor(Res.getColor(R.color.colorPrimary));
            if (null != mStatus && mStatus.getVisibility() == View.VISIBLE)
                mStatus.setBackgroundColor(Res.getColor(R.color.colorPrimary));
            if (null != mBackground && mBackground.getVisibility() == View.VISIBLE)
                mBackground.setBackgroundResource(R.drawable.transparent_bg);
        } else {
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
    }

    /**
     * 设置背景色
     *
     * @param colorId 颜色id
     */
    protected void setBackgroundColor(int colorId) {

        if (colorId == 0) {
            if (null != mAppBarLayout && Build.VERSION.SDK_INT >= 21) {
                mAppBarLayout.setStateListAnimator(null);
                mAppBarLayout.setElevation(Res.getDimens(R.dimen.space_2));
            }
            if (null != mToolBar && mToolBar.getVisibility() == View.VISIBLE)
                mToolBar.setBackgroundColor(Res.getColor(R.color.colorPrimary));
            if (null != mStatus && mStatus.getVisibility() == View.VISIBLE)
                mStatus.setBackgroundColor(Res.getColor(R.color.colorPrimary));
            if (null != mBackground && mBackground.getVisibility() == View.VISIBLE)
                mBackground.setBackgroundResource(R.drawable.transparent_bg);
        } else {
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

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_base_title;
    }

    protected Toolbar getToolBar() {
        if (mToolBar == null)
            return null;
        return mToolBar;
    }

    protected ImageView getStatus() {
        if (mStatus == null)
            return null;
        return mStatus;
    }

    protected LinearLayout getBackground() {
        if (mBackground == null)
            return null;
        return mBackground;
    }

    protected EditText getEditTextView() {
        if (mEditText == null)
            return null;
        return mEditText;
    }

    protected TextView getTitle() {
        if (mTitle == null)
            return null;
        return mTitle;
    }

    protected TextView getRightBadge() {
        if (mRightBadge == null)
            return null;
        return mRightBadge;
    }

    protected TextView getLeftBadge() {
        if (mLeftBadge == null)
            return null;
        return mLeftBadge;
    }

    protected AppBarLayout getAppBarLayout() {
        if (mAppBarLayout == null)
            return null;
        return mAppBarLayout;
    }
}
