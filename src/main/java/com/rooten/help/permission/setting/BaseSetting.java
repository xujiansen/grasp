package com.rooten.help.permission.setting;

import android.app.Activity;
import android.content.Intent;

/**
 * 设置基类
 */

class BaseSetting {
    protected static void startActivity(Activity activity, Intent intent) {
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
