package com.rooten.ctrl.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import com.rooten.frame.IActivityResult;
import com.rooten.frame.IResultListener;

import lib.grasp.util.FileUtil;

public class TakeImage {
    private static final String RETURN_KEY = "data";
    private static final String SELECTED_DATA = "selected_data";
    private static final String MAX_NUM = "max_num";

    public static void selectImage(Context context, IActivityResult activityResult, final ArrayList<String> selectedData, int maxNum, final onImageSelectedReturnListener l) {
        if (context == null || activityResult == null || selectedData == null || maxNum < 0) return;

        // 已经选择的文件数量超过最大可选的数量
        int selectedDataSize = selectedData.size();
        if (selectedDataSize > maxNum) return;

        // 校验
        validSelectedData(selectedData);

        Intent intent = new Intent(context, ImageManagerActivity.class);
        intent.putStringArrayListExtra(SELECTED_DATA, selectedData);
        intent.putExtra(MAX_NUM, maxNum);
        activityResult.startForResult(intent, new IResultListener() {
            @Override
            public void onResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK || data == null) return;

                ArrayList<String> selectedUrl = data.getStringArrayListExtra(RETURN_KEY);
                if (selectedUrl == null) return;

                if (l != null) l.onSelected(selectedUrl);
            }
        });
    }

    public static void selectImage(Context context, IActivityResult activityResult, int maxNum, final onImageSelectedReturnListener l) {
        ArrayList<String> selectedData = new ArrayList<>();
        selectImage(context, activityResult, selectedData, maxNum, l);
    }

    public static void selectImage(Context context, IActivityResult activityResult, final onImageSelectedReturnListener l) {
        if (context == null || activityResult == null) return;

        Intent intent = new Intent(context, ImageManagerActivity.class);
        activityResult.startForResult(intent, new IResultListener() {
            @Override
            public void onResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK || data == null) return;

                ArrayList<String> selectedUrl = data.getStringArrayListExtra(RETURN_KEY);
                if (selectedUrl == null) return;

                if (l != null) l.onSelected(selectedUrl);
            }
        });
    }

    private static void validSelectedData(ArrayList<String> selectedData) {
        if (selectedData == null || selectedData.size() == 0) return;

        ArrayList<String> tempData = new ArrayList<>();
        tempData.addAll(selectedData);

        for (String str : tempData) {
            // 遍历已选择的图片列表，删除该项
            if (!FileUtil.fileExists(str)) {
                selectedData.remove(str);
            }
        }
    }

    public interface onImageSelectedReturnListener {
        void onSelected(ArrayList<String> selectedImage);
    }
}
