package com.rooten.help;

import android.app.Activity;

import java.util.ArrayList;

/** Activity管理器 */
public class ActivityMgr {

    /** 单例 */
    private static volatile ActivityMgr defaultInstance;

    /** Activity管理器 */
    public static ActivityMgr getDefault() {
        if (defaultInstance == null) {
            synchronized (ActivityMgr.class) {
                if (defaultInstance == null) {
                    defaultInstance = new ActivityMgr();
                }
            }
        }
        return defaultInstance;
    }

    private ArrayList<Activity> mActivityList = new ArrayList<>();

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    public boolean containsActivity(Class<? extends Activity> cls) {
        for (Activity activity : mActivityList) {
            String name1 = activity.getClass().getName();
            String name2 = cls.getName();
            if (name1.equals(name2)) return true;
        }
        return false;
    }

    public Activity getActivity(Class<?> cls) {
        for (Activity activity : mActivityList) {
            String name1 = activity.getClass().getName();
            String name2 = cls.getName();
            if (name1.equals(name2)) return activity;
        }
        return null;
    }

    public void delActivity(Class<?> cls) {
        Activity activity = getActivity(cls);
        if (activity == null) return;

        mActivityList.remove(activity);
        activity.finish();
        activity = null;
    }

    public boolean isEmpty() {
        return mActivityList.isEmpty();
    }

    /**
     * 1. 手动Finish所有的Activity
     * <br/>
     * 2. 清空成员变量(Activity列表)
     */
    public void onDestroy() {
        // 遍历Activity列表，销毁Activity
        for (Activity activity : mActivityList) {
            activity.finish();
        }

        // 清空列表
        mActivityList.clear();
    }
}
