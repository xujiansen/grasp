package com.rooten.frame;

import android.os.Message;

public interface IHandler {
    /**
     * 已经处理返回true，否则返回false向下处理
     *
     * @param msg
     * @return
     */
    boolean handleMessage(Message msg);
}
