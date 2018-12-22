package cn.com.rooten.help.apploop;

import android.content.Context;
import android.content.SharedPreferences;

import cn.com.rooten.BaApp;
import cn.com.rooten.Constant;
import cn.com.rooten.help.ActivityMgr;
import lib.grasp.util.L;

/**
 * 重复实现类
 */
public class AppLoopImpl {
    public static void onLooper(BaApp app, int looperId) {
        switch (looperId) {
            case Constant.ID_POLL: {
                long curSysTime = System.currentTimeMillis();
                long preTime = getPreLoopTime(app, KEY_APP_LOOP);
                long scaleTime = curSysTime - preTime;

                // 如果时间间隔小于5分钟就不处理
                if (scaleTime < 5 * 60 * 1000) break;

                // 保存当前的时间
                saveLoopTime(app, KEY_APP_LOOP, curSysTime);

                // 校验app是否在线
                checkAppExist(app);

                // 再次启动服务
//                app.startAppOnService();

                // 校验GPS是否打开
                checkGpsEnable(app);
                break;
            }

            case Constant.ID_RELOGIN: {
                L.logAndWrite(AppLoopImpl.class.getSimpleName(), "ID_RELOGIN");

                // 再次重连
//                app.getAppReLoginMgr().onReLogin();
                break;
            }

            case Constant.ID_APPUPGRADE: {
                break;
            }

            case Constant.ID_HEARTBEAT: {
//                app.sendHeartbeat();    // 发心跳
                TaskUtil.startHeartbeat(app);
                break;
            }

            default:
                break;
        }
    }

    /**
     * 检验App是否存在
     */
    private static void checkAppExist(BaApp app) {
        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "checkAppExist--1111");

        ActivityMgr activityMgr = app.getActivityMgr();
//        if (app.isOnline()) return;

        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "checkAppExist--2222");

        // 如果Activity的列表为空说明之前程序被杀掉了
        if (activityMgr == null || activityMgr.isEmpty()) {
            L.logAndWrite(AppLoopImpl.class.getSimpleName(), "checkAppExist--3333");

            // 重新启动-重连成功之后设置notification
//            app.getAppReLoginMgr().onReLaunchApp();
        } else {
            L.logAndWrite(AppLoopImpl.class.getSimpleName(), "checkAppExist--4444");

            // 如果程序没被杀,就是断线了,重连即可
//            app.getAppReLoginMgr().onReLoginNow();
        }
    }

    private static void checkGpsEnable(BaApp app) {

    }

    /*********************************************辅助方法*************************************************/
    private static final String APP_LOOP_FILE = "app_loop.txt";
    private static final String KEY_APP_LOOP = "KEY_APP_LOOP";

    private static void saveLoopTime(BaApp app, String loopKey, long time) {
        SharedPreferences sharedPreferences = app.getSharedPreferences(APP_LOOP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(loopKey, time);
        edit.apply();
    }

    private static long getPreLoopTime(BaApp app, String loopKey) {
        SharedPreferences sharedPreferences = app.getSharedPreferences(APP_LOOP_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(loopKey, 0l);
    }
}
