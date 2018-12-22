package cn.com.rooten.util;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PowerUtil {
    private Context mContext;
    private WakeLock mWakeLock;

    public PowerUtil(Context context) {
        mContext = context;
    }

    public void acquireCpu() {
        if (mWakeLock == null) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
        }
        if (mWakeLock.isHeld()) return;
        mWakeLock.acquire(60 * 1000);
    }

    public void acquireCpu(long timeout) {
        if (mWakeLock == null) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
        }
        if (mWakeLock.isHeld()) return;
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire(timeout);
    }

    public void releaseCpu() {
        if (mWakeLock == null) return;
        if (!mWakeLock.isHeld()) return;
        mWakeLock.release();
    }
}
