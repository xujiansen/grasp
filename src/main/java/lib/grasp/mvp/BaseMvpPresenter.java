package lib.grasp.mvp;

import androidx.annotation.NonNull;


/**
 * Presenter基类
 */
public abstract class BaseMvpPresenter<V extends IMvpView> implements IMvpPresenter {
    /**
     * 宿主View(一般是Activity/Fragment)<br/>
     * 防止 Activity/Fragment 不走 onDestory() 方法，所以采用弱引用来防止内存泄漏
     */
    protected V mViewRef;

    /** 初始化Presenter(同时需要传入宿主View) */
    public BaseMvpPresenter(@NonNull V view) {
        addView(view);
    }

    /** 初始化Presenter(同时注册View) */
    public void addView(V view) {
        mViewRef = view;
    }

    /** 获取在本Presenter注册的View */
    protected abstract V getView();

    /** 本Presenter是否已经注册了View */
    @Override
    public boolean isViewAttach() {
        return mViewRef != null;
    }

    /** 获取在本Presenter对宿主View的引用 */
    @Override
    public void attachView(IMvpView iMvpView) {
        if (mViewRef == null) {
            mViewRef = (V) iMvpView;
        }
    }

    /** 获取在本Presenter对宿主View的引用 */
    @Override
    public void detachView() {
        if (mViewRef != null) {
//            mViewRef.clear();
            mViewRef = null;
        }
    }
}