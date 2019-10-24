package com.imoonx.video;

/**
 * Put VideoPlayer into layout From a VideoPlayer to another VideoPlayer
 */
public class VideoPlayerManager {

    public static VideoPlayer FIRST_FLOOR_VIDEO;
    public static VideoPlayer SECOND_FLOOR_VIDEO;

    public static void setFirstFloor(VideoPlayer videoPlayer) {
        FIRST_FLOOR_VIDEO = videoPlayer;
    }

    public static void setSecondFloor(VideoPlayer videoPlayer) {
        SECOND_FLOOR_VIDEO = videoPlayer;
    }

    public static VideoPlayer getFirstFloor() {
        return FIRST_FLOOR_VIDEO;
    }

    public static VideoPlayer getSecondFloor() {
        return SECOND_FLOOR_VIDEO;
    }

    public static VideoPlayer getCurrentJcvd() {
        if (getSecondFloor() != null) {
            return getSecondFloor();
        }
        return getFirstFloor();
    }

    public static void completeAll() {
        if (SECOND_FLOOR_VIDEO != null) {
            SECOND_FLOOR_VIDEO.onCompletion();
            SECOND_FLOOR_VIDEO = null;
        }
        if (FIRST_FLOOR_VIDEO != null) {
            FIRST_FLOOR_VIDEO.onCompletion();
            FIRST_FLOOR_VIDEO = null;
        }
    }
}
