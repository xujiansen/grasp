package lib.grasp.test;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.rooten.frame.AppActivity;
import cn.com.rooten.util.Utilities;
import lib.grasp.R;
import lib.grasp.helper.DownLoadHelper;
import lib.grasp.helper.UpLoadHelper;
import lib.grasp.http.okhttpprogress.ProgressHelper;
import lib.grasp.http.okhttpprogress.ProgressRequestBody;
import lib.grasp.http.okhttpprogress.UIProgressRequestListener;
import lib.grasp.http.okhttpprogress.UIProgressResponseListener;
import lib.grasp.util.ApkUtil;
import lib.grasp.util.FileUtil;
import lib.grasp.util.NumberUtil;
import lib.grasp.util.TOAST;
import lib.grasp.widget.MessageBoxGrasp;
import lib.grasp.widget.banner.AdBannerDialogGrasp;
import lib.grasp.widget.banner.BannerEntity;
import lib.grasp.widget.banner.BannerGrasp;
import lib.grasp.widget.diaglog.CheckMultiEntity;
import lib.grasp.widget.diaglog.CheckOneEntity;
import lib.grasp.widget.diaglog.RadioOneEntity;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 */
public class LoadTestActivity extends AppActivity implements View.OnClickListener {

    private View mView;

    private String mDownUrl     = "http://192.168.1.20:8080/MyUrlSample/mobileqq_android.apk";
    private String mDownPath    = Environment.getExternalStorageDirectory().getPath() + "/zdown/mobileqq_android.apk";


    private String mUpUrl           = "http://192.168.1.20:8080/MyUrlSample/upload";
    private String mLocalFilePath   = Environment.getExternalStorageDirectory().getPath() + "/zdown/mobileqq_android.apk";

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mView = View.inflate(this, R.layout.test_grasp, null);
        installView(mView, 1);
        init();
    }

    private void init() {
        initMyView();
    }

    private void initMyView() {
        mView.findViewById(R.id.btn1).setOnClickListener(this);
        mView.findViewById(R.id.btn2).setOnClickListener(this);
        mView.findViewById(R.id.btn3).setOnClickListener(this);
        mView.findViewById(R.id.btn4).setOnClickListener(this);
        mView.findViewById(R.id.btn5).setOnClickListener(this);
        mView.findViewById(R.id.btn6).setOnClickListener(this);
        mView.findViewById(R.id.btn7).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn1) {
            download();
        } else if (i == R.id.btn2) {
            upload();
        } else if (i == R.id.btn3) {
            download_Test();
        } else if (i == R.id.btn4) {
            upload_Test();
        } else if (i == R.id.btn5) {
            showRadioOne();
        }else if (i == R.id.btn6) {
            showCheckOne();
        }else if (i == R.id.btn7) {
            showCheckMulti();
        }
    }

    /**
     * 下载
     */
    private void download() {
        //这个是ui线程回调，可直接操作UI
        UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                System.out.println("------onUIRequestProgress:" + NumberUtil.getProgress(bytesRead, contentLength));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("------onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws EOFException {
                File file = new File(mDownPath);
                FileUtil.saveOkHttpFile(response, file);
            }
        };

        //构造请求
        final Request request1 = new Request
                .Builder()
                .url(mDownUrl)
                .build();

        //包装Response使其支持进度回调
        ProgressHelper
                .addProgressResponseListener(uiProgressResponseListener)
                .newCall(request1)
                .enqueue(uiProgressResponseListener);
    }

    /**
     * 上传
     */
    private void upload() {
        File file = new File(mLocalFilePath);
        final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
            @Override
            public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
                System.out.println("------onUIRequestProgress:" + NumberUtil.getProgress(bytesWrite, contentLength));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("------onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("------onResponse:" + response);
            }
        };

        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/octet-stream"), file);

        MultipartBody multipartBody = new MultipartBody
                .Builder("BbC04y")
                .addPart(requestBody)
                .build();

        ProgressRequestBody progressRequestBody = ProgressHelper
                .addProgressRequestListener(multipartBody, uiProgressRequestListener);

        final Request request = new Request
                .Builder()
                .url(mUpUrl)
                .post(progressRequestBody)
                .build();

        new OkHttpClient()
                .newCall(request)
                .enqueue(uiProgressRequestListener);
    }

    /**
     * 下载
     */
    private void download_Test() {
        DownLoadHelper loadHelper = new DownLoadHelper(this, mDownUrl, mDownPath);
        loadHelper.setLoadListener(new lib.grasp.helper.LoadListener() {
            @Override
            public void onSuccess() {
                doConfirmToInstall();
            }

            @Override
            public void onFail() {
                doIndicateFailMsg();
            }
        });
        loadHelper.startLoad();
    }

    /**
     * 上传
     */
    private void upload_Test() {
        String localFilePath = mLocalFilePath;
        List<String> list = new ArrayList<>();
        list.add(localFilePath);

        UpLoadHelper loadHelper = new UpLoadHelper(this, mUpUrl, list);
        loadHelper.setLoadListener(new lib.grasp.helper.LoadListener() {
            @Override
            public void onSuccess() {
                MessageBoxGrasp.infoMsg(LoadTestActivity.this, "上传完成");
            }

            @Override
            public void onFail() {
                MessageBoxGrasp.infoMsg(LoadTestActivity.this, "上传失败");
            }
        });
        loadHelper.startLoad();
    }

    /** 确认安装 */
    private void doConfirmToInstall(){
        MessageBoxGrasp.confirmMsg(this, "下载完成,现在安装?", v -> {
            if(TextUtils.isEmpty(mDownPath)) return;
            ApkUtil.installAPK(LoadTestActivity.this, new File(mDownPath));
        });
    }

    /** 下载失败提示 */
    private void doIndicateFailMsg(){
        MessageBoxGrasp.infoMsg(LoadTestActivity.this, "下载失败, 请稍候再试");
    }

    /**
     * radio_one
     */
    private void showRadioOne() {
        MessageBoxGrasp.radioOne(this, "测试选择一个就消失", true, RadioOneEntity.getTestDatas(), v -> {
            Object o = v.getTag();
            if(!(o instanceof RadioOneEntity)) return;
            RadioOneEntity entity = (RadioOneEntity)o;
            TOAST.showShort(LoadTestActivity.this, entity.name);
        });
    }

    /**
     * check_one
     */
    private void showCheckOne() {
        MessageBoxGrasp.checkOne(this, "测试选择一个,点确定消失", true, CheckOneEntity.getTestDatas(), v -> {
            Object o = v.getTag();
            if(!(o instanceof CheckOneEntity)) return;
            CheckOneEntity entity = (CheckOneEntity)o;
            TOAST.showShort(LoadTestActivity.this, entity.name);
        }, null);
    }

    /**
     * check_multi
     */
    private void showCheckMulti() {
        MessageBoxGrasp.checkMulti(this, "测试选择一个,点确定消失", true, CheckMultiEntity.getTestDatas(), v -> {
            Object o = v.getTag();
            if(!(o instanceof List)) return;
            List<CheckOneEntity> entityList = (List<CheckOneEntity>)o;
            TOAST.showShort(LoadTestActivity.this, String.valueOf(entityList.size()));
        }, null);

    }
}
