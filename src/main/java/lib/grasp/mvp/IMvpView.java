package lib.grasp.mvp;

import android.support.v4.app.FragmentActivity;

public interface IMvpView {

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    <T extends FragmentActivity> T getSelfActivity();
}