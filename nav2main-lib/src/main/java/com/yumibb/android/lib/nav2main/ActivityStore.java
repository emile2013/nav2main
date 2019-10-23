package com.yumibb.android.lib.nav2main;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import androidx.annotation.Keep;



@Keep
public class ActivityStore {


    private static volatile CopyOnWriteArraySet activitiesInstance = new CopyOnWriteArraySet();


    private volatile static ActivityStore sInstance;

    private List<WeakReference<Activity>> mRunningActivities = new ArrayList<>();


    private ActivityStore() {
    }

    public static ActivityStore getInstance() {
        if (sInstance == null) {
            synchronized (ActivityStore.class) {
                if (sInstance == null) {
                    sInstance = new ActivityStore();
                }
            }
        }
        return sInstance;
    }

    public void putActivity(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        mRunningActivities.add(weakReference);
    }

    public void removeActivity(Activity activity) {
        for (WeakReference<Activity> activityWeakReference : mRunningActivities) {
            if (activityWeakReference == null)
                continue;
            Activity tmpActivity = activityWeakReference.get();
            if (tmpActivity == null)
                continue;
            if (tmpActivity == activity) {
                mRunningActivities.remove(activityWeakReference);
                break;
            }
        }
    }


    int getRunningActivityCount() {
        int count = 0;
        for (WeakReference<Activity> activityWeakReference : mRunningActivities) {
            if (activityWeakReference == null)
                continue;
            Activity activity = activityWeakReference.get();
            if (activity == null)
                continue;
            count++;
        }
        return count;
    }

    /**
     * 在activity finish方法中调用
     *
     * @param activity
     */
    @Keep
    public static void detectAppTask(Activity activity) {

        //activity 启动了下一个页面，不是返回
        if (activitiesInstance.contains(activity.toString())) {
            activitiesInstance.remove(activity.toString());
            return;
        }

        int count = ActivityStore.getInstance().getRunningActivityCount();
        if (count > 1) {
            return;
        }

        if (!activity.isTaskRoot()) {
            return;
        }

        Class<? extends Activity> mainClazz = Nav2Main.getInstance().getMainClazz();
        if (mainClazz == null) {
            return;
        }

        if (TextUtils.equals(mainClazz.getName(), activity.getClass().getName())) {
            return;
        }

        Nav2Main.Nav2MainCallback callback = Nav2Main.getInstance().getNav2MainCallback();

        Intent intent = new Intent(activity, mainClazz);
        if (callback != null) {
            callback.beforeRoute(activity, intent);
        }
        activity.startActivity(intent);
    }

    /**
     * record start next activity for detect if go back action
     *
     * @param activity
     */
    @Keep
    public static void recordStartNext(Activity activity) {

        if (activity == null) {
            return;
        }
        activitiesInstance.add(activity.toString());
    }

    /**
     * clear start record
     *
     * @param activity
     */
    public static void clearStartNext(Activity activity) {

        if (activity == null) {
            return;
        }
        activitiesInstance.remove(activity.toString());
    }
}
