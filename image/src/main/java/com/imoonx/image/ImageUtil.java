package com.imoonx.image;


import com.imoonx.image.bean.Image;

import java.util.List;

public class ImageUtil {

    public static String[] toArray(List<Image> images) {
        if (images == null)
            return null;
        int len = images.size();
        if (len == 0)
            return null;
        String[] strings = new String[len];
        int i = 0;
        for (Image image : images) {
            strings[i] = image.getPath();
            i++;
        }
        return strings;
    }
}
