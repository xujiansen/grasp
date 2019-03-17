package com.rooten;

import android.content.Intent;

public interface IActionReceiver {
    void onReceive(String action, Intent intent);
}
