package com.imoonx.common.ui.slide;

public interface SwipeListener {

    void onClose(SwipeLayout swipeLayout);

    void onOpen(SwipeLayout swipeLayout);

    void onStartClose(SwipeLayout swipeLayout);

    void onStartOpen(SwipeLayout swipeLayout);
}
