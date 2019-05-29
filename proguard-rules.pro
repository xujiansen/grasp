# Add project specific ProGuard rules here.
# By a, the flags in this file are appended to flags specified
# in D:\Android\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#预校验
-dontpreverify

#混淆时是否记录日志
-verbose

#忽视警告
-ignorewarnings

# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

################################保持哪些类不被混淆#######################################
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#不混淆第三方包
-keep class android.support.multidex.** { *; }
-keep class org.java_websocket.** { *; }
-keep class net.minidev.json.** { *; }
-keep class com.thetransactioncompany.jsonrpc2.** { *; }
-keep class com.thetransactioncompany.jsonrpc2.client.** { *; }
-keep class org.webrtc.** { *; }
-keep class com.nineoldandroids.** { *; }

-keep class com.rengwuxian.materialedittext.** { *; }
-keep class com.rockerhieu.emojicon.** { *; }
-keep class uk.co.senab.photoview.** { *; }
-keep class com.squareup.picasso.** { *; }
-keep class com.android.support.** { *; }
-keep class com.joanzapata.iconify.** { *; }

-keep class com.youth.banner.** { *; }          # 轮播图(内含glide:3.6.1')
-keep class com.gongwen.marqueen.** { *; }               # 跑马灯
-keep class com.github.addappcn.** { *; }       # 选择器
-keep class com.alibaba.** { *; }               # seekbar
-keep class com.github.warkiz.widget.** { *; }  # JSON(选择器用)
-keep class com.google.zxing.** { *; }          # 二维码
-keep class com.google.gson.** { *; }           # gson
-keep class voice.** { *; }                     # 语音
-keep class com.android.volley.** { *; }        # volley

-keep class com.ipcamera.demo.** { *; }         # 摄像头
-keep class vstc2.nativecaller.** { *; }        # 摄像头

-keep class android.support.** { *; }

#高德--定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#高德--搜索
-keep   class com.amap.api.services.**{*;}

#高德--2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#腾讯云
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#okioUtil
-keep class okhttp3.** { *; }                   # okhttp
-keep class com.zhy.http.okhttp.** { *; }       # okhttp_util

#微信支付
-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.tencent.wxop.** { *; }
-keep class com.tencent.mm.sdk.** { *; }

# 支付宝支付
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}

# 权限
-dontwarn com.hjq.permissions.**

-keep class com.rooten.** { *; }
-keep class com.multi.image.** { *; }
-keep class com.zxing.** { *; }
-keep class lib.grasp.** { *; }

# 第三方picker(gzu-liyujiang/AndroidPicker)
-keepattributes InnerClasses,Signature
-keepattributes *Annotation*
-keep class cn.qqtheme.framework.entity.** { *;}

# bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }

#mqtt
-keep class org.eclipse.paho.** { *; }                              # mqtt
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#ijkplayer
-keep class tv.danmaku.ijk.media.player.** {*;}
-keep class tv.danmaku.ijk.media.player.IjkMediaPlayer{*;}
-keep class tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi{*;}

#ijkplayer
-keep class com.dueeeke.** { *; }                                   # ijkplayer
-keep class com.dueeeke.videoplayer.** { *; }                       # ijkplayer
-keep class com.github.dueeeke.dkplayer.** { *; }                   # dkplayer

#flexbox
-keepnames public class com.google.android.flexbox.FlexboxLayoutManager

# greenDao
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**