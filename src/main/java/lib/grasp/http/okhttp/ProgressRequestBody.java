package lib.grasp.http.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RecoverySystem;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by GaQu_Dev on 2018/8/16.
 */
public class ProgressRequestBody extends RequestBody {
    public static final int UPDATE = 0x01;
    private RequestBody requestBody;
    private RecoverySystem.ProgressListener mListener;
    private BufferedSink bufferedSink;
    private MyHandler myHandler;

    public ProgressRequestBody(RequestBody body, RecoverySystem.ProgressListener listener) {
        requestBody = body;
        mListener = listener;
        if (myHandler == null) {
            myHandler = new MyHandler();
        }
    }

    class MyHandler extends Handler {
        //放在主线程中显示
        public MyHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    if (mListener != null)
                        mListener.onProgress(getProgress(progressModel.mBytesWritten, progressModel.mContentLength));
                    break;

            }
        }
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //刷新
        bufferedSink.flush();
    }

    /** 渐渐沉入 */
    private Sink sink(BufferedSink sink) {

        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                //回调
                Message msg = Message.obtain();
                msg.what = UPDATE;
                msg.obj = new ProgressModel(bytesWritten, contentLength, bytesWritten == contentLength);
                myHandler.sendMessage(msg);
            }
        };
    }

    public class ProgressModel {
        long mBytesWritten;
        long mContentLength;
        boolean isFinished;

        public ProgressModel(long mBytesWritten, long mContentLength, boolean isFinished) {
            this.mBytesWritten = mBytesWritten;
            this.mContentLength = mContentLength;
            this.isFinished = isFinished;
        }
    }

    private int getProgress(long size, long allsize) {
        float s = size;
        float per = s / allsize;
        BigDecimal b = new BigDecimal(per);
        per = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return (int) (per * 100);
    }
}
