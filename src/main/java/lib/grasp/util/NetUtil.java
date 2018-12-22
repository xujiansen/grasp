package lib.grasp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 */
public class NetUtil {

    public static boolean isNetConnected(Context cxt) {
        ConnectivityManager connectivityManager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNet = connectivityManager.getActiveNetworkInfo();
        return activeNet != null && activeNet.isConnected();
    }

    public static boolean isNetConnected2(Context cxt) {
        ConnectivityManager connectivityManager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }
}
