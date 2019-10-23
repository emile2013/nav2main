package com.yumibb.android.lib.nav2main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;


public class Nav2Main {
    private volatile static Nav2Main sInstance;


    private Class<? extends Activity> mMainClazz;


    private Nav2MainCallback mNav2MainCallback;

    private Nav2Main() {
    }

    public static Nav2Main getInstance() {
        if (sInstance == null) {
            synchronized (Nav2Main.class) {
                if (sInstance == null) {
                    sInstance = new Nav2Main();
                }
            }
        }
        return sInstance;
    }

    public Nav2Main main(Class<? extends Activity> clazz) {
        mMainClazz = clazz;
        return this;
    }


    public void init(Application application, Nav2MainCallback callback) {

        if (application == null)
            throw new RuntimeException("Nav2Main application can not be null!");

        mNav2MainCallback = callback;
        ALifecycleCallback lifecycleCallback = new ALifecycleCallback();
        application.registerActivityLifecycleCallbacks(lifecycleCallback);

    }

    public Class<? extends Activity> getMainClazz() {
        return mMainClazz;
    }


    public interface Nav2MainCallback {
        void beforeRoute(Context context, Intent intent);
    }

    public Nav2MainCallback getNav2MainCallback() {
        return mNav2MainCallback;
    }
}
