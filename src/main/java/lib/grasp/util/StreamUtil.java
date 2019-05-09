package lib.grasp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by GaQu_Dev on 2019/5/8.
 */
public class StreamUtil {
    /**
     * 关闭输出流
     */
    public static void closeOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 关闭输入流
     */
    public static void closeInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 关闭输入流
     */
    public static void closeBufferReader(BufferedReader is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 关闭输入流
     */
    public static void closeRandomAccessStream(RandomAccessFile is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }
}
