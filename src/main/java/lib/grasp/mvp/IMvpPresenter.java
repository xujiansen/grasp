package lib.grasp.mvp;

/**
 * Presenter接口<Activity/Fragment>
 */
public interface IMvpPresenter <V extends IMvpView> {

    /**
     *  presenter 与 view 直接的联系
     */
    void attachView(V iMvpView);

    /**
     * 断开 presenter 与 view 直接的联系
     */
    void detachView();

    /**
     * 判断 presenter 是否与 view 建立联系，防止出现内存泄露状况
     *
     * @return {@code true}: 联系已建立<br>{@code false}: 联系已断开
     */
    boolean isViewAttach();
}
