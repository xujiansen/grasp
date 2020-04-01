package com.rooten.interf;

import android.content.Intent;

/** tab的广播监听 */
public interface IActionReceiver {
    void onReceive(String action, Intent intent);
}
