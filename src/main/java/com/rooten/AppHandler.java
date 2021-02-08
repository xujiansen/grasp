package com.rooten;

import android.os.Handler;
import android.os.Message;

import com.rooten.interf.IHandler;

import java.lang.ref.WeakReference;

/** 防止内存泄露 */
public class AppHandler extends Handler {
    private WeakReference<IHandler> mHandle = null;

    public AppHandler(IHandler h) {
        mHandle = new WeakReference<>(h);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mHandle == null) return;
        IHandler h = mHandle.get();
        if (h == null) return;
        h.handleMessage(msg);
    }
}
