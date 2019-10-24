package com.imoonx.file.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoonx.file.R;
import com.imoonx.file.recycle.BaseMultiItemQuickAdapter;
import com.imoonx.file.recycle.BaseViewHolder;
import com.imoonx.file.view.CheckBox;
import com.imoonx.util.XFileUtil;

import java.util.List;

public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {

    public MultipleItemQuickAdapter(List<MultipleItem> data) {
        super(data);
        addItemType(MultipleItem.FOLD, R.layout.file_item_fold);
        addItemType(MultipleItem.FILE, R.layout.file_item_file);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        helper.setText(R.id.tv_file_name, item.getData().getFileName());
        if (item.getItemType() == MultipleItem.FOLD) {
            Glide.with(mContext).load(R.mipmap.file_list_folder_icon).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
        } else {
            helper.setText(R.id.tv_file_size, XFileUtil.formetFileSizeD(item.getData().getFileSize()));
            helper.setText(R.id.tv_file_time, item.getData().getTime());
            if (item.getData().getIsCheck()) {
                ((CheckBox) helper.getView(R.id.cb_file)).setChecked(true, false);
            } else {
                ((CheckBox) helper.getView(R.id.cb_file)).setChecked(false, false);
            }
            Glide.with(mContext).load(com.imoonx.file.FileUtil.getFileTypeImageId(mContext, item.getData().getFileName())).fitCenter()
                    .into((ImageView) helper.getView(R.id.iv_file));
        }
    }

}
