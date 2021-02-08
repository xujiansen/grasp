package lib.grasp.mvp;

import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

/**
 * Presenter的宿主Activity或Fragment
 */
public interface IMvpView {

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    <T extends FragmentActivity> T getHostActivity();

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    Handler getHostHandler();
}