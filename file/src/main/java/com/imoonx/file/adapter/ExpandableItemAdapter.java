package com.imoonx.file.adapter;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoonx.file.FileSQLiteHelper;
import com.imoonx.file.R;
import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.bean.SubItem;
import com.imoonx.file.recycle.BaseMultiItemQuickAdapter;
import com.imoonx.file.recycle.BaseViewHolder;
import com.imoonx.file.recycle.entity.MultiItemEntity;
import com.imoonx.file.utils.Config;
import com.imoonx.file.view.CheckBox;
import com.imoonx.util.XFileUtil;

import java.util.List;

public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int HEAD = 0;
    public static final int CONTENT = 1;
    private FileSQLiteHelper mFileSQLiteHelper;
    private boolean isPhoto = false;

    public ExpandableItemAdapter(List<MultiItemEntity> data, boolean isPhoto, FileSQLiteHelper fileSQLiteHelper) {
        super(data);
        this.isPhoto = isPhoto;
        addItemType(HEAD, R.layout.file_item_head);
        addItemType(CONTENT, R.layout.file_item_content);
        this.mFileSQLiteHelper = fileSQLiteHelper;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case HEAD:
                final SubItem subItem = (SubItem) item;
                if (null == subItem.getSubItems() || subItem.getSubItems().size() == 0) {
                    helper.setText(R.id.tv_count, mContext.getString(R.string.count, "" + 0));
                } else {
                    helper.setText(R.id.tv_count, mContext.getString(R.string.count, "" + subItem.getSubItems().size()));
                }
                helper.setText(R.id.tv_title, subItem.getTitle());
                helper.setImageResource(R.id.expanded_menu, subItem.isExpanded() ? R.mipmap.ic_arrow_drop_down_grey_700_24dp : R.mipmap.ic_arrow_drop_up_grey_700_24dp);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (subItem.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case CONTENT:
                final FileInfo f = (FileInfo) item;
                helper.setText(R.id.tv_content, f.getFileName()).setText(R.id.tv_size, XFileUtil.formetFileSizeD(f.getFileSize()))
                        .setText(R.id.tv_time, f.getTime());

                if (isPhoto) {
                    Glide.with(mContext).load(f.getFilePath()).into((ImageView) helper.getView(R.id.iv_cover));
                } else {
                    Glide.with(mContext).load(com.imoonx.file.FileUtil.getFileTypeImageId(mContext, f.getFilePath())).fitCenter()
                            .into((ImageView) helper.getView(R.id.iv_cover));
                }
                if (null != mFileSQLiteHelper)
                    if (mFileSQLiteHelper.qureyByFileName(f.getFileName()))
                        ((CheckBox) helper.getView(R.id.cb_file)).setChecked(true, true);
                    else ((CheckBox) helper.getView(R.id.cb_file)).setChecked(false, true);

                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isPhoto) {
                            boolean IsPhoto = f.getIsPhoto();
                            f.setIsPhoto(!IsPhoto);
                        } else {
                            f.setIsPhoto(false);
                        }
                        boolean isCheck = f.getIsCheck();
                        f.setIsCheck(!isCheck);
                        if (f.getIsCheck()) {
                            if (null != mFileSQLiteHelper)
                                mFileSQLiteHelper.insertFile(f);
                            ((CheckBox) helper.getView(R.id.cb_file)).setChecked(true, true);
                        } else {
                            if (null != mFileSQLiteHelper)
                                mFileSQLiteHelper.deleteFile(f);
                            ((CheckBox) helper.getView(R.id.cb_file)).setChecked(false, true);
                        }
                        Intent intent = new Intent();
                        intent.setAction(Config.INTENT_ACTION_UPDATA_SELECT_COUNT);
                        if (null != mContext)
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                });
                break;
        }
    }
}
