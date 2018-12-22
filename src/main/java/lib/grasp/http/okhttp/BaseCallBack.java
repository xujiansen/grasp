package lib.grasp.http.okhttp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: shantoo on 2017/3/30 16:23.
 */
public abstract class BaseCallBack<T> extends Callback<BaseResponse<T>> {

    public BaseCallBack() { }

    @Override
    public BaseResponse<T> parseNetworkResponse(Response response, int id) throws Exception {
        String jsonString = response.body().string();
        BaseResponse<T> res = new Gson().fromJson(jsonString, new TypeToken<BaseResponse<T>>() {}.getType());
        return res;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
    }
}