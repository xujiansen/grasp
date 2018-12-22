package lib.grasp.util;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by GaQu_Dev on 2018/10/31.
 */
public class BlueToothUtil {
    /**
     * 打开蓝牙
     */
    public static void openBluetooth() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            btAdapter.enable();
        }
    }

    /**
     * 关闭蓝牙
     */
    public static void closeBluetooth() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && btAdapter.isEnabled()) {
            btAdapter.disable();
        }
    }

}
