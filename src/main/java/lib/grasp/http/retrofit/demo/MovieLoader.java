package lib.grasp.http.retrofit.demo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import lib.grasp.http.retrofit.BaseResponse;
import lib.grasp.http.retrofit.ObjectLoader;
import lib.grasp.http.retrofit.RetrofitServiceManager;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 * Movie相关接口
 * Created by zhouwei on 16/11/10.
 */

public class MovieLoader extends ObjectLoader {

    public interface MovieService {

        /**
         * GetForm请求
         */
        @GET("/")
        Observable<BaseResponse<ArrayList<Integer>>> getForm(@Query("start") String start, @Query("count") String count);

        /**
         * PostForm请求
         */
        @FormUrlEncoded
        @POST("/")
        Observable<BaseResponse<ArrayList<Integer>>> postForm(@Field("cityId") String cityId, @Field("key") String key);

        /**
         * PostJson请求
         */
        @POST("/")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<BaseResponse<ArrayList<Integer>>> postJson(@Body Object object);
    }

    private MovieService mMovieService;
    public MovieLoader(Context context, boolean mIsShowDialog, String mInfoStr) {
        super(context, mIsShowDialog, mInfoStr);
        mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
    }

    /**
     * GetForm请求
     */
    public Observable<List<Integer>> getForm(String cityId, String key) {
        return observe(mMovieService.getForm(cityId, key))
                .map(new Func1<BaseResponse<ArrayList<Integer>>, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call(BaseResponse<ArrayList<Integer>> list) {
                        return list.data;
                    }
                });
    }

    /**
     * postForm
     */
    public Observable<List<Integer>> postForm(String cityId, String key) {
        return observe(mMovieService.postForm(cityId, key))
                .map(new Func1<BaseResponse<ArrayList<Integer>>, List<Integer>>() {
                    @Override
                    public List<Integer> call(BaseResponse<ArrayList<Integer>> list) {
                        return list.data;
                    }
                });
    }

    /**
     * PostJson请求
     */
    public Observable<List<Integer>> postJson(String cityId, String key) {
        return observe(mMovieService.postJson(new Exception("123")))
                .map(new Func1<BaseResponse<ArrayList<Integer>>, List<Integer>>() {
                    @Override
                    public List<Integer> call(BaseResponse<ArrayList<Integer>> list) {
                        return list.data;
                    }
                });
    }
}
