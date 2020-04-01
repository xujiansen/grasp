package lib.grasp.util;

import org.greenrobot.eventbus.EventBus;

import lib.grasp.eventbus.Event;

/**
 * EventBusUtil
 * <br/>
 * https://www.jianshu.com/p/e00297348f17
 */
public class EventBusUtil {
    /** 注册监听 */
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    /** 解注册监听 */
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    /** 发送事件(封装好) */
    public static void sendEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    /** 发送事件(code + object) */
    public static <T> void sendEvent(int eventCode, T object) {
        EventBus.getDefault().post(new Event<T>(eventCode, object));
    }

    /** 发送Sticky事件(封装好) */
    public static void sendStickyEvent(Event event) {
        EventBus.getDefault().postSticky(event);
    }

    /** 发送Sticky事件(code + object) */
    public static <T> void sendStickyEvent(int eventCode, T object) {
        EventBus.getDefault().postSticky(new Event<T>(eventCode, object));
    }
}
