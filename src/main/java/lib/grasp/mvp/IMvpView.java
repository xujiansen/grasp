package lib.grasp.mvp;

import androidx.fragment.app.FragmentActivity;

public interface IMvpView {

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    <T extends FragmentActivity> T getSelfActivity();
}