package com.makulu.dota2me;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by xujintian on 2015/8/14.
 */
public class DotaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        context = this;
    }

    private static Context context;

    public static Context getContext() {
        return context;
    }


}
