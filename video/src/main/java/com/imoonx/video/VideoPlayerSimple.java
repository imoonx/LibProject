package com.imoonx.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import com.imoonx.util.XLog;

public class VideoPlayerSimple extends VideoPlayer {

    public VideoPlayerSimple(Context context) {
        super(context);
    }

    public VideoPlayerSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_base;
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        updateFullscreenButton();
        fullscreenButton.setVisibility(View.GONE);
    }

    @Override
    public void setUiWitStateAndScreen(int state) {
        super.setUiWitStateAndScreen(state);
        switch (currentState) {
            case CURRENT_STATE_NORMAL:
                startButton.setVisibility(View.VISIBLE);
                break;
            case CURRENT_STATE_PREPARING:
                startButton.setVisibility(View.INVISIBLE);
                break;
            case CURRENT_STATE_PLAYING:
                startButton.setVisibility(View.VISIBLE);
                break;
            case CURRENT_STATE_PAUSE:
                break;
            case CURRENT_STATE_ERROR:
                break;
        }
        updateStartImage();
    }

    private void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setImageResource(R.drawable.video_click_pause_selector);
        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setImageResource(R.drawable.video_click_error_selector);
        } else {
            startButton.setImageResource(R.drawable.video_click_play_selector);
        }
    }

    public void updateFullscreenButton() {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            fullscreenButton.setImageResource(R.drawable.video_shrink);
        } else {
            fullscreenButton.setImageResource(R.drawable.video_enlarge);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fullscreen && currentState == CURRENT_STATE_NORMAL) {
            XLog.i(VideoPlayerSimple.class, "Play video first");
            return;
        }
        super.onClick(v);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (currentState == CURRENT_STATE_NORMAL) {
                XLog.i(VideoPlayerSimple.class, "Play video first");
                return;
            }
        }
        super.onProgressChanged(seekBar, progress, fromUser);
    }
}
