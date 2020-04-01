package com.rooten.interf;

/** tab的返回监听 */
public interface IBackHandled {
    /**
     * @return true 就在当前的方法中处理, false 往下传递
     */
    boolean onBack();
}
