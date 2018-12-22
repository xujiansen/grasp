package cn.com.rooten.camera;

import android.os.Message;

interface IHandler {
    /**
     * 已经处理返回true，否则返回false向下处理
     *
     * @param msg
     * @return
     */
    boolean handleMessage(Message msg);
}
