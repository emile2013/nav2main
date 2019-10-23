package com.yumibb.android.nav2main.demo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yumibb.android.lib.nav2main.Nav2Main;

/**
 * Demo App
 *
 * @author huangyuan
 * @since 2019-10-23
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Nav2Main.getInstance().main(MainActivity.class).init(this, new Nav2Main.Nav2MainCallback() {
            @Override
            public void beforeRoute(Context context, Intent intent) {
                Log.i("App", "beforeRoute");
            }
        });
    }
}
