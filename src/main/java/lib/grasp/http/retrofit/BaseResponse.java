package lib.grasp.http.retrofit;

/**
 *
 * 网络请求结果 基类
 * Created by zhouwei on 16/11/10.
 */

public class BaseResponse<T> {
    public int code;
    public String msg;

    public T data;

    public boolean isSuccess(){
        return code == 200;
    }
}
