package lib.grasp.eventbus;

/**
 * Created by netuo_888 on 2020/3/30.
 */
public class Event<T> {
//    EventBus Code, 最好新建一个BizEventCode类专门定义code
//    public static final class EventCode {
//        public static final int A = 0x000001;
//        public static final int B = 0x000002;
//        public static final int C = 0x000003;
//        public static final int D = 0x000004;
//    }

    private int code;
    private T data;

    public Event(int code) {
        this.code = code;
    }

    public Event(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
