package com.example.apptest;
//

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

//
//import com.imoonx.util.XLog;
//
public class MenuViewItem extends FrameLayout {
    //
//    private int width = -1;
//    private int height = -1;
//    private Bitmap bitmap;
//
    public MenuViewItem(Context context) {
        super(context);
    }

    public MenuViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MenuViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        if (action != MotionEvent.ACTION_DOWN) {
//            return super.onTouchEvent(event);
//        }
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        if (width == -1 || height == -1) {
//            Drawable drawable = getBackground().getCurrent();
//            bitmap = ((BitmapDrawable) drawable).getBitmap();
//            width = getWidth();
//            height = getHeight();
//        }
//        if (null == bitmap || x < 0 || y < 0 || x >= width || y >= height) {
//            XLog.e(MenuViewItem.class, "null == bitmap || x < 0 || y < 0 || x >= width || y >= height");
//            return false;
//        }
//        XLog.e(MenuViewItem.class, "width=" + bitmap.getWidth() + "****height=" + bitmap.getHeight());
//        if (x >= bitmap.getWidth()) {
//            XLog.e(MenuViewItem.class, "x >= bitmap.getWidth()   " + x);
//            return false;
//        }
//        if (y >= bitmap.getHeight()) {
//            XLog.e(MenuViewItem.class, "y >= bitmap.getHeight()  " + y);
//            return false;
//        }
//        int pixel = bitmap.getPixel(x, y);
//        XLog.e(MenuViewItem.class, "x=" + x + "****y=" + y);
//        if (Color.TRANSPARENT == pixel) {
//            XLog.e(MenuViewItem.class, "Color.TRANSPARENT == pixel");
//            return false;
//        }
//        return super.onTouchEvent(event);
//    }
}
