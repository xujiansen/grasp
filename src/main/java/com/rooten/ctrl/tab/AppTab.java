package com.rooten.ctrl.tab;


import android.app.Fragment;
import android.view.View;

class AppTab {
    String title = "";
    View itemView;
    Fragment fragment;
    boolean isPressed = false;
    int iconNormalRes = -1;
    int iconPressedRes = -1;

    AppTab() {
        isPressed = false;
    }
}
