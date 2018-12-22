package cn.com.rooten.util;

import android.content.Context;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListAvailableStorage {
    public static List<StorageInfo> listAvailableStorage(Context context) {
        ArrayList<StorageInfo> storageList = new ArrayList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            getVolumeList.setAccessible(true);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);
            if (invokes == null) return storageList;

            for (Object obj : invokes) {
                Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                String path = (String) getPath.invoke(obj, new Object[0]);
                StorageInfo info = new StorageInfo(path);
                File file = new File(info.path);
                if ((file.exists()) && (file.isDirectory()) && (file.canWrite() && file.canRead())) {
                    Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                    try {
                        Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                        info.state = (String) getVolumeState.invoke(storageManager, info.path);
                        if (!info.isMounted()) continue;

                        info.isValid = isStoreCardValid(file.toString());
                        info.isRemovable = (Boolean) isRemovable.invoke(obj, new Object[0]);
                        storageList.add(info);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }

        // 清除预留空间，仅以当前的尺寸
        storageList.trimToSize();
        return storageList;
    }

    private static boolean isStoreCardValid(String path) {
        boolean canMkdirs;
        boolean canDelete = false;

        String testName = UUID.randomUUID().toString();
        String testPath = path + "/" + testName;
        File testFile = new File(testPath);

        canMkdirs = testFile.mkdirs();    // 是否可以创建

        if (canMkdirs && testFile.exists()) {
            canDelete = testFile.delete();    // 是否可以删除
        }

        return canMkdirs && canDelete;
    }
}
