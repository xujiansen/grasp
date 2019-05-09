package com.rooten.base;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lib.grasp.util.FileUtil;

public class UserData{

    /** token */
    @SerializedName("token")
    public String token;

    public String mStrPwd       = "";
    public String mStrUserPath  = "";

    public UserData() {
    }

    public void clear() {
        mStrUserPath = "";
    }

    /** 头像临时目录(压缩) */
    public String getCompressAvatarPath() {
        String path = mStrUserPath + "avatar/compress/";
        FileUtil.ensurePathExists(path);
        return path;
    }

    /** 头像临时目录(裁切) */
    public String getCropAvatarPath() {
        String path = mStrUserPath + "avatar/crop/";
        FileUtil.ensurePathExists(path);
        return path;
    }

    public void setPrivatePath(final String strPath) {
        mStrUserPath = strPath;
        FileUtil.ensurePathExists(strPath);
    }

    public boolean isError() {
        return TextUtils.isEmpty(mStrUserPath);
    }

    /** post/get 放在头里面-----系统里面的通用业务模块使用（没办法） */
    public Map<String, String> getOsHeadParam(){
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }

    public TreeMap<String, String> packData(){
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String jsonString = gson.toJson(this);
        Type type = new TypeToken<TreeMap<String, String>>() {}.getType();
        TreeMap<String, String> map = gson.fromJson(jsonString, type);
        return map;
    }
}
