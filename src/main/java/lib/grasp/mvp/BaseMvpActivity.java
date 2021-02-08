package lib.grasp.mvp;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rooten.AppHandler;
import com.rooten.interf.IHandler;

/**
 * Activity
 * 基类(MVP)
 */
public abstract class BaseMvpActivity <P extends BaseMvpPresenter> extends AppCompatActivity implements IMvpView, IHandler {

    /**
     * 持有的Presenter的引用
     */
    private P mPresenter;

    /** 主线程执行 */
    protected final Handler mHandler = new AppHandler(this);

    @Nullable
    protected void setPresenter(P presenter){
        mPresenter = presenter;
    }

    protected P getPresenter(){
        return mPresenter;
    }

    /**
     * 在生命周期结束时，将 presenter 与 view 之间的联系断开，防止出现内存泄露
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getPresenter() != null) getPresenter().detachView();
    }

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    @Override
    public AppCompatActivity getHostActivity() {
        return this;
    }


    @Override
    public Handler getHostHandler() {
        return mHandler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
