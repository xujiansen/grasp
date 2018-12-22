package cn.com.rooten;

public interface IBackHandled {
    /**
     * @return true就在当前的方法中处理，false往下传递
     */
    boolean onBack();
}
