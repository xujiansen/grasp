package lib.grasp.mvp;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.rooten.AppHandler;
import com.rooten.interf.IHandler;

/**
 * Fragment
 * 基类(MVP)
 */
public abstract class BaseMvpFragment<P extends IMvpPresenter> extends Fragment implements IMvpView, IHandler {

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

    @Override
    public void onDetach() {
        super.onDetach();
        if (getPresenter() != null) getPresenter().detachView();
    }

    /**
     * 获取Activity对象 / 上下文
     * <br/>一般给Presenter调用
     */
    @Override
    public FragmentActivity getHostActivity() {
        return getActivity();
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
