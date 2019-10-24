package com.imoonx.common.behavior;

public interface AnimateHelper {

    int STATE_SHOW = 1;
    int STATE_HIDE = 0;

    void show();

    void hide();

    void setStartY(float y);

    void setMode(int modeBottom);

    int getState();
}
