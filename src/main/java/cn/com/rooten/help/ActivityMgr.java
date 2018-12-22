package cn.com.rooten.help;

import android.app.Activity;

import java.util.ArrayList;

public class ActivityMgr {
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


    public void onDestroy() {
        // 遍历Activity列表，销毁Activity
        for (Activity activity : mActivityList) {
            activity.finish();
        }

        // 清空列表
        mActivityList.clear();
    }
}
