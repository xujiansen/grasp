package lib.grasp.widget.pressbtn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lib.grasp.R;
import lib.grasp.util.FileUtil;

public class PressSpeakButton extends AppCompatButton {
    public interface RecordListener {
        /** 录制完成,发送 */
        void onNewAudioMsgInput(float during, String filePath);
    }
    private RecordListener mRecordListener;
    public void setRecordListener(RecordListener listener){
        this.mRecordListener = listener;
    }

    /** 音量通知 */
    private final int TAG_VOICE     = 0;
    /** 倒计时通知 */
    private final int TAG_TIMELEFT  = 1;
    /** 超时通知 */
    private final int TAG_TIMEOUT   = 2;

    private final String PRESS_TO_TALK          = "按住 说话";
    private final String RELEASE_TO_SEND        = "松开 发送";
    private final String RELEASE_TO_CANCLE      = "松开手指, 取消发送";
    private final String RELEASE_TO_CANCLE_EXPL = "松开手指可取消录音";
    private final String UP_TO_CANCLE_EXPL      = "向上滑动可取消录音";

    private Context mContext;

    private Vibrator mVibrator;
    private long[]  mPattern = {0,100};      // 震动停止,开启
    private float   downY;                   // 手指拖动的实际距离
    private float   MAX_DISTANCE_Y;          // 手指拖动的最大距离

    private int     mRecordState = 0;        // 录音状态
    private float   mRecodeTime  = 0.0f;     // 实际录音时长，如果录音时间太短则录音失败
    private double  voiceValue   = 0.0;      // 录音的音量值
    private boolean mIsCanceled  = false;    // 是否取消录音

    private static final int RECORD_OFF = 0;        // 不在录音
    private static final int RECORD_PRE = 1;        // 预备录音
    private static final int RECORD_ON  = 2;        // 正在录音

    private static final int MIN_RECORD_TIME    = 1;   // 最短录音时间，单位秒
    private static final int LEFT_NITI_TIME     = 5;   // 最后倒计时，单位秒
    private static final int MAX_RECORD_TIME    = 10;   // 最短录音时间，单位秒

    private Dialog      mRecordDialog;
    private TextView    mDialogTextView;
    private TextView    mDialogTextTime;
    private ImageView   mDialogImg;

    private Drawable    mBtnNormal;
    private Drawable    mBtnPressed;

    private Thread      mRecordThread;

    private AudioRecorder recorder;

    public void init(String savePath) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        System.out.println(wm.getDefaultDisplay().getHeight());
        MAX_DISTANCE_Y = wm.getDefaultDisplay().getHeight()/10;

        recorder = new AudioRecorder(mContext, savePath);
        mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mBtnNormal = getResources().getDrawable(R.drawable.ic_btn_orange_normal);
        mBtnPressed = getResources().getDrawable(R.drawable.ic_btn_orange_pressed);
        this.setTextColor(Color.WHITE);
        this.setText(PRESS_TO_TALK);
        this.setBackground(mBtnNormal);
    }

    // 录音时显示Dialog
    private void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog   = new Dialog(mContext, R.style.Dialogstyle);
            mRecordDialog.setContentView(R.layout.dialog_record);
            mDialogImg       = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mDialogTextView  = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
            mDialogTextView.setTextSize(14);
            mDialogTextTime  = (TextView) mRecordDialog.findViewById(R.id.record_time);
            mDialogTextTime.setTextSize(12);
        }

        switch (flag) {
            case 1: //按钮内松开就取消
                mDialogImg.setImageResource(R.drawable.record_cancel);
                mDialogTextView.setText(UP_TO_CANCLE_EXPL);
                break;
            case 2: //按钮外松开就取消
                mDialogImg.setImageResource(R.drawable.record_cancel);
                mDialogTextView.setText(RELEASE_TO_CANCLE_EXPL);
                break;
            default: break;
        }
        mRecordDialog.show();
    }


    private class StartTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mRecordState = RECORD_PRE;
            mIsCanceled     = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mIsCanceled) {               //若还没真正开始就松开手指
                mRecordState = RECORD_OFF;  //放弃录音
                return;
            }
            mRecordState = RECORD_ON;
            callRecordTimeThread();     //开启计时/监听音量线程
            showVoiceDialog(1);         //显示
            if(mDialogTextTime != null) mDialogTextTime.setText("");
            mVibrator.vibrate(mPattern, -1);
            if(mRecordListener == null) return;
            recorder.startRecording();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下按钮
                doPressButton(event);
                break;

            case MotionEvent.ACTION_MOVE: // 滑动手指
                dodragButton(event);
                break;

            case MotionEvent.ACTION_UP: // 松开手指
                doReleaseButton();
                break;
        }
        return true;
    }

    /** 按下手指 */
    private void doPressButton(MotionEvent event){
        PressSpeakButton.this.setBackground(mBtnPressed);
        PressSpeakButton.this.setText(RELEASE_TO_SEND);

        if(mRecordState == RECORD_PRE||mRecordState == RECORD_ON) return;
        downY = event.getY();
        new StartTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Void)null);
    }

    /** 拖动手指 */
    private void dodragButton(MotionEvent event){
        float moveY = event.getY();
        if (mRecordState == RECORD_ON && downY - moveY > MAX_DISTANCE_Y) {   //按钮外
            PressSpeakButton.this.setText(RELEASE_TO_CANCLE);
            mIsCanceled = true;
            showVoiceDialog(2);
        }
        if (mRecordState == RECORD_ON && downY - moveY < MAX_DISTANCE_Y) {   //按钮内
            PressSpeakButton.this.setText(RELEASE_TO_SEND);
            mIsCanceled = false;
        }
    }

    /** 松开手指 */
    private void doReleaseButton(){
        PressSpeakButton.this.setBackground(mBtnNormal);
        PressSpeakButton.this.setText(PRESS_TO_TALK);

        if (mRecordDialog != null && mRecordDialog.isShowing())  mRecordDialog.dismiss();

        //若还没来得及开始录音
        if(mRecordState != RECORD_ON){
            mIsCanceled = true;
            return;
        }

        mRecordState = RECORD_OFF;
        voiceValue = 0.0;

        //以下可注释掉
        if (mIsCanceled) Toast.makeText(mContext,"取消", Toast.LENGTH_SHORT).show();
        else if(mRecodeTime < MIN_RECORD_TIME) Toast.makeText(mContext,"时间太短", Toast.LENGTH_SHORT).show();
        else Toast.makeText(mContext,"录音成功", Toast.LENGTH_SHORT).show();


        recorder.stopRecording();
        if (mIsCanceled || mRecodeTime < MIN_RECORD_TIME) FileUtil.delFile(recorder.getFilePath());
        else if(mRecordListener != null) mRecordListener.onNewAudioMsgInput(mRecodeTime, recorder.getFilePath());
    }

    /** 音量线程 */
    private Runnable recordThread = new Runnable() {
        @Override
        public void run() {
            mRecodeTime = 0.0f;
            while (mRecordState == RECORD_ON) {
                try {
                    Thread.sleep(100);
                    mRecodeTime += 0.1;
                    if (!mIsCanceled && mRecordListener!=null) {
                        recordHandler.sendEmptyMessage(TAG_VOICE);
                    }
                    int leftTime = (int) (MAX_RECORD_TIME - mRecodeTime);
                    if(leftTime <= 0){
                        recordHandler.sendEmptyMessage(TAG_TIMEOUT);
                        break;
                    }
                    if(LEFT_NITI_TIME >= leftTime){
                        Message msg = new Message();
                        msg.what = TAG_TIMELEFT;
                        msg.arg1 = leftTime;
                        recordHandler.sendMessage(msg);
                    }
                }
                catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }
    };

    // 开启音量/计时线程
    private void callRecordTimeThread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
//        recorder.startRecording();
    }

    @SuppressLint("HandlerLeak")
    private Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TAG_VOICE:{
                    voiceValue = recorder.getAmplitude();
                    setDialogImage();
                    break;
                }
                case TAG_TIMELEFT:{
                    if(mDialogTextTime != null) mDialogTextTime.setText("还剩" + msg.arg1 + "秒");
                    break;
                }
                case TAG_TIMEOUT:{
                    doReleaseButton();
                    break;
                }
            }
        }
    };

    // 录音Dialog图片随录音音量大小切换
    private void setDialogImage() {
//        L.log("setDialogImage", voiceValue);
        if (voiceValue < 600.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_01);
        } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_02);
        } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_03);
        } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_04);
        } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_05);
        } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_06);
        } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_07);
        } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_08);
        } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_09);
        } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_10);
        } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_11);
        } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_12);
        } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_13);
        } else if (voiceValue > 12000.0) {
            mDialogImg.setImageResource(R.drawable.record_animate_14);
        }
    }


    public PressSpeakButton(Context context) {
        super(context);
        mContext = context;
    }

    public PressSpeakButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PressSpeakButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }
}
