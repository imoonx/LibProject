package com.imoonx.common.ui.gesturelock;

import android.content.Context;
import android.content.SharedPreferences;

import com.imoonx.util.TDevice;

public class GesturePreference {
    private Context context;
    private final String fileName = TDevice.getPackageName();
    private String nameTable = TDevice.getPackageName();

    public GesturePreference(Context context, int nameTableId) {
        this.context = context;
        if (nameTableId != -1)
            this.nameTable = nameTable + nameTableId;
    }

    public void WriteStringPreference(String data) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(nameTable, data);
        editor.commit();
    }

    public String ReadStringPreference() {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString(nameTable, "null");
    }
}
