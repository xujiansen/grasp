package cn.com.rooten;


import android.os.Bundle;

public interface IMainThreadInvokeListener {
    void onInvokeInMain(int what, Bundle data, Object obj);
}
