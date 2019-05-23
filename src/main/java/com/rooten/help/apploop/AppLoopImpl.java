package com.rooten.help.apploop;

import com.rooten.BaApp;

import lib.grasp.util.L;

/**
 * 重复实现类
 */
public class AppLoopImpl {
    public static void onLooper(BaApp app, int looperId) {
        L.logAndWrite(AppLoopImpl.class.getSimpleName() + "AppLoopImpl--onLooper------------looperId:" + looperId);
//        switch (looperId) {
//            case Constant.ID_POLL: {
//                long curSysTime = System.currentTimeMillis();
//                long preTime = getPreLoopTime(app, KEY_APP_LOOP);
//                long scaleTime = curSysTime - preTime;
//
//                // 如果时间间隔小于5分钟就不处理
//                if (scaleTime < Constant.TIME_POLL * 1000) break;
////                if (scaleTime < Constant.TIME_POLL * 60 * 1000) break;
//
//                saveLoopTime(app, KEY_APP_LOOP, curSysTime);        // 保存当前的时间
//
//                checkAppExist(app);                                 // 校验app是否在线
//
////                app.startAppOnService();                            // 再次启动服务
//                break;
//            }
//
//            case Constant.ID_HEARTBEAT: {
////                app.sendHeartbeat();    // 发心跳
////                LoopHelper.startHeartbeat(app);
//                break;
//            }
//
//            case Constant.ID_RELOGIN: {
//                break;
//            }
//
//            case Constant.ID_APPUPGRADE: {
//                break;
//            }
//            default_video:
//                break;
//        }
    }

//    /**
//     * 保证APP存在并且在前台
//     */
//    private static void checkAppExist(BaApp app) {
//        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "checkAppExist--校验app是否在线------------:" + AppUtil.isAppRun(app, "com.gaqu.adplayer65"));
//
//        if (!AppUtil.isAppRun(app, "com.gaqu.adplayer65")) {
//            Intent intent = new Intent("android.intent.action.gaqu_adplayer");
//            app.sendBroadcast(intent);
//            return;
//        }
//
//        AppUtil.setTopApp(app);
//    }


}
