package com.rooten;

public class Constant {
    // 系统
    public static boolean APP_DEBUG = false;

    // 界面风格设置
    public static final int COLOR_TOOLBAR = 0xFFCDE7EF;
    public static final boolean PAGE_USER_BORDER = false;   // 页面使用边框
    public static final int PAGE_PADDING = 1;               // 页内填充

    // 定期任务的时间和执行任务ID
    public static final int TIME_POLL = 30;             // 轮询，service监听app是否在(秒)

    public final static int ID_HEARTBEAT = 1003;        // 心跳id
    public final static int ID_RELOGIN = 1009;          // 重连id
    public final static int ID_POLL = 1012;             // 轮询id
    public final static int ID_APPUPGRADE = 1016;       // 版本更新id

    private Constant() { }

    /** token失效广播 */
    public static final String ARG_TOKEN_EXPIRE         = "grasp.arg.token.expire";
    /** 版本升级 */
    public static final String ARG_NEW_VERSION          = "grasp.arg.new.version";
    /** 轮询广播(必须播放)-保活 */
    public static final String ARG_BROADCAST_LOOP       = "grasp.arg.loop";
    /** 轮询广播-调起远程服务 */
    public static final String ARG_ACTION_REMOTE_SERVICE= "com.rooten.help.apploop.AppLoopService";


    /** 验证码获取间隔(秒) */
    public static final int YZM_VAILDATE_TIME = 60;

    /** 最大回看的月 */
    public static final int MAX_DISTANCE = 12;

    /** 回看的viewpager的真实的页数 */
    public static final int MAX_PAGE_COUNT 	= 5;

    /** 裁切图片后罪名 */
    public static final String SUFFIX_AVATAR_NAME = ".gqpng";

    /** 阿里云（测试机-windows） */
    public static final String PROTOCOL = "http";
    public static final String IP       = "192.168.1.78";
    public static final String PORT     = "8082";

    /** 上传图片 */
//    public static final String UPLOAD_PIC               = PROTOCOL + "://" + IP + ":" + PORT + "/api/upload";
    public static final String UPLOAD_PIC               = PROTOCOL + "://" + "192.168.1.20" + ":" + "8080" + "/MyUrlSample/upload";
}
