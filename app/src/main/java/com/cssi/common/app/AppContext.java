package com.cssi.common.app;

import com.cssi.common.base.BaseApplication;
import com.cssi.common.util.Log;

public class AppContext extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setDebug(BuildConfig.DEBUG);
    }

}
