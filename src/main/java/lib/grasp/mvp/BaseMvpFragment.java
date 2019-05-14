package lib.grasp.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lib.grasp.util.L;

/**
 * View基类(MVP)
 */
public abstract class BaseMvpFragment<P extends IMvpPresenter> extends Fragment implements IMvpView {

    /**
     * 持有的Presenter的引用
     */
    private P mPresenter;

    /**
     * 创建/bind Presenter<br/>
     * 子类需要重写该方法, 返回一个实现IBaseXPresenter接口的Presenter对象(用于完成本View的所有的逻辑业务)
     */
    public abstract P onBindPresenter();

    /**
     * 获取 Presenter 对象，在需要获取时才创建Presenter，起到懒加载作用
     * <br/>一般给自己(View)调用
     */
    public P getPresenter() {
        if (mPresenter == null) mPresenter = onBindPresenter();
        return mPresenter;
    }

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    @Override
    public FragmentActivity getSelfActivity() {
        return getActivity();
    }


    /**
     * 在生命周期结束时，将 presenter 与 view 之间的联系断开，防止出现内存泄露
     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mPresenter != null) mPresenter.detachView();
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter != null) mPresenter.attachView(this);    // 绑定宿主view与presenter
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresenter != null) mPresenter.detachView();        // 解绑宿主view与presenter
    }
}
