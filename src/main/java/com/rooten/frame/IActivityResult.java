package com.rooten.frame;

import android.content.Intent;

public interface IActivityResult {
    void startForResult(Intent intent, IResultListener listener);
}
