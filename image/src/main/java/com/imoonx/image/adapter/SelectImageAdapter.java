package com.imoonx.image.adapter;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.imoonx.image.R;
import com.imoonx.image.ui.PicturesPreviewerItemTouchCallback;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择适配器
 */
public class SelectImageAdapter extends RecyclerView.Adapter<SelectImageAdapter.TweetSelectImageHolder>
        implements PicturesPreviewerItemTouchCallback.ItemTouchHelperAdapter {

    private int MAX_SIZE;
    private final int TYPE_NONE = 0;
    private final int TYPE_ADD = 1;
    private final List<Model> mModels = new ArrayList<>();
    private Callback mCallback;
    private static int mPictureId;
    private static boolean mIsNeedPicture;
    public boolean isNotDelete;

    public SelectImageAdapter(Callback callback, int pictureMaxSize, int pictureId, boolean isNeedPicture) {
        mCallback = callback;
        MAX_SIZE = pictureMaxSize;
        mPictureId = pictureId;
        mIsNeedPicture = isNeedPicture;
    }

    @Override
    public int getItemCount() {
        int size = mModels.size();
        if (size == MAX_SIZE) {
            return size;
        } else if (size == 0) {
            return 1;
        } else {
            return size + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Log.e("position", position + "");
        int size = mModels.size();
        if (size >= MAX_SIZE) {
            return TYPE_NONE;
        } else if (position == size + 1) {
            return TYPE_ADD;
        } else if (position == size) {
            return TYPE_ADD;
        } else {
            return TYPE_NONE;
        }
    }

    @NonNull
    @Override
    public TweetSelectImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_list_selecter, parent, false);
        if (viewType == TYPE_NONE) {
            return new TweetSelectImageHolder(view, new TweetSelectImageHolder.HolderListener() {
                @Override
                public void onDelete(Model model) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        int pos = mModels.indexOf(model);
                        if (pos == -1)
                            return;
                        mModels.remove(pos);
                        if (mModels.size() > 0) {
                            notifyItemRemoved(pos);
                            XLog.i(this.getClass(), "有图片");
                        } else {
                            XLog.i(this.getClass(), "无图片");
                            notifyDataSetChanged();
                        }
                        XLog.i(getClass(), "图片的数量=" + mModels.size());
                        if (mCallback != null) {
                            mCallback.onEmpty(mModels.size());
                        }

                    }
                }

                @Override
                public void onDrag(TweetSelectImageHolder holder) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        // Start a drag whenever the handle view it
                        // touched
                        mCallback.onStartDrag(holder);
                    }
                }
            }, isNotDelete);
        } else {
            return new TweetSelectImageHolder(view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        callback.onLoadMoreClick();
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final TweetSelectImageHolder holder, int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE || size != position) {
            Model model = mModels.get(position);
            holder.bind(position, model, mCallback.getImgLoader());
        }
    }

    @Override
    public void onViewRecycled(TweetSelectImageHolder holder) {
        Glide.clear(holder.mImage);
    }

    public void clear() {
        mModels.clear();
    }

    public void add(Model model) {
        XLog.i(this.getClass(), "MAX_SIZE=" + MAX_SIZE);
        if (mModels.size() >= MAX_SIZE)
            return;
        mModels.add(model);
    }

    public void add(String path) {
        add(new Model(path));
    }

    public String[] getPaths() {
        int size = mModels.size();
        if (size == 0)
            return null;
        String[] paths = new String[size];
        int i = 0;
        for (Model model : mModels) {
            paths[i++] = model.path;
        }
        return paths;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // Collections.swap(mModels, fromPosition, toPosition);
        if (fromPosition == toPosition)
            return false;

        if (fromPosition < toPosition) {
            Model fromModel = mModels.get(fromPosition);
            Model toModel = mModels.get(toPosition);

            mModels.remove(fromPosition);
            mModels.add(mModels.indexOf(toModel) + 1, fromModel);
        } else {
            Model fromModel = mModels.get(fromPosition);
            mModels.remove(fromPosition);
            mModels.add(toPosition, fromModel);
        }

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mModels.remove(position);
        notifyItemRemoved(position);
    }

    public boolean isNotDelete() {
        return isNotDelete;
    }

    public void setNotDelete(boolean notDelete) {
        isNotDelete = notDelete;
    }

    public static class Model {
        public Model(String path) {
            this.path = path;
        }

        public String path;
        public boolean isUpload;
    }

    public interface Callback {
        void onLoadMoreClick();

        RequestManager getImgLoader();

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);

        /**
         * 数据 数量变化时调用
         *
         * @param imageCount 图片数量
         */
        void onEmpty(int imageCount);
    }

    /**
     * TweetSelectImageHolder
     */
    static class TweetSelectImageHolder extends RecyclerView.ViewHolder implements PicturesPreviewerItemTouchCallback.ItemTouchHelperViewHolder {

        private ImageView mImage;
        private ImageView mDelete;
        private ImageView mGifMask;
        private HolderListener mListener;

        private TweetSelectImageHolder(View itemView, HolderListener listener, boolean isNotDelete) {
            super(itemView);
            mListener = listener;
            mImage = itemView.findViewById(R.id.iv_content);
            mDelete = itemView.findViewById(R.id.iv_delete);
            mGifMask = itemView.findViewById(R.id.iv_is_gif);
            if (isNotDelete) {
                //不能删除
                mDelete.setVisibility(View.GONE);
            } else {
                //可以删除
                mDelete.setVisibility(View.VISIBLE);
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = v.getTag();
                        final HolderListener holderListener = mListener;
                        if (holderListener != null && obj != null && obj instanceof Model) {
                            holderListener.onDelete((Model) obj);
                        }
                    }
                });
            }

            mImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final HolderListener holderListener = mListener;
                    if (holderListener != null) {
                        holderListener.onDrag(TweetSelectImageHolder.this);
                    }
                    return true;
                }
            });
            mImage.setBackgroundColor(0xffdadada);
        }

        @SuppressWarnings("deprecation")
        private TweetSelectImageHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.iv_content);
            mDelete = itemView.findViewById(R.id.iv_delete);
            mGifMask = itemView.findViewById(R.id.iv_is_gif);
            mDelete.setVisibility(View.GONE);
            if (mIsNeedPicture) {
                mImage.setImageResource(mPictureId);
                mImage.setOnClickListener(clickListener);
            }
            mImage.setBackgroundDrawable(null);
        }

        public void bind(int position, Model model, RequestManager loader) {
            mDelete.setTag(model);
            // In this we need clear before load
            Glide.clear(mImage);
            // Load image
            if (model.path.toLowerCase().endsWith("gif")) {
                loader.load(model.path).asBitmap().centerCrop().error(R.mipmap.page_icon_empty).into(mImage);
                // Show gif mask
                mGifMask.setVisibility(View.VISIBLE);
            } else {
                loader.load(model.path).centerCrop().error(R.mipmap.page_icon_empty).into(mImage);
                mGifMask.setVisibility(View.GONE);
            }
        }

        @Override
        public void onItemSelected() {
            try {
                Vibrator vibrator = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(20);
            } catch (Exception e) {
                XLog.i(SelectImageAdapter.class, e);
            }
        }

        @Override
        public void onItemClear() {
        }

        /**
         * Holder 与Adapter之间的桥梁
         */
        interface HolderListener {
            void onDelete(Model model);

            void onDrag(TweetSelectImageHolder holder);
        }
    }
}
