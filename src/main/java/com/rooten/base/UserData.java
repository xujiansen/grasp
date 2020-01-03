package com.rooten.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UserData{

    /** token */
    @SerializedName("token")
    public String   mToken;

    /** 用户ID */
    public String	mStrUserID 		= "";
    /** 用户名称 */
    public String	mStrName   		= "";
    /** 用户密码(登录时输入) */
    public String   mStrPwd         = "";

    /** post/get 放在头里面-----系统里面的通用业务模块使用（没办法） */
    public Map<String, String> getOsHeadParam(){
        HashMap<String, String> map = new HashMap<>();
        map.put("token", mToken);
        return map;
    }

    /** 打包user实体类 */
    public TreeMap<String, String> packData(){
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String jsonString = gson.toJson(this);
        Type type = new TypeToken<TreeMap<String, String>>() {}.getType();
        TreeMap<String, String> map = gson.fromJson(jsonString, type);
        return map;
    }
}
