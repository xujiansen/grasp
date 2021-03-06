package lib.grasp.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/** 方向传感器帮助类 */
public class OrientationHelper implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;

    private float lastX;

    private OnOrientationListener onOrientationListener;

    /** 方向传感器帮助类 */
    public OrientationHelper(Context context, OnOrientationListener listener) {
        this.context = context;
        this.onOrientationListener = listener;

        // 获得传感器管理器
        sensorManager = (SensorManager) context .getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * 开始检测方向传感器
     * <p/>
     * 最好在比如灭屏等情况之下停止检测
     */
    public void start() {
        if (sensorManager != null) {
            // 获得方向传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            // 注册
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    /**
     * 停止检测方向
     * <p/>
     * 最好在比如灭屏等情况之下停止检测
     */
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 接受方向感应器的类型
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // 这里我们可以得到数据，然后根据需要来处理
            float x = event.values[SensorManager.DATA_X];

            if (Math.abs(x - lastX) > 1.0 && onOrientationListener != null) {
                onOrientationListener.onOrientationChanged(x);
            }
            lastX = x;
        }
    }

    /** 注册监听 */
    public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        this.onOrientationListener = onOrientationListener;
    }

    public interface OnOrientationListener {
        /** 设备朝向传感器发生回调 */
        void onOrientationChanged(float x);
    }

    /** 获取上次的角度 */
    public float getAngle() {
        return lastX;
    }
}
