package com.yumibb.android.lib.nav2main;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;


public class ALifecycleCallback implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityStore.getInstance().putActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ActivityStore.clearStartNext(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityStore.getInstance().removeActivity(activity);
    }

}
