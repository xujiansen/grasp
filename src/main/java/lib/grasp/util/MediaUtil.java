package lib.grasp.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 音视频
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
}
