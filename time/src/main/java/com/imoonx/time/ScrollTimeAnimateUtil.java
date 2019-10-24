package com.imoonx.time;

import android.content.Context;
import android.view.Gravity;

import com.imoonx.util.Res;

public class ScrollTimeAnimateUtil {

    private static final int INVALID = -1;

    public static int getAnimationResource(Context context, int gravity, boolean isInAnimation) {
        switch (gravity) {
            case Gravity.BOTTOM:
                return isInAnimation ? Res.getAnimID("slide_in_bottom") : Res.getAnimID("slide_out_bottom");
            default:
                break;
        }
        return INVALID;
    }
}
