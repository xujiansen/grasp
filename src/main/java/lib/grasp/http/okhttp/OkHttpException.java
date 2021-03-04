package lib.grasp.http.okhttp;

/**
 * Created by JS_grasp on 2019/6/30.
 */
class OkHttpException extends Exception {

    private int errorType;
    private String errorInfo;

    public OkHttpException(int errorType, String errorInfo) {
        this.errorType = errorType;
        this.errorInfo = errorInfo;
    }

    @Override
    public String toString() {
        return "{" +
                "errorType=" + errorType +
                ", errorInfo='" + errorInfo + '\'' +
                '}';
    }
}
