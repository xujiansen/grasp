package lib.grasp.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * View基类(MVP)
 */
public abstract class  BaseMvpActivity <P extends IMvpPresenter> extends AppCompatActivity implements IMvpView {

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
     * @return Presenter对象
     */
    protected P getPresenter() {
        if (mPresenter == null) mPresenter = onBindPresenter();
        return mPresenter;
    }

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    @Override
    public AppCompatActivity getSelfActivity() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter != null) mPresenter.attachView(this);    // 绑定宿主view与presenter
    }

    /**
     * 在生命周期结束时，将 presenter 与 view 之间的联系断开，防止出现内存泄露
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.detachView();
    }
}
