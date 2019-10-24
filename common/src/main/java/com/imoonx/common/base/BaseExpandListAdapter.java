package com.imoonx.common.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseListAdapter
 */
public abstract class BaseExpandListAdapter<T> extends BaseExpandableListAdapter implements ViewHolder.Callback {

    protected LayoutInflater mInflater;
    private List<T> mDatas;
    private List<T> mPreData;
    protected Callback mCallback;

    public BaseExpandListAdapter(Callback callback) {
        this.mCallback = callback;
        this.mInflater = LayoutInflater.from(callback.getContext());
        this.mDatas = new ArrayList<>();
    }

    public List<T> getDatas() {
        return this.mDatas;
    }

    public void updateItem(int location, T item) {
        if (mDatas.isEmpty())
            return;
        mDatas.set(location, item);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        checkListNull();
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int location, T item) {
        checkListNull();
        mDatas.add(location, item);
        notifyDataSetChanged();
    }

    public void addItem(List<T> items) {
        checkListNull();
        if (items != null) {
            List<T> date = new ArrayList<>();
            if (mPreData != null) {
                for (T d : items) {
                    if (!mPreData.contains(d)) {
                        date.add(d);
                    }
                }
            } else {
                date = items;
            }
            mPreData = items;
            mDatas.addAll(date);
        }
        notifyDataSetChanged();
    }

    public void addItem(int position, List<T> items) {
        checkListNull();
        mDatas.addAll(position, items);
        notifyDataSetChanged();
    }

    public void removeItem(int location) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.remove(location);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mPreData = null;
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void checkListNull() {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
    }

    @Override
    public RequestManager getImgLoader() {
        return mCallback.getImgLoader();
    }

    @Override
    public LayoutInflater getInflate() {
        return mInflater;
    }

    @Override
    public int getGroupCount() {
        return mDatas.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < mDatas.size())
            return mDatas.get(groupPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public interface Callback {
        RequestManager getImgLoader();

        Context getContext();
    }
}