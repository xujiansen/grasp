package lib.grasp.mvp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;


/**
 * Presenter基类
 * <br/>
 * V: Presenter的宿主Activity或Fragment
 */
public class BaseMvpPresenter<V extends IMvpView> implements IMvpPresenter<V> {
    /**
     * 宿主View(一般是Activity/Fragment)<br/>
     * 防止 Activity/Fragment 不走 onDestory() 方法，所以采用弱引用来防止内存泄漏
     */
    protected WeakReference<V> mViewRef;

    /** 初始化Presenter(同时需要传入宿主View) */
    public BaseMvpPresenter(@NonNull V host) {
        attachView(host);
    }

    /**
     * 获取在本Presenter注册的View
     * <br/>
     * 注意判断, 防止宿主界面被销毁
     */
    @Nullable
    protected <T extends FragmentActivity> T getActivity(){
        if(mViewRef == null) return null;
        if(mViewRef.get() == null) return null;
        return mViewRef.get().getHostActivity();
    }

    /** 获取在本Presenter对宿主View的引用 */
    @Override
    public void attachView(V iMvpView) {
        if (mViewRef == null || mViewRef.get() == null) {
            mViewRef = new WeakReference<>(iMvpView);
        }
    }

    /** 获取在本Presenter对宿主View的引用 */
    @Override
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    /** 本Presenter是否已经注册了View */
    @Override
    public boolean isViewAttach() {
        return mViewRef != null && mViewRef.get() != null;
    }
}