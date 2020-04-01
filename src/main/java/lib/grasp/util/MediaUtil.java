package lib.grasp.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 本地音视频播放工具类
 */
public class MediaUtil {
    private static final String TAG = "MediaUtil";

    private MediaPlayer player;
    private EventListener eventListener;

    private MediaUtil(){
        player = new MediaPlayer();
    }

    private static MediaUtil instance = new MediaUtil();

    public static MediaUtil getInstance(){
        return instance;
    }

    public MediaPlayer getPlayer() {
        return player;
    }


    public void setEventListener(final EventListener eventListener) {
        if (player != null){
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    eventListener.onStop();
                }
            });
        }
        this.eventListener = eventListener;
    }

    public void play(FileInputStream inputStream){
        try{
            if (eventListener != null){
                eventListener.onStop();
            }
            player.reset();
            player.setDataSource(inputStream.getFD());
            player.prepare();
            player.start();
        }catch (IOException e){
            Log.e(TAG, "play error:" + e);
        }
    }

    public int playAndGetDuring(FileInputStream inputStream){
        try{
            if (eventListener != null){
                eventListener.onStop();
            }
            player.reset();
            player.setDataSource(inputStream.getFD());
            player.prepare();
            player.start();
            return player.getDuration();
        }catch (IOException e){
            Log.e(TAG, "play error:" + e);
            return -1;
        }
    }

    public void playNetMedia(Context context, String url, Map<String, String> map){
        try{
            if (eventListener != null){
                eventListener.onStop();
            }
            Uri uri = Uri.parse(url);
            player.reset();
            player.setDataSource(context, uri, map);
            player.prepare();
            player.start();
        }catch (IOException e){
            Log.e(TAG, "play error:" + e);
        }
    }

    public static FileInputStream getStream(String filepath){
        try{
            File file = new File(filepath);
            return new FileInputStream(file);
        }catch (IOException e){
            Log.e(TAG, "play error:" + e);
        }
        return null;
    }


    public void stop(){
        if (player != null && player.isPlaying()){
            player.stop();
        }
    }

    public long getDuration(Context context, String path){
        player = MediaPlayer.create(context, Uri.parse(path));
        return player.getDuration();
    }


    /**
     * 播放器事件监听
     */
    public interface EventListener{
        void onStop();
    }




    /** 获取媒体音量 */
    public static int getMediaVol(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager == null) return -1;
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /** 设置媒体音量(不是百分比) */
    public static void setMediaVol(Context context, int vol){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager == null) return;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_PLAY_SOUND);
    }
}
