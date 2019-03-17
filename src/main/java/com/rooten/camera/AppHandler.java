package com.rooten.camera;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

class AppHandler extends Handler {
    private WeakReference<IHandler> mHandle = null;

    AppHandler(IHandler h) {
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
