package com.imoonx.pdf.viewer;

import android.content.Context;
import android.widget.ImageView;

public class OpaqueImageView extends ImageView {

    public OpaqueImageView(Context context) {
        super(context);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}